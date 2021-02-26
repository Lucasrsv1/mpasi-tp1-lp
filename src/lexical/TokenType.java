package lexical;

public enum TokenType {
    // SPECIALS
    UNEXPECTED_EOF,
    INVALID_TOKEN,
    END_OF_FILE,

    // SYMBOLS
    DOT,           // .
    COMMA,         // ,
    COLON,         // :
    SEMICOLON,     // ;
    OPEN_PAR,      // (
    CLOSE_PAR,     // )

    // OPERATORS
    ASSIGN,        // :=
    EQUAL,         // =
    NOT_EQUAL,     // <>
    LOWER,         // <
    GREATER,       // >
    LOWER_EQ,      // <=
    GREATER_EQ,    // >=
    ADD,           // +
    SUB,           // -
    MUL,           // *
    DIV,           // /
    MOD,           // %

    // KEYWORDS
    PROGRAM,       // program
    CONST,         // const
    VAR,           // var
    BEGIN,         // begin
    END,           // end
    IF,            // if
    THEN,          // then
    ELSE,          // else
    CASE,          // case
    OF,            // of
    WHILE,         // while
    DO,            // do
    REPEAT,        // repeat
    UNTIL,         // until
    FOR,           // for
    TO,            // to
    WRITE,         // write
    WRITELN,       // writeln
    READLN,        // readln
    NOT,           // not
    AND,           // and
    OR,            // or

    // OTHERS
    ID,            // identifier
    INTEGER,       // integer
    REAL,          // real
    STRING,        // string
};
