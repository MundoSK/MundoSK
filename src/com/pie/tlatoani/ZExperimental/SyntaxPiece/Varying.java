package com.pie.tlatoani.ZExperimental.SyntaxPiece;

import com.google.common.collect.ImmutableList;

import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * Created by Tlatoani on 8/21/17.
 */
public class Varying extends SyntaxPiece {
    public final ImmutableList<SyntaxPiece> options;
    public final Optional<String> variable;
    public final boolean isOptional;

    public Varying(ImmutableList<SyntaxPiece> options, Optional<String> variable) {
        this.options = options;
        this.variable = variable;
        isOptional = isOptional();
    }

    public boolean isOptional() {
        return options.get(0).equals(Literal.EMPTY);
    }

    @Override
    public boolean containsVariables() {
        if (variable.isPresent()) {
            return true;
        }
        for (SyntaxPiece syntaxPiece : options) {
            if (syntaxPiece.containsVariables()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public VariableUsage getVariableUsage(String variable) {
        VariableUsage combinedUsage = options
                .stream()
                .map(syntaxPiece -> syntaxPiece.getVariableUsage(variable))
                .reduce(VariableUsage::xor)
                .orElse(VariableUsage.NONE);
        if (this.variable.filter(variable::equals).isPresent()) {
            combinedUsage = VariableUsage.and(combinedUsage, VariableUsage.SPECIFIC);
        }
        return combinedUsage;
        /*VariableUsage highestUsage = VariableUsage.NONE;
        boolean consistentlySpecific = true;
        for (SyntaxPiece option : options) {
            switch (option.getVariableUsage(variable)) {
                case NONE:
                    consistentlySpecific = false;
                    break;
                case SPECIFIC:
                    if (highestUsage == VariableUsage.CONSISTENT) {
                        return VariableUsage.CONFLICTING;
                    } else {
                        highestUsage = consistentlySpecific ? VariableUsage.SPECIFIC : VariableUsage.INCONISTENT;
                        break;
                    }
                case CONSISTENT:
                    if (highestUsage == VariableUsage.NONE) {
                        highestUsage = VariableUsage.CONSISTENT;
                        break;
                    } else {
                        return VariableUsage.CONFLICTING;
                    }
                case CONFLICTING:
                    return VariableUsage.CONFLICTING;
            }
        }
        if (this.variable.filter(variable::equals).isPresent()) {
            if (highestUsage == VariableUsage.NONE) {
                return VariableUsage.SPECIFIC;
            } else {
                return VariableUsage.CONFLICTING;
            }
        }
        return highestUsage;*/
    }

    public static int digitsInBase(int num, int base) {
        int result = 0;
        while (num > 0) {
            num /= base;
            result++;
        }
        return result;
    }

    @Override
    public int markLength() {
        int result = 0;
        for (SyntaxPiece syntaxPiece : options) {
            result = Math.max(result, syntaxPiece.markLength());
        }
        if (containsVariables()) {
            result += digitsInBase(options.size() - 1, 2);
        }
        return result;
    }

    @Override
    public int expressionAmount() {
        int result = 0;
        for (SyntaxPiece syntaxPiece : options) {
            result += syntaxPiece.expressionAmount();
        }
        return result;
    }

    @Override
    public void addVariables(VariableCollective constraints) {
        variable.ifPresent(constraints::addVaryingOption);
        for (SyntaxPiece option : options) {
            option.addVariables(constraints);
        }
    }

    @Override
    public String readableSyntax() {
        StringJoiner joiner = new StringJoiner("|", isOptional ? "[" : "(", isOptional ? "]" : ")");
        for (SyntaxPiece syntaxPiece : options) {
            if (!syntaxPiece.equals(Literal.EMPTY)) {
                joiner.add(syntaxPiece.readableSyntax());
            }
        }
        return joiner.toString();
    }

    @Override
    public String actualSyntax(int prevMarkLength) {
        if (isOptional && options.size() == 2) {
            return "[" + options.get(1).actualSyntax(prevMarkLength) + "]";
        }
        StringJoiner joiner = new StringJoiner("|", isOptional ? "[(" : "(", isOptional ? ")]" : ")");
        //int markLength = markLength();
        int varMarkLength = digitsInBase(options.size() - 1, 2);
        if (containsVariables()) {
            //String markSuffix = String.join("", Collections.nCopies(prevMarkLength, "0"));
            for (int i = isOptional() ? 1 : 0; i < options.size(); i++) {
                joiner.add((i << prevMarkLength) + "¦" + options.get(i).actualSyntax(prevMarkLength + varMarkLength));
            }
        } else {
            for (SyntaxPiece syntaxPiece : options) {
                if (!syntaxPiece.equals(Literal.EMPTY)) {
                    joiner.add(syntaxPiece.actualSyntax(prevMarkLength));
                }
            }
        }
        return joiner.toString();
    }

    @Override
    public String originalSyntax() {
        StringJoiner joiner = new StringJoiner("|", (isOptional ? "[" : "(") + variable.map(name -> name + "=").orElse(""), isOptional ? "]" : ")");
        for (SyntaxPiece syntaxPiece : options) {
            if (!syntaxPiece.equals(Literal.EMPTY)) {
                joiner.add(syntaxPiece.originalSyntax());
            }
        }
        return joiner.toString();
    }

    @Override
    public void setInformation(MarkSpecificInformation information, int mark, int prevExprAmount) {
        int markLength = markLength();
        int optionIndex = mark % (2 ^ markLength);
        int nextMark = mark >> markLength;
        variable.ifPresent(var -> information.markVarValues.put(var, optionIndex));
        for (int i = 0; i < optionIndex; i++) {
            prevExprAmount += options.get(i).expressionAmount();
        }
        options.get(optionIndex).setInformation(information, nextMark, prevExprAmount);
    }

    @Override
    public String toString(int mark) {
        int markLength = markLength();
        if (!containsVariables()) {
            return options.get(0).toString(0);
        }
        int optionIndex = mark % (2 ^ markLength);
        int nextMark = mark >> markLength;
        return options.get(optionIndex).toString(nextMark);
    }

    @Override
    public String toString() {
        return "Varying(" + options.stream().map(SyntaxPiece::toString).collect(Collectors.joining(", ")) + ")";
    }
}
