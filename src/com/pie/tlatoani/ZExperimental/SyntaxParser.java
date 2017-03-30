package com.pie.tlatoani.ZExperimental;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Tlatoani on 3/26/17.
 */
public class SyntaxParser {

    public enum Symbol {
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
    }

    public static abstract class StringSymbolAlternating {
        public final Optional<? extends StringSymbolAlternating> next;

        protected StringSymbolAlternating(Optional<? extends StringSymbolAlternating> next) {
            this.next = next;
        }

        public abstract boolean consume(AlternatingConsumer consumer);
    }

    public final static class FromString extends StringSymbolAlternating {
        public final String string;

        public FromString(String string, Optional<FromSymbol> next) {
            super(next);
            this.string = string;
        }

        @Override
        public boolean consume(AlternatingConsumer consumer) {
            return consumer.consumeString(string, next);
        }
    }

    public final static class FromSymbol extends StringSymbolAlternating {
        public final Symbol symbol;

        public FromSymbol(Symbol symbol, Optional<FromString> next) {
            super(next);
            this.symbol = symbol;
        }

        @Override
        public boolean consume(AlternatingConsumer consumer) {
            return consumer.consumeSymbol(symbol, next);
        }
    }

    public static Optional<? extends StringSymbolAlternating> deconstruct(StringSymbolAlternating alternating, AlternatingConsumer consumer) {
        if (alternating.consume(consumer)) {
            return alternating.next.flatMap(next -> deconstruct(next, consumer));
        }
        return alternating.next;
    }

    public interface AlternatingConsumer {
        boolean consumeString(String string, Optional<? extends StringSymbolAlternating> next);
        boolean consumeSymbol(Symbol symbol, Optional<? extends StringSymbolAlternating> next);
    }

    public static class ConsumeResult {
        public final SyntaxPiece result;
        public final Optional<StringSymbolAlternating> next;
        public final String error;

        private ConsumeResult(SyntaxPiece result, Optional<StringSymbolAlternating> next, String error) {
            this.result = result;
            this.next = next;
            this.error = error;
        }

        public static ConsumeResult success(SyntaxPiece result, Optional<StringSymbolAlternating> next) {
            return new ConsumeResult(result, next, null);
        }

        public static ConsumeResult failure(String error) {
            return new ConsumeResult(null, null, error);
        }

        public boolean successful() {
            return error == null;
        }
    }

    public ConsumeResult untilSymbols(StringSymbolAlternating alternating, Symbol... terminators) {
        final List<SyntaxPiece> syntaxPieces = new ArrayList<SyntaxPiece>();
        final boolean[] terminated = {false};
        AlternatingConsumer consumer = new AlternatingConsumer() {

            @Override
            public boolean consumeString(String string) {
                syntaxPieces.add(new SyntaxPiece.Literal(string));
                return true;
            }

            @Override
            public boolean consumeSymbol(Symbol symbol) {
                for (Symbol terminator : terminators) {
                    if (terminator == symbol) {
                        terminated[0] = true;
                        return false;
                    }
                }
                return true;
            }
        };
        while (!terminated[0]) {

        }
    }
}
