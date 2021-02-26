package syntatic;

import java.io.IOException;
import java.util.ArrayList;

import lexical.Lexeme;
import lexical.TokenType;
import lexical.LexicalAnalysis;
import lexical.LexicalException;

import interpreter.command.*;
import interpreter.expr.*;
import interpreter.value.*;
import interpreter.util.Memory;
import interpreter.RuntimeException;

public class SyntaticAnalysis {
    private Lexeme current;
    private final LexicalAnalysis lex;
    private final boolean debugging;

    public SyntaticAnalysis (LexicalAnalysis lex, boolean debugging) throws LexicalException, IOException {
        this.lex = lex;
        this.current = lex.nextToken();
        this.debugging = debugging;
    }

    public Command start () throws LexicalException, RuntimeException, IOException {
        Command cmd = procProgram();
        eat(TokenType.END_OF_FILE);
        return cmd;
    }

    private void advance () throws LexicalException, IOException {
        if (this.debugging)
            System.out.println("Advanced (\"" + current.token + "\", " + current.type + ")");

        current = lex.nextToken();
    }

    private void eat (TokenType type) throws LexicalException, IOException {
        if (this.debugging)
            System.out.println("Expected (..., " + type + "), found (\"" + current.token + "\", " + current.type + ")");

        if (type == current.type)
            advance();
        else
            showError();
    }

    private void showError () {
        System.out.printf("%02d: ", lex.getLine());

        switch (current.type) {
            case INVALID_TOKEN:
                System.out.printf("Lexema inválido [%s]\n", current.token);
                break;
            case UNEXPECTED_EOF:
            case END_OF_FILE:
                System.out.printf("Fim de arquivo inesperado\n");
                break;
            default:
                System.out.printf("Lexema não esperado [%s]\n", current.token);
                break;
        }

        System.exit(1);
    }

    // <program>  ::= program <id> ';'
    //                [ const <const> { <const> } ]
    //                [ var <var> { <var> } ]
    //                <block> '.'
    private Command procProgram () throws LexicalException, RuntimeException, IOException {
        eat(TokenType.PROGRAM);
        procId();
        eat(TokenType.SEMICOLON);
        
        if (current.type == TokenType.CONST) {
            eat(TokenType.CONST);
            do {
                procConst();
            } while(current.type == TokenType.ID);
        }
        
        if (current.type == TokenType.VAR) {
            eat(TokenType.VAR);
            do {
                procVar();
            } while(current.type == TokenType.ID);
        }
        
        Command cmd = procBlock();
        
        eat(TokenType.DOT);
        return cmd;
    }

    // <const>    ::= <id> = <value> ';'
    private ConstExpr procConst () throws LexicalException, RuntimeException, IOException {
        String id = procId();
        int line = this.lex.getLine();
        
        eat(TokenType.EQUAL);
        
        Value value = procValue();
        eat(TokenType.SEMICOLON);
        
        Memory.registryConstant(id, value);
        return new ConstExpr(line, value);
    }

    // <var>      ::= <id> { ',' <id> } [ = <value> ] ';'
    private ArrayList<Variable> procVar () throws LexicalException, RuntimeException, IOException {
        ArrayList<Variable> vars = new ArrayList<>();
        ArrayList<String> ids = new ArrayList<>();
        ids.add(procId());

        int line = this.lex.getLine();
        while (current.type == TokenType.COMMA) {
            eat(TokenType.COMMA);
            ids.add(procId());
        }
        
        Value value = null;
        if (current.type == TokenType.EQUAL) {
            eat(TokenType.EQUAL);
            value = procValue();
        }

        for (String id : ids) {
            Memory.registryVariable(id, value);
            vars.add(new Variable(line, id));
        }
        
        eat(TokenType.SEMICOLON);
        return vars;
    }

    // <body>     ::= <block> | <cmd>
    private Command procBody () throws LexicalException, RuntimeException, IOException {
        if (current.type == TokenType.BEGIN)
            return procBlock();

        return procCmd();
    }

    // <block>    ::= begin [ <cmd> { ';' <cmd> } ] end
    private BlocksCommand procBlock () throws LexicalException, RuntimeException, IOException {
        eat(TokenType.BEGIN);
        BlocksCommand block = new BlocksCommand(this.lex.getLine());

        if (isCmd()) {
            block.addCommand(procCmd());
            
            while (current.type == TokenType.SEMICOLON) {
                eat(TokenType.SEMICOLON);
                block.addCommand(procCmd());
            }
        }
        
        eat(TokenType.END);
        return block;
    }

    // <cmd>      ::= <assign> | <if> | <case> | <while> | <for> | <repeat> | <write> | <read>
    private Command procCmd () throws LexicalException, RuntimeException, IOException {
        switch (current.type) {
            case ID:
                return procAssign();
            case IF:
                return procIf();
            case CASE:
                return procCase();
            case WHILE:
                return procWhile();
            case FOR:
                return procFor();
            case REPEAT:
                return procRepeat();
            case WRITE:
            case WRITELN:
                return procWrite();
            case READLN:
                return procRead();
            default:
                showError();
                return null;
        }
    }

    // <assign>   ::= <id> := <expr>
    private AssignCommand procAssign () throws LexicalException, RuntimeException, IOException {
        String id = procId();

        eat(TokenType.ASSIGN);
        int line = this.lex.getLine();
        
        Expr expr = procExpr();
        return new AssignCommand(line, Variable.getInstance(id), expr);
    }

    // <if>       ::= if <boolexpr> then <body> [else <body>]
    private IfCommand procIf () throws LexicalException, RuntimeException, IOException {
        eat(TokenType.IF);
        int line = this.lex.getLine();

        BoolExpr cond = procBoolExpr();
        eat(TokenType.THEN);

        IfCommand cmd = new IfCommand(line, cond, procBody());
        if (current.type == TokenType.ELSE) {
            eat(TokenType.ELSE);
            cmd.setElseCommands(procBody());
        }
        
        return cmd;
    }

    // <case>     ::= case <expr> of { <value> : <body> ';' } [ else <body> ';' ] end
    private CaseCommand procCase () throws LexicalException, RuntimeException, IOException {
        eat(TokenType.CASE);
        int line = this.lex.getLine();

        CaseCommand caseCmd = new CaseCommand(line, procExpr());
        eat(TokenType.OF);

        while (isValue()) {
            Value value = procValue();
            eat(TokenType.COLON);
            
            caseCmd.addOption(new CaseCommand.CaseOption(value, procBody()));
            eat(TokenType.SEMICOLON);
        }
        
        if (current.type == TokenType.ELSE) {
            eat(TokenType.ELSE);
            caseCmd.setOtherwise(procBody());
            eat(TokenType.SEMICOLON);
        }
        
        eat(TokenType.END);
        return caseCmd;
    }

    // <while>    ::= while <boolexpr> do <body>
    private WhileCommand procWhile () throws LexicalException, RuntimeException, IOException {
        eat(TokenType.WHILE);
        int line = this.lex.getLine();
        BoolExpr cond = procBoolExpr();

        eat(TokenType.DO);
        return new WhileCommand(line, cond, procBody());
    }

    // <repeat>   ::= repeat [ <cmd> { ';' <cmd> } ] until <boolexpr>
    private RepeatCommand procRepeat () throws LexicalException, RuntimeException, IOException {
        eat(TokenType.REPEAT);
        int line = this.lex.getLine();

        ArrayList<Command> cmds = new ArrayList<>();
        if (isCmd()) {
            cmds.add(procCmd());

            while (current.type == TokenType.SEMICOLON) {
                eat(TokenType.SEMICOLON);
                cmds.add(procCmd());
            }
        }
        
        eat(TokenType.UNTIL);
        return new RepeatCommand(line, cmds, procBoolExpr());
    }

    // <for>      ::= for <id> := <expr> to <expr> do <body>
    private ForCommand procFor () throws LexicalException, RuntimeException, IOException {
        eat(TokenType.FOR);
        int line = this.lex.getLine();
        String id = procId();
        
        eat(TokenType.ASSIGN);
        Expr src = procExpr();
        
        eat(TokenType.TO);
        Expr dst = procExpr();
        
        eat(TokenType.DO);
        return new ForCommand(line, Variable.getInstance(id), src, dst, procBody());
    }

    // <write>    ::= (write | writeln) '(' [ <expr> { ',' <expr> } ] ')'
    private WriteCommand procWrite () throws LexicalException, RuntimeException, IOException {
        boolean writeln = current.type == TokenType.WRITELN;
        if (writeln)
            eat(TokenType.WRITELN);
        else
            eat(TokenType.WRITE);
        
        WriteCommand cmd = new WriteCommand(this.lex.getLine(), writeln);
        eat(TokenType.OPEN_PAR);

        if (isValue() || current.type == TokenType.ID || current.type == TokenType.OPEN_PAR) {
            cmd.addExpr(procExpr());

            while (current.type == TokenType.COMMA) {
                eat(TokenType.COMMA);
                cmd.addExpr(procExpr());
            }
        }
        
        eat(TokenType.CLOSE_PAR);
        return cmd;
    }

    // <read>     ::= readln '(' <id> { ',' <id> } ')'
    private ReadCommand procRead () throws LexicalException, IOException {
        eat(TokenType.READLN);
        ReadCommand cmd = new ReadCommand(this.lex.getLine());
        
        eat(TokenType.OPEN_PAR);
        cmd.addVariable(Variable.getInstance(procId()));
        
        while (current.type == TokenType.COMMA) {
            eat(TokenType.COMMA);
            cmd.addVariable(Variable.getInstance(procId()));
        }

        eat(TokenType.CLOSE_PAR);
        return cmd;
    }
    
    // <boolexpr> ::= [ not ] <cmpexpr> [ (and | or) <boolexpr> ]
    private BoolExpr procBoolExpr () throws LexicalException, RuntimeException, IOException {
        boolean useNot = false;
        int line = this.lex.getLine();
        if (current.type == TokenType.NOT) {
            eat(TokenType.NOT);
            useNot = true;
        }
        
        BoolExpr boolExpr = procCmpExpr();
        if (useNot)
            boolExpr = new NotBoolExpr(line, boolExpr);
        
        if (current.type == TokenType.AND || current.type == TokenType.OR) {
            line = this.lex.getLine();
            CompositeBoolExpr.BoolOp op = current.type == TokenType.AND ? CompositeBoolExpr.BoolOp.And : CompositeBoolExpr.BoolOp.Or;
            eat(current.type);

            boolExpr = new CompositeBoolExpr(line, boolExpr, op, procBoolExpr());
        }

        return boolExpr;
    }

    // <cmpexpr>  ::= <expr> ('=' | '<>' | '<' | '>' | '<=' | '>=') <expr>
    private SingleBoolExpr procCmpExpr () throws LexicalException, RuntimeException, IOException {
        Expr expr = procExpr();
        int line = this.lex.getLine();

        SingleBoolExpr.RelOp op;
        switch (current.type) {
            case EQUAL:
                op = SingleBoolExpr.RelOp.Equal;
                eat(TokenType.EQUAL);
                break;
            case NOT_EQUAL:
                op = SingleBoolExpr.RelOp.NotEqual;
                eat(TokenType.NOT_EQUAL);
                break;
            case LOWER:
                op = SingleBoolExpr.RelOp.LowerThan;
                eat(TokenType.LOWER);
                break;
            case GREATER:
                op = SingleBoolExpr.RelOp.GreaterThan;
                eat(TokenType.GREATER);
                break;
            case LOWER_EQ:
                op = SingleBoolExpr.RelOp.LowerEqual;
                eat(TokenType.LOWER_EQ);
                break;
            case GREATER_EQ:
                op = SingleBoolExpr.RelOp.GreaterEqual;
                eat(TokenType.GREATER_EQ);
                break;
            default:
                showError();
                return null;
        }

        return new SingleBoolExpr(line, expr, op, procExpr());
    }

    // <expr>     ::= <term> { ('+' | '-') <term> }
    private Expr procExpr () throws LexicalException, RuntimeException, IOException {
        int line;
        BinaryExpr.BinaryOp op;

        Expr term = procTerm();
        while (current.type == TokenType.ADD || current.type == TokenType.SUB) {
            line = this.lex.getLine();
            op = current.type == TokenType.ADD ? BinaryExpr.BinaryOp.AddOp : BinaryExpr.BinaryOp.SubOp;

            eat(current.type);
            term = new BinaryExpr(line, term, op, procTerm());
        }

        return term;
    }

    // <term>     ::= <factor> { ('*' | '/' | '%') <factor> }
    private Expr procTerm () throws LexicalException, RuntimeException, IOException {
        int line;
        BinaryExpr.BinaryOp op;

        Expr factor = procFactor();
        while (
            current.type == TokenType.MUL ||
            current.type == TokenType.DIV ||
            current.type == TokenType.MOD
        ) {
            line = this.lex.getLine();
            if (current.type == TokenType.MUL) {
                eat(TokenType.MUL);
                op = BinaryExpr.BinaryOp.MulOp;
            } else if (current.type == TokenType.DIV) {
                eat(TokenType.DIV);
                op = BinaryExpr.BinaryOp.DivOp;
            } else {
                eat(TokenType.MOD);
                op = BinaryExpr.BinaryOp.ModOp;
            }
            
            factor = new BinaryExpr(line, factor, op, procFactor());
        }

        return factor;
    }

    // <factor>   ::= <value> | <id> | '(' <expr> ')'
    private Expr procFactor () throws LexicalException, RuntimeException, IOException {
        Expr expr;
        if (isValue()) {
            expr = new ConstExpr(this.lex.getLine(), procValue());
        } else if (current.type == TokenType.ID) {
            String id = procId();
            expr = Variable.getInstance(id);
            if (expr == null)
                expr = new ConstExpr(this.lex.getLine(), Memory.read(id));
        } else {
            eat(TokenType.OPEN_PAR);
            expr = procExpr();
            eat(TokenType.CLOSE_PAR);
        }
        
        return expr;
    }

    // <value>    ::= <integer> | <real> | <string>
    private Value procValue () throws LexicalException, IOException {
        switch (current.type) {
            case INTEGER:
                return procInteger();
            case REAL:
                return procReal();
            case STRING:
            default:
                return procString();
        }
    }

    private String procId () throws LexicalException, IOException {
        String id = this.current.token;
        eat(TokenType.ID);
        return id;
    }

    private IntegerValue procInteger () throws LexicalException, IOException {
        int num = Integer.parseInt(this.current.token);
        IntegerValue value = new IntegerValue(num);

        eat(TokenType.INTEGER);
        return value;
    }

    private RealValue procReal () throws LexicalException, IOException {
        double num = Double.parseDouble(this.current.token);
        RealValue value = new RealValue(num);

        eat(TokenType.REAL);
        return value;
    }

    private StringValue procString () throws LexicalException, IOException {
        StringValue value = new StringValue(this.current.token);

        eat(TokenType.STRING);
        return value;
    }
    
    private boolean isCmd () {
        return current.type == TokenType.ID ||
               current.type == TokenType.IF ||
               current.type == TokenType.CASE ||
               current.type == TokenType.WHILE ||
               current.type == TokenType.FOR ||
               current.type == TokenType.REPEAT ||
               current.type == TokenType.WRITE ||
               current.type == TokenType.WRITELN ||
               current.type == TokenType.READLN;
    }
    
    private boolean isValue () {
        return current.type == TokenType.INTEGER ||
               current.type == TokenType.REAL ||
               current.type == TokenType.STRING;
    }
}
