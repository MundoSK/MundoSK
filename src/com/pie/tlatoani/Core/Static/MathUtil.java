package com.pie.tlatoani.Core.Static;

import ch.njol.skript.classes.Changer;

/**
 * Created by Tlatoani on 8/10/17.
 */
public class MathUtil {
    public static final String HEX_DIGITS = "0123456789abcdef";

    public static int binaryLog(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("You can only take the logarithm of a positive number, n = " + n);
        }
        return 31 - Integer.numberOfLeadingZeros(n);
    }

    public static int intMod(int number, int mod) {
        return (number % mod) + (number >= 0 ? 0 : mod);
    }

    public static int limitToRange(int min, int num, int max) {
        if (num > max) return max;
        if (num < min) return min;
        return num;
    }

    public static boolean isInRange(double min, double num, double max) {
        return !(num > max || num < min);
    }

    public static char toHexDigit(int num) {
        return HEX_DIGITS.charAt(num % 16);
    }

    public static int digitsInBase(int num, int base) {
        int result = 0;
        while (num > 0) {
            num /= base;
            result++;
        }
        return result;
    }

    public static int change(Changer.ChangeMode mode, int original, int value) {
        switch (mode) {
            case SET: return value;
            case ADD: return original + value;
            case REMOVE: return original - value;
            default: throw new IllegalArgumentException(
                    "The MathUtil.change() method can only be used with SET, ADD, REMOVE ChangeModes, mode = " + mode);
        }
    }
}
