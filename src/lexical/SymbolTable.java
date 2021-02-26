package lexical;

import java.util.Map;
import java.util.HashMap;

public class SymbolTable {
    private final Map<String, TokenType> st;

    public SymbolTable () {
        st = new HashMap<>();

        // SYMBOLS
        st.put(".", TokenType.DOT);
        st.put(",", TokenType.COMMA);
        st.put(":", TokenType.COLON);
        st.put(";", TokenType.SEMICOLON);
        st.put("(", TokenType.OPEN_PAR);
        st.put(")", TokenType.CLOSE_PAR);

        // OPERATORS
        st.put(":=", TokenType.ASSIGN);
        st.put("=", TokenType.EQUAL);
        st.put("<>", TokenType.NOT_EQUAL);
        st.put("<", TokenType.LOWER);
        st.put(">", TokenType.GREATER);
        st.put("<=", TokenType.LOWER_EQ);
        st.put(">=", TokenType.GREATER_EQ);
        st.put("+", TokenType.ADD);
        st.put("-", TokenType.SUB);
        st.put("*", TokenType.MUL);
        st.put("/", TokenType.DIV);
        st.put("%", TokenType.MOD);

        // KEYWORDS
        st.put("program", TokenType.PROGRAM);
        st.put("const", TokenType.CONST);
        st.put("var", TokenType.VAR);
        st.put("begin", TokenType.BEGIN);
        st.put("end", TokenType.END);
        st.put("if", TokenType.IF);
        st.put("then", TokenType.THEN);
        st.put("else", TokenType.ELSE);
        st.put("case", TokenType.CASE);
        st.put("of", TokenType.OF);
        st.put("while", TokenType.WHILE);
        st.put("do", TokenType.DO);
        st.put("repeat", TokenType.REPEAT);
        st.put("until", TokenType.UNTIL);
        st.put("for", TokenType.FOR);
        st.put("to", TokenType.TO);
        st.put("write", TokenType.WRITE);
        st.put("writeln", TokenType.WRITELN);
        st.put("readln", TokenType.READLN);
        st.put("not", TokenType.NOT);
        st.put("and", TokenType.AND);
        st.put("or", TokenType.OR);
    }

    public boolean contains (String token) {
        return st.containsKey(token);
    }

    public TokenType find (String token) {
        return this.contains(token) ? st.get(token) : TokenType.ID;
    }
}
