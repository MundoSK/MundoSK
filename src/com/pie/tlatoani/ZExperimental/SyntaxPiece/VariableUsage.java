package com.pie.tlatoani.ZExperimental.SyntaxPiece;

/**
 * Created by Tlatoani on 8/21/17.
 */
public enum VariableUsage {
    NONE, //Used when a SyntaxPiece does not use a variable name
    INCONISTENT, //Used when a SyntaxPiece may use a variable only if the SyntaxPiece is used
    SPECIFIC, //Used when a SyntaxPiece uses a variable name if and only if the SyntaxPiece is used
    CONSISTENT, //Used when a SyntaxPiece uses a variable name regardless of whether said SyntaxPiece is used
    CONFLICTING; //Used when a SyntaxPiece may use a variable name more than once in some cases

    public static VariableUsage and(VariableUsage usage1, VariableUsage usage2) {
        if (usage1 == NONE) {
            return usage2;
        } else if (usage2 == NONE) {
            return usage1;
        } else {
            return CONFLICTING;
        }
    }

    public static VariableUsage xor(VariableUsage usage1, VariableUsage usage2) {
        if (usage1.ordinal() > usage2.ordinal()) {
            return xor(usage2, usage1);
        } else if (usage1 == NONE) {
            if (usage2 == SPECIFIC) {
                return INCONISTENT;
            } else {
                return usage2;
            }
        } else if (usage1 == INCONISTENT) {
            switch (usage2) {
                case INCONISTENT:
                case SPECIFIC:
                    return INCONISTENT;
                case CONSISTENT:
                case CONFLICTING:
                    return CONFLICTING;
                default: throw new IllegalStateException();
            }
        } else if (usage1 == SPECIFIC) {
            if (usage2 == CONSISTENT) {
                return CONFLICTING;
            } else {
                return usage2;
            }
        } else {
            return CONFLICTING;
        }
    }
}
