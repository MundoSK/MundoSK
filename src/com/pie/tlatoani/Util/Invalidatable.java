package com.pie.tlatoani.Util;

import java.util.Optional;

/**
 * Created by Tlatoani on 3/28/18.
 */
public class Invalidatable<T> {
    private final T value;
    private final Creator.Validity validity;

    private Invalidatable(T value, Creator.Validity validity) {
        if (value == null) {
            throw new IllegalArgumentException("The value of an Invalidatable cannot be null!");
        }
        this.value = value;
        this.validity = validity;
    }

    private static Creator.Validity invalid = null;

    private Invalidatable() {
        if (invalid == null) {
            Creator creator = new Creator();
            invalid = creator.validity;
            creator.invalidate();
        }
        this.value = null;
        this.validity = invalid;
    }

    public static <T> Invalidatable<T> invalid() {
        return new Invalidatable<>();
    }

    public Optional<T> get() {
        return validity.isValid() ? Optional.of(value) : Optional.empty();
    }

    public static class Creator {
        private Validity validity = new Validity();

        public <T> Invalidatable<T> create(T value) {
            return new Invalidatable<T>(value, validity);
        }

        public void invalidate() {
            validity = new Validity();
        }

        class Validity {

            boolean isValid() {
                return validity == this;
            }
        }
    }
}
