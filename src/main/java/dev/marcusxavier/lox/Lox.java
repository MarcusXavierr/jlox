package dev.marcusxavier.lox;

import dev.marcusxavier.lox.scanner.Scanner;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {
    static boolean hadError = false;
    
    public static void main(String @NotNull [] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: jlox [script]");
            System.exit(64);
        }

        if (args.length == 1) {
            runFile(args[0]);
            System.exit(0);
        }

        runPrompt();
    }

    public static void error(int line, String message) {
        report(line, "", message);
    }
   
    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));
      
        if (hadError) System.exit(65);
    }

    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        while (true) {
            System.out.print("> ");
            String line = reader.readLine();
            if (line == null) break;
            run(line);
            hadError = false; 
        }
    }
  
    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        Parser parser = new Parser(tokens);
        Expr expression = parser.parse();
      
        if (hadError) return;
      
        System.out.println(new AstPrinter().print(expression));
    }
  
  
    private static void report(int line, String where, String message) {
        System.err.printf("[Line %s] Error %s: %s%n", line, where, message);
        hadError = true; 
    }
  
    static public void error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.locationLine, " at end", message);
        } else {
            String where = String.format(" at '%s'", token.lexeme);
            report(token.locationLine, where, message);
        }
    }
}