package com.pie.tlatoani.ZExperimental.SyntaxPiece;

import com.google.common.collect.ImmutableList;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Tlatoani on 3/26/17.
 */
public class SyntaxParser {

    public enum Symbol {
        VARIABLE_IDENTIFIER('='),
        EXPRESSION_IDENTIFIER('%'),
        VARYING_OPENER('('),
        VARYING_CLOSER(')'),
        VARYING_SEPARATOR('|'),
        OPTIONAL_OPENER('['),
        OPTIONAL_CLOSER(']');

        public final char chara;

        Symbol(char chara) {
            this.chara = chara;
        }

        public static Optional<Symbol> fromChar(char chara) {
            for (Symbol symbol : values()) {
                if (symbol.chara == chara) {
                    return Optional.of(symbol);
                }
            }
            return Optional.empty();
        }
    }

    public static class ParsingIterator {
        private final String string;
        private int nextIndex = 0;

        public ParsingIterator(String string) {
            this.string = string;
        }

        public boolean hasNext() {
            return nextIndex < string.length();
        }

        public void next(ParsingConsumer consumer) {
            if (string.charAt(nextIndex) == '\\') {
                nextIndex += 2;
                consumer.consumeChar(string.charAt(nextIndex - 1), nextIndex - 1);
            } else {
                nextIndex++;
                Optional<Symbol> symbolOptional = Symbol.fromChar(string.charAt(nextIndex - 1));
                if (symbolOptional.isPresent()) {
                    consumer.consumeSymbol(symbolOptional.get(), nextIndex - 1);
                } else {
                    consumer.consumeChar(string.charAt(nextIndex - 1), nextIndex - 1);
                }
            }
        }
    }

    public interface ParsingConsumer {
        void consumeChar(char chara, int index);
        void consumeSymbol(Symbol symbol, int index);
    }

    public static SyntaxPiece concatenate(List<SyntaxPiece> pieces) {
        if (pieces.size() == 1) {
            return pieces.get(0);
        }
        return new Concatenation(ImmutableList.copyOf(pieces));
    }

    public static SyntaxPiece parse(String string) {
        return new ParsingConsumer() {
            List<SyntaxPiece> syntaxPieces = new LinkedList<>();
            StringBuilder literalBuilder = new StringBuilder();
            ParsingIterator parsingIterator = new ParsingIterator(string);

            public SyntaxPiece parse() {
                while (parsingIterator.hasNext()) {
                    parsingIterator.next(this);
                }
                return concatenate(syntaxPieces);
            }

            @Override
            public void consumeChar(char chara, int index) {
                literalBuilder.append(chara);
            }

            @Override
            public void consumeSymbol(Symbol symbol, int index) {
                if (literalBuilder.length() > 0) {
                    syntaxPieces.add(new Literal(literalBuilder.toString()));
                    literalBuilder = new StringBuilder();
                }
                switch (symbol) {
                    case EXPRESSION_IDENTIFIER:
                        syntaxPieces.add(parseExpression(parsingIterator));
                        break;
                    case VARYING_OPENER:
                    case OPTIONAL_OPENER:
                        //System.out.println("Transitioning to varying, index = " + index);
                        syntaxPieces.add(parseVarying(parsingIterator, symbol == Symbol.OPTIONAL_OPENER));
                        break;
                    default:
                        throw new IllegalArgumentException("Illegal " + symbol + " found, index = " + index);
                }
            }
        }.parse();
    }

    public static Expression parseExpression(ParsingIterator parsingIterator) {
        String variable = new ParsingConsumer() {
            StringBuilder stringBuilder = new StringBuilder();
            boolean continu = true;

            public String extract() {
                while (parsingIterator.hasNext() && continu) {
                    parsingIterator.next(this);
                }
                if (continu) {
                    throw new IllegalArgumentException("Expression never terminated");
                }
                return stringBuilder.toString();
            }

            @Override
            public void consumeChar(char chara, int index) {
                stringBuilder.append(chara);
            }

            @Override
            public void consumeSymbol(Symbol symbol, int index) {
                if (symbol == Symbol.VARIABLE_IDENTIFIER) {
                    continu = false;
                } else {
                    throw new IllegalArgumentException("VARIABLE_IDENTIFIER expected, found: " + symbol + ", index = " + index);
                }
            }
        }.extract();
        String exprInfo = new ParsingConsumer() {
            StringBuilder stringBuilder = new StringBuilder();
            boolean continu = true;

            public String extract() {
                while (parsingIterator.hasNext() && continu) {
                    parsingIterator.next(this);
                }
                if (continu) {
                    throw new IllegalArgumentException("Expression never terminated");
                }
                return stringBuilder.toString();
            }

            @Override
            public void consumeChar(char chara, int index) {
                stringBuilder.append(chara);
            }

            @Override
            public void consumeSymbol(Symbol symbol, int index) {
                if (symbol == Symbol.EXPRESSION_IDENTIFIER) {
                    continu = false;
                } else {
                    throw new IllegalArgumentException("EXPRESSION_IDENTIFIER exptected, found: " + symbol + ", index = " + index);
                }
            }
        }.extract();
        return new Expression(variable, exprInfo);
    }

    public static Varying parseVarying(ParsingIterator parsingIterator, boolean optional) {
        return new ParsingConsumer() {
            List<SyntaxPiece> options = new LinkedList<SyntaxPiece>();
            List<SyntaxPiece> syntaxPieces = new LinkedList<>();
            StringBuilder literalBuilder = new StringBuilder();
            Optional<String> variable = Optional.empty();
            boolean continu = true;

            public Varying parse() {
                while (parsingIterator.hasNext() && continu) {
                    parsingIterator.next(this);
                }
                //System.out.println("Returning from varying, next Index = " + parsingIterator.nextIndex);
                if (continu) {
                    throw new IllegalArgumentException("Varying never terminated");
                }
                if (optional) {
                    options.add(0, Literal.EMPTY);
                }
                return new Varying(ImmutableList.copyOf(options), variable);
            }

            @Override
            public void consumeChar(char chara, int index) {
                literalBuilder.append(chara);
            }

            @Override
            public void consumeSymbol(Symbol symbol, int index) {
                if (literalBuilder.length() > 0 && symbol != Symbol.VARIABLE_IDENTIFIER) {
                    syntaxPieces.add(new Literal(literalBuilder.toString()));
                    literalBuilder = new StringBuilder();
                }
                switch (symbol) {
                    case EXPRESSION_IDENTIFIER:
                        syntaxPieces.add(parseExpression(parsingIterator));
                        break;
                    case VARYING_OPENER:
                    case OPTIONAL_OPENER:
                        syntaxPieces.add(parseVarying(parsingIterator, symbol == Symbol.OPTIONAL_OPENER));
                        break;
                    case VARIABLE_IDENTIFIER:
                        if (variable.isPresent() || !syntaxPieces.isEmpty() || !options.isEmpty()) {
                            throw new IllegalArgumentException("Illegal " + symbol + " found, index = " + index);
                        } else {
                            variable = Optional.of(literalBuilder.toString());
                            literalBuilder = new StringBuilder();
                        }
                        break;
                    case VARYING_SEPARATOR:
                        options.add(concatenate(syntaxPieces));
                        syntaxPieces = new LinkedList<>();
                        break;
                    default:
                        options.add(concatenate(syntaxPieces));
                        if ((symbol == Symbol.OPTIONAL_CLOSER) == optional) {
                            continu = false;
                        } else {
                            throw new IllegalArgumentException("Illegal closer: " + symbol + ", index = " + index);
                        }
                }
            }
        }.parse();
    }

}
