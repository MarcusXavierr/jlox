package dev.marcusxavier.lox.scanner;

import dev.marcusxavier.lox.Lox;
import dev.marcusxavier.lox.Token;
import dev.marcusxavier.lox.TokenType;

import java.util.ArrayList;
import java.util.List;

import static dev.marcusxavier.lox.TokenType.*;

public class Scanner {
    private final ScannerLiterals scannerLiterals;
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1; 

    public Scanner(String source) {
        this.source = source;
        this.scannerLiterals = new ScannerLiterals(this.source);
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }
  
    private void scanToken() {
        char c = advance();

        switch (c) {
            // Single char lexemes
            case '(' -> addToken(LEFT_PAREN);
            case ')' -> addToken(RIGHT_PAREN);
            case '{' -> addToken(LEFT_BRACE);
            case '}' -> addToken(RIGHT_BRACE);
            case ',' -> addToken(COMMA);
            case '.' -> addToken(DOT);
            case '-' -> addToken(MINUS);
            case '+' -> addToken(PLUS);
            case ';' -> addToken(SEMICOLON);
            case '*' -> addToken(STAR);


            // Double char lexemes
            case '!' -> addToken(match('=') ? BANG_EQUAL : BANG);
            case '=' -> addToken(match('=') ? EQUAL_EQUAL : EQUAL);
            case '<' -> addToken(match('=') ? LESS_EQUAL : LESS);
            case '>' -> addToken(match('=') ? GREATER_EQUAL : GREATER);
            case '/' -> {
                if (match('/')) {
                    // Advance until comment is gone
                    while (peek() != '\n' && !isAtEnd()) advance();
                } else {
                    addToken(SLASH);
                }
            }

            // Ignore whitespace
            case ' ', '\r', '\t' -> {}
            // Count lines
            case '\n' -> line++;
            
            // Treat string literals 
            case '"' -> string();
            
            default -> {
               if (TypeValidator.isDigit(c)) {
                   number();
               } else if (TypeValidator.isAlpha(c)){ 
                   identifier();        
               } else {
                   Lox.error(line, "Unexpected character.");
               }
            }
        }
    } 
  
    private void string() {
        LiteralData data = scannerLiterals.string(start, current, line);
        updateState(data);
    }
  
    private void number() {
        LiteralData data = scannerLiterals.number(start, current, line);
        updateState(data); 
    }
  
    private void identifier() {
        LiteralData data = scannerLiterals.identifier(start, current, line);
        updateState(data); 
    }
  
    private void updateState(LiteralData data) {
        data.token().ifPresent(tokens::add);
        current = data.current();
        line = data.line();
    }
    
    private char advance() {
        char c = source.charAt(current);
        current++; 
        return c; 
    }
  
    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }
  
    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (peek() != expected) return false;
        
        current++;
        return true;
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }
    
    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }  
}