package com.pie.tlatoani.Util.Static;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by Tlatoani on 3/17/18.
 */
public class OptionalUtil {

    public static <T> void consume(Optional<T> optional, Runnable runnable, Consumer<T> tConsumer) {
        if (optional.isPresent()) {
            tConsumer.accept(optional.get());
        } else {
            runnable.run();
        }
    }

    public static <T, R> R map(Optional<T> optional, Supplier<R> supplier, Function<T, R> function) {
        if (optional.isPresent()) {
            return function.apply(optional.get());
        } else {
            return supplier.get();
        }

    }

    public static <S, T extends S> Optional<T> cast(S obj, Class<T> tClass) {
        if (tClass.isInstance(obj)) {
            return Optional.of((T) obj);
        } else {
            return Optional.empty();
        }
    }

    public static <T> boolean equal(@Nullable T t, Optional<T> optional) {
        if (optional == null) {
            throw new IllegalArgumentException("The optional argument should not be null");
        } else if (t == null) {
            return !optional.isPresent();
        } else {
            return optional.map(t::equals).orElse(false);
        }
    }

    public static <T> boolean referencesEqual(@Nullable T t, Optional<T> optional) {
        if (optional == null) {
            throw new IllegalArgumentException("The optional argument should not be null");
        } else if (t == null) {
            return !optional.isPresent();
        } else {
            return optional.map(val -> t == val).orElse(false);
        }
    }
}