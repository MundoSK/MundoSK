package com.pie.tlatoani.ZExperimental.SyntaxPiece;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
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
        private String string;

        public ParsingIterator(String string) {
            this.string = string;
        }

        public boolean hasNext() {
            return !string.isEmpty();
        }

        public void next(ParsingConsumer consumer) {
            if (string.charAt(0) == '\\') {
                consumer.consumeChar(string.charAt(1));
                string = string.substring(2);
            } else {
                Optional<Symbol> symbolOptional = Symbol.fromChar(string.charAt(0));
                if (symbolOptional.isPresent()) {
                    consumer.consumeSymbol(symbolOptional.get());
                } else {
                    consumer.consumeChar(string.charAt(0));
                }
                string = string.substring(1);
            }
        }
    }

    public interface ParsingConsumer {
        void consumeChar(char chara);
        void consumeSymbol(Symbol symbol);
    }

    public static SyntaxPiece concatenate(List<SyntaxPiece> pieces) {
        if (pieces.size() == 1) {
            return pieces.get(0);
        }
        return new Concatenation(ImmutableList.copyOf(pieces));
    }

    public static SyntaxPiece parse(String string) {
        return new ParsingConsumer() {
            ArrayList<SyntaxPiece> syntaxPieces = new ArrayList<>();
            StringBuilder literalBuilder = new StringBuilder();
            ParsingIterator parsingIterator = new ParsingIterator(string);

            public SyntaxPiece parse() {
                while (parsingIterator.hasNext()) {
                    parsingIterator.next(this);
                }
                return concatenate(syntaxPieces);
            }

            @Override
            public void consumeChar(char chara) {
                literalBuilder.append(chara);
            }

            @Override
            public void consumeSymbol(Symbol symbol) {
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
                        syntaxPieces.add(parseVarying(parsingIterator, symbol == Symbol.OPTIONAL_OPENER));
                        break;
                    default:
                        throw new IllegalArgumentException("Illegal " + symbol + " found");
                }
            }
        }.parse();
    }

    public static Expression parseExpression(ParsingIterator parsingIterator) {
        String variable = new ParsingConsumer() {
            StringBuilder stringBuilder = new StringBuilder();
            boolean continu = true;

            public String extract() {
                while (continu) {
                    parsingIterator.next(this);
                }
                return stringBuilder.toString();
            }

            @Override
            public void consumeChar(char chara) {
                stringBuilder.append(chara);
            }

            @Override
            public void consumeSymbol(Symbol symbol) {
                if (symbol == Symbol.VARIABLE_IDENTIFIER) {
                    continu = false;
                } else {
                    throw new IllegalArgumentException("Baaaad");
                }
            }
        }.extract();
        String exprInfo = new ParsingConsumer() {
            StringBuilder stringBuilder = new StringBuilder();
            boolean continu = true;

            public String extract() {
                while (continu) {
                    parsingIterator.next(this);
                }
                return stringBuilder.toString();
            }

            @Override
            public void consumeChar(char chara) {
                stringBuilder.append(chara);
            }

            @Override
            public void consumeSymbol(Symbol symbol) {
                if (symbol == Symbol.EXPRESSION_IDENTIFIER) {
                    continu = false;
                } else {
                    throw new IllegalArgumentException("Baaaad");
                }
            }
        }.extract();
        return new Expression(variable, exprInfo);
    }

    public static Varying parseVarying(ParsingIterator parsingIterator, boolean optional) {
        return new ParsingConsumer() {
            ArrayList<SyntaxPiece> options = new ArrayList<SyntaxPiece>();
            ArrayList<SyntaxPiece> syntaxPieces = new ArrayList<>();
            StringBuilder literalBuilder = new StringBuilder();
            Optional<String> variable = Optional.empty();
            boolean continu;

            public Varying parse() {
                if (optional) {
                    options.add(Literal.EMPTY);
                }
                while (parsingIterator.hasNext() && continu) {
                    parsingIterator.next(this);
                }
                if (continu) {
                    throw new IllegalArgumentException("Varying never terminated");
                }
                return new Varying(ImmutableList.copyOf(options), variable);
            }

            @Override
            public void consumeChar(char chara) {
                literalBuilder.append(chara);
            }

            @Override
            public void consumeSymbol(Symbol symbol) {
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
                            throw new IllegalArgumentException("Illegal " + symbol + "found");
                        } else {
                            variable = Optional.of(literalBuilder.toString());
                            literalBuilder = new StringBuilder();
                        }
                        break;
                    case VARYING_SEPARATOR:
                        options.add(concatenate(syntaxPieces));
                        syntaxPieces = new ArrayList<>();
                        break;
                    default:
                        options.add(concatenate(syntaxPieces));
                        if ((symbol == Symbol.OPTIONAL_CLOSER) == optional) {
                            continu = false;
                        } else {
                            throw new IllegalArgumentException("Illegal closer: " + symbol);
                        }
                }
            }
        }.parse();
    }

}
