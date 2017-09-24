package com.pie.tlatoani.ZExperimental.SyntaxPiece;

import java.util.Set;

/**
 * Created by Tlatoani on 8/21/17.
 */
public class Literal extends SyntaxPiece {
    public final String text;

    public static final com.pie.tlatoani.ZExperimental.SyntaxPiece.Literal EMPTY = new com.pie.tlatoani.ZExperimental.SyntaxPiece.Literal("");

    public Literal(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof com.pie.tlatoani.ZExperimental.SyntaxPiece.Literal && text.equals(((com.pie.tlatoani.ZExperimental.SyntaxPiece.Literal) object).text);
    }

    @Override
    public boolean containsVariables() {
        return false;
    }

    @Override
    public void addVariableNames(Set<String> set) {
        //no variables to add
    }

    @Override
    public VariableUsage getVariableUsage(String variable) {
        return VariableUsage.NONE;
    }

    @Override
    public int markLength() {
        return 0;
    }

    @Override
    public int expressionAmount() {
        return 0;
    }

    @Override
    public void setConstraints(ExpressionConstraints.Collective constraints) {
    }

    @Override
    public String readableSyntax() {
        return text;
    }

    @Override
    public String actualSyntax(int prevMarkLength) {
        return text;
    }

    @Override
    public String originalSyntax() {
        return text;
    }

    @Override
    public void setInformation(MarkSpecificInformation information, int mark, int prevExprAmount) {
    }

    @Override
    public String toString(int mark) {
        return text;
    }

    @Override
    public String toString() {
        return "Literal(\"" + text + "\")";
    }
}
