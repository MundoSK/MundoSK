package com.pie.tlatoani.Util;

import com.pie.tlatoani.Core.Static.MathUtil;

public class IntUsage {
    private final int amount;
    private final int[] array;

    public IntUsage(int amount) {
        this.amount = amount;
        this.array = new int[(amount + 29) / 30];
        for (int i = 0; i < array.length; i++) {
            array[i] = Integer.MAX_VALUE - 1;
        }
    }

    public boolean isUsed(int n) {
        if (!MathUtil.isInRange(1, n, amount)) {
            throw new IllegalArgumentException("n = " + n + ", should be within range 1 to " + amount + " (inclusive)");
        }
        return (array[(n - 1) / 30] & (1 << n)) == 0;
    }

    public void setUsed(int n) {
        if (!MathUtil.isInRange(1, n, amount)) {
            throw new IllegalArgumentException("n = " + n + ", should be within range 1 to " + amount + " (inclusive)");
        }
        if (!isUsed(n)) {
            array[(n - 1) / 30] -= 1 << n;
        }
    }

    public void setUnused(int n) {
        if (!MathUtil.isInRange(1, n, amount)) {
            throw new IllegalArgumentException("n = " + n + ", should be within range 1 to " + amount + " (inclusive)");
        }
        array[(n - 1) / 30] |= 1 << n;
    }

    public int getFirstUnused() {
        for (int i = 0; i < array.length; i++) {
            if (array[i] != 0) {
                int result =  MathUtil.binaryLog(array[i] & (-array[i]));
                if (result <= amount) {
                    return result;
                } else {
                    break;
                }
            }
        }
        return -1;
    }

    public int useFirstUnused() {
        int result = getFirstUnused();
        if (result != -1) {
            setUsed(result);
        }
        return result;
    }
}
