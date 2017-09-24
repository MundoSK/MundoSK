package com.pie.tlatoani.ZExperimental.SyntaxPiece;

import java.util.Set;

/**
 * Created by Tlatoani on 8/21/17.
 */
public class Expression extends SyntaxPiece {
    public final String variable;
    public final String exprInfo;
    public final ExpressionConstraints constraints;

    public Expression(String variable, String exprInfo) {
        this.variable = variable;
        this.exprInfo = exprInfo;
        this.constraints = new ExpressionConstraints(exprInfo);
    }

    @Override
    public boolean containsVariables() {
        return true;
    }

    @Override
    public void addVariableNames(Set<String> set) {
        set.add(variable);
    }

    @Override
    public VariableUsage getVariableUsage(String variable) {
        return this.variable.equals(variable)
                ? (constraints.nullable
                        ? VariableUsage.CONSISTENT
                        : VariableUsage.SPECIFIC)
                : VariableUsage.NONE;
    }

    @Override
    public int markLength() {
        return 0;
    }

    @Override
    public int expressionAmount() {
        return 1;
    }

    @Override
    public void setConstraints(ExpressionConstraints.Collective constraints) {
        constraints.addConstraints(variable, this.constraints);
    }

    @Override
    public String readableSyntax() {
        return "%" + exprInfo + "%"; //Might want to remove -, *, etc.
    }

    @Override
    public String actualSyntax(int prevMarkLength) {
        return "%" + exprInfo + "%";
    }

    @Override
    public String originalSyntax() {
        return "%" + variable + "=" + exprInfo + "%";
    }

    @Override
    public void setInformation(MarkSpecificInformation information, int mark, int prevExprAmount) {
        information.exprIndexes.put(variable, prevExprAmount);
    }

    @Override
    public String toString(int mark) {
        return "%" + variable + "%";
    }

    @Override
    public String toString() {
        return "Expression(\"" + variable + "\", \"" + exprInfo + "\")";
    }
}
