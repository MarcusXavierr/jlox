package dev.marcusxavier.lox.scanner;

import dev.marcusxavier.lox.Lox;
import dev.marcusxavier.lox.Token;
import dev.marcusxavier.lox.TokenType;

import java.util.Optional;

class ScannerLiterals {

    private final String source;

    ScannerLiterals(String source) {
        this.source = source;
    }

    public LiteralData string(int start, int current, int line) {
        while (peek(current) != '"' && !isAtEnd(current)) {
            if (peek(current) == '\n') {
                line++;
            }
            current++;
        }

        if (isAtEnd(current)) {
            Lox.error(line, "Unterminated string.");
            return new LiteralData(Optional.empty(), current, line);
        }

        // The closing "
        current++;

        String value = source.substring(start + 1, current - 1);
        String text = source.substring(start, current);
        var literal = Optional.of(new Token(TokenType.STRING, text, value, line));
        return new LiteralData(literal, current, line);
    }

    public LiteralData number(int start, int current, int line) {
        while (TypeValidator.isDigit(peek(current))) current++;

        if (peek(current) == '.' && TypeValidator.isDigit(peekNext(current)))  {
            do {
                current++;
            }
            while (TypeValidator.isDigit(peek(current)));
        }

        String text = source.substring(start, current);
        Double num = Double.parseDouble(text);
        Token token = new Token(TokenType.NUMBER, text, num, line);

        return new LiteralData(Optional.of(token), current, line);
    }


    public LiteralData identifier(int start, int current, int line) {
        while(TypeValidator.isAlphaNumeric(peek(current))) current++;

        String text = source.substring(start, current);
        TokenType type = ScannerKeywords.keywords.get(text);
        if (type == null) {
            type = TokenType.IDENTIFIER;
        }

        Token token = new Token(type, text, null, line);
        return new LiteralData(Optional.of(token), current, line);
    }

    private boolean isAtEnd(int current) {
        return current >= source.length();
    }

    private char peek(int current) {
        if (isAtEnd(current)) return '\0';
        return source.charAt(current);
    }

    private char peekNext(int current) {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }
}
