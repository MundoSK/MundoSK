package com.pie.tlatoani.ZExperimental.SyntaxPiece;

import com.google.common.collect.ImmutableList;

import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * Created by Tlatoani on 8/21/17.
 */
public class Concatenation extends SyntaxPiece {
    public final ImmutableList<SyntaxPiece> pieces;

    public Concatenation(ImmutableList<SyntaxPiece> pieces) {
        this.pieces = pieces;
    }

    @Override
    public boolean containsVariables() {
        for (SyntaxPiece syntaxPiece : pieces) {
            if (syntaxPiece.containsVariables()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void addVariableNames(Set<String> set) {
        for (SyntaxPiece piece : pieces) {
            piece.addVariableNames(set);
        }
    }

    @Override
    public VariableUsage getVariableUsage(String variable) {
        VariableUsage highestUsage = VariableUsage.NONE;
        for (SyntaxPiece piece : pieces) {
            switch (piece.getVariableUsage(variable)) {
                case NONE:
                    break;
                case SPECIFIC:
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
        return highestUsage;
    }

    @Override
    public int markLength() {
        int result = 0;
        for (SyntaxPiece syntaxPiece : pieces) {
            result += syntaxPiece.markLength();
        }
        return result;
    }

    @Override
    public int expressionAmount() {
        int result = 0;
        for (SyntaxPiece syntaxPiece : pieces) {
            result += syntaxPiece.expressionAmount();
        }
        return result;
    }

    @Override
    public void setConstraints(ExpressionConstraints.Collective constraints) {
        for (SyntaxPiece piece : pieces) {
            piece.setConstraints(constraints);
        }
    }

    @Override
    public String readableSyntax() {
        StringJoiner joiner = new StringJoiner("");
        for (SyntaxPiece syntaxPiece : pieces) {
            joiner.add(syntaxPiece.readableSyntax());
        }
        return joiner.toString();
    }

    @Override
    public String actualSyntax(int prevMarkLength) {
        StringJoiner joiner = new StringJoiner("");
        for (SyntaxPiece syntaxPiece : pieces) {
            joiner.add(syntaxPiece.actualSyntax(prevMarkLength));
            prevMarkLength += syntaxPiece.markLength();
        }
        return joiner.toString();
    }

    @Override
    public String originalSyntax() {
        StringJoiner joiner = new StringJoiner("");
        for (SyntaxPiece syntaxPiece : pieces) {
            joiner.add(syntaxPiece.originalSyntax());
        }
        return joiner.toString();
    }

    @Override
    public void setInformation(MarkSpecificInformation information, int mark, int prevExprAmount) {
        for (SyntaxPiece syntaxPiece : pieces) {
            int markLength = syntaxPiece.markLength();
            syntaxPiece.setInformation(information, mark % (2 ^ markLength), prevExprAmount);
            mark >>= markLength;
            prevExprAmount += syntaxPiece.expressionAmount();
        }
    }

    @Override
    public String toString(int mark) {
        String result = "";
        for (SyntaxPiece syntaxPiece : pieces) {
            int markLength = syntaxPiece.markLength();
            String curr = syntaxPiece.toString(mark % (2 ^ markLength));
            if (result.length() > 0 && result.charAt(result.length() - 1) == ' ' && curr.length() > 0 && curr.charAt(0) == ' ') {
                curr = curr.substring(1);
            }
            result += curr;
            mark >>= markLength;
        }
        return result;
    }

    @Override
    public String toString() {
        return "Concatenation(" + pieces.stream().map(SyntaxPiece::toString).collect(Collectors.joining(", ")) + ")";
    }
}
