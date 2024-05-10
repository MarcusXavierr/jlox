package dev.marcusxavier.lox;

public class Token {
    final TokenType type;
    final String lexeme;
    final Object literal;
    // could add column and length too, to get better syntax error messages
    // Or add offset, as suggested here https://craftinginterpreters.com/scanning.html#location-information 
    final int locationLine;

    public Token(TokenType type, String lexeme, Object literal, int locationLine) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.locationLine = locationLine;
    }
  
    public String toString() {
        return String.format("%s %s %s", type, lexeme, literal);
    }
}
