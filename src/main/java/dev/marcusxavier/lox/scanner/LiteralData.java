package dev.marcusxavier.lox.scanner;

import dev.marcusxavier.lox.Token;

import java.util.Optional;

record LiteralData(Optional<Token> token, int current, int line)  {
}
