import lexical.Lexeme;
import lexical.LexicalAnalysis;
import lexical.TokenType;
import lexical.LexicalException;

import syntatic.SyntaticAnalysis;

import interpreter.command.Command;
import interpreter.RuntimeException;

public class Mpasi {
    public static final boolean DEBUGGING = false;

    public static void main (String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java mpasi [miniPascal file]");
            return;
        }
        
        // O código a seguir é usado apenas para testar o analisador léxico.
        if (DEBUGGING)
            lexicalTest(args);

        try (LexicalAnalysis l = new LexicalAnalysis(args[0])) {
            SyntaticAnalysis s = new SyntaticAnalysis(l, DEBUGGING);
            Command c = s.start();
            c.execute();
        } catch (LexicalException | RuntimeException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.err.println("Internal error: " + e.getMessage());
            for (StackTraceElement x : e.getStackTrace())
                System.err.println(x.toString());
        }
    }

    private static boolean checkType (TokenType type) {
        return !(type == TokenType.END_OF_FILE ||
                 type == TokenType.INVALID_TOKEN ||
                 type == TokenType.UNEXPECTED_EOF);
    }
    
    private static void lexicalTest (String[] args) {
        try (LexicalAnalysis l = new LexicalAnalysis(args[0])) {
            Lexeme lex = l.nextToken();
            while (checkType(lex.type)) {
                System.out.printf("(\"%s\", %s)\n", lex.token, lex.type);
                lex = l.nextToken();
            }

            switch (lex.type) {
                case INVALID_TOKEN:
                    System.out.printf("%02d: Lexema inválido [%s]\n", l.getLine(), lex.token);
                    break;
                case UNEXPECTED_EOF:
                    System.out.printf("%02d: Fim de arquivo inesperado\n", l.getLine());
                    break;
                default:
                    System.out.printf("(\"%s\", %s)\n", lex.token, lex.type);
                    break;
            }
        } catch (Exception e) {
            System.err.println("Internal error: " + e.getMessage());
            for (StackTraceElement x : e.getStackTrace())
                System.err.println(x.toString());
        }
    }
}
