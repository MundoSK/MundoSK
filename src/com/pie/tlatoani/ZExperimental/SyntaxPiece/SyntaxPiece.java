package com.pie.tlatoani.ZExperimental.SyntaxPiece;

import java.util.*;

/**
 * Created by Tlatoani on 3/24/17.
 */
public abstract class SyntaxPiece {

    SyntaxPiece() {}

    public abstract boolean containsVariables();

    public abstract VariableUsage getVariableUsage(String variable);

    public abstract int markLength();

    public abstract int expressionAmount();

    public VariableCollective getVariables() {
        VariableCollective collective = new VariableCollective();
        addVariables(collective);
        return collective;
    }

    public abstract void addVariables(VariableCollective collective);

    public abstract String readableSyntax();

    public abstract String actualSyntax(int prevMarkLength);

    public abstract String originalSyntax();

    public abstract void setInformation(MarkSpecificInformation information, int mark, int prevExprAmount);

    public abstract String toString(int mark);

    public abstract String toString();

    public static class MarkSpecificInformation {
        public final Map<String, Integer> exprIndexes = new HashMap<>();
        public final Map<String, Integer> markVarValues = new HashMap<>();
    }

}
