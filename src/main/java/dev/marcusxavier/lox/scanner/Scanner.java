package dev.marcusxavier.lox.scanner;

import dev.marcusxavier.lox.Lox;
import dev.marcusxavier.lox.Token;
import dev.marcusxavier.lox.TokenType;

import java.util.ArrayList;
import java.util.List;

import static dev.marcusxavier.lox.TokenType.*;

public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1; 

    public Scanner(String source) {
        this.source = source;
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
               if (isDigit(c)) {
                   number();
               } else if (isAlpha(c)){ 
                   identifier();        
               } else {
                   Lox.error(line, "Unexpected character.");
               }
            }
        }
    } 
  
    // TODO: Refactor this literal functions so this can go to another class, but it messes with state.
    // Maybe the state can go there
    private void string() {
        while (peek() != '"' && !isAtEnd()) {
           if (peek() == '\n') {
               line++;
           }
           advance(); 
        }
       
        if (isAtEnd()) {
            Lox.error(line, "Unterminated string.");
            return;
        }
      
        // The closing "
        advance();
      
        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value); 
    }
  
    private void number() {
        while (isDigit(peek())) advance(); 
        
        if (peek() == '.' && isDigit(peekNext()))  {
            do {
                advance();
            }
            while (isDigit(peek()));
        }
        
        Double num = Double.parseDouble(source.substring(start, current));
        addToken(NUMBER, num);
    }
  
    private void identifier() {
        while(isAlphaNumeric(peek())) advance();
      
        String text = source.substring(start, current);
        TokenType type = ScannerKeywords.keywords.get(text);
        if (type == null) {
            type = IDENTIFIER;
        }
        addToken(type);
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
    
    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }
    
    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }
    
    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }  
  
    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1); 
    }
}
