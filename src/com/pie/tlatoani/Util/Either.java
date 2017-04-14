package com.pie.tlatoani.Util;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by Tlatoani on 4/13/17.
 */
public interface Either<L, R> {
    void consume(Consumer<L> leftConsumer, Consumer<R> rightConsumer);

    <T> T map(Function<L, T> leftFunction, Function<R, T> rightFunction);

    class Left<L, R> implements Either<L, R> {
        public final L value;

        public Left(L value) {
            this.value = value;
        }

        @Override
        public void consume(Consumer<L> leftConsumer, Consumer<R> rightConsumer) {
            leftConsumer.accept(value);
        }

        @Override
        public <T> T map(Function<L, T> leftFunction, Function<R, T> rightFunction) {
            return leftFunction.apply(value);
        }
    }

    class Right<L, R> implements Either<L, R> {
        public final R value;

        public Right(R value) {
            this.value = value;
        }

        @Override
        public void consume(Consumer<L> leftConsumer, Consumer<R> rightConsumer) {
            rightConsumer.accept(value);
        }

        @Override
        public <T> T map(Function<L, T> leftFunction, Function<R, T> rightFunction) {
            return rightFunction.apply(value);
        }
    }
}
