package com.pie.tlatoani.ZExperimental;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.pie.tlatoani.Mundo;

import java.util.*;

/**
 * Created by Tlatoani on 3/24/17.
 */
public abstract class SyntaxPiece {
    public final boolean containsVariables = containsVariables();
    public final int markLength = markLength();
    public final int expressionAmount = expressionAmount();

    public abstract boolean containsVariables();

    public abstract int markLength();

    public abstract int expressionAmount();

    public abstract void setConstraints(ExpressionConstraints.Collective constraints);

    public abstract String readableSyntax();

    public abstract String actualSyntax(int prevMarkLength);

    public abstract void setInformation(MarkSpecificInformation information, int mark, int prevExprAmount);

    public abstract String toString(int mark);

    public static class MarkSpecificInformation {
        public final Map<String, Integer> exprIndexes = new HashMap<>();
        public final Map<String, Integer> markVarValues = new HashMap<>();
    }

    public static class Literal extends SyntaxPiece {
        public final String text;

        public static final Literal EMPTY = new Literal("");

        public Literal(String text) {
            this.text = text;
        }

        @Override
        public boolean equals(Object object) {
            return object instanceof Literal && text.equals(((Literal) object).text);
        }

        @Override
        public boolean containsVariables() {
            return false;
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
        public void setConstraints(ExpressionConstraints.Collective constraints) {}

        @Override
        public String readableSyntax() {
            return text;
        }

        @Override
        public String actualSyntax(int prevMarkLength) {
            return text;
        }

        @Override
        public void setInformation(MarkSpecificInformation information, int mark, int prevExprAmount) {}

        @Override
        public String toString(int mark) {
            return text;
        }
    }

    public static class Expression extends SyntaxPiece {
        public final String variable;
        public final String exprInfo;
        public final ExpressionConstraints constraints;

        public Expression(String variable, String exprInfo) {
            this.variable = variable;
            this.exprInfo = exprInfo;
            this.constraints = ExpressionConstraints.fromString(exprInfo);
        }

        @Override
        public boolean containsVariables() {
            return true;
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
        public void setInformation(MarkSpecificInformation information, int mark, int prevExprAmount) {
            information.exprIndexes.put(variable, prevExprAmount);
        }

        @Override
        public String toString(int mark) {
            return "%" + variable + "%";
        }
    }

    public static class Varying extends SyntaxPiece {
        public final ImmutableList<SyntaxPiece> options;
        public final Optional<String> variable;
        public final boolean isOptional = isOptional();

        public Varying(ImmutableList<SyntaxPiece> options, Optional<String> variable) {
            this.options = options;
            this.variable = variable;
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
                if (syntaxPiece.containsVariables) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public int markLength() {
            int result = 0;
            for (SyntaxPiece syntaxPiece : options) {
                result = Math.max(result, syntaxPiece.markLength());
            }
            if (containsVariables()) {
                result += Mundo.digitsInBase(options.size() - 1, 2);
            }
            return result;
        }

        @Override
        public int expressionAmount() {
            int result = 0;
            for (SyntaxPiece syntaxPiece : options) {
                result += syntaxPiece.expressionAmount;
            }
            return result;
        }

        @Override
        public void setConstraints(ExpressionConstraints.Collective constraints) {
            for (SyntaxPiece option : options) {
                option.setConstraints(constraints);
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
            if (containsVariables()) {
                String markSuffix = String.join("", Collections.nCopies(prevMarkLength, "0"));
                for (int i = isOptional() ? 1 : 0; i < options.size(); i++) {
                    joiner.add(i + markSuffix + "¦" + options.get(i).actualSyntax(prevMarkLength + markLength));
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
        public void setInformation(MarkSpecificInformation information, int mark, int prevExprAmount) {
            int optionIndex = mark % (2 ^ markLength);
            int nextMark = mark >> markLength;
            variable.ifPresent(var -> information.markVarValues.put(var, optionIndex));
            for (int i = 0; i < optionIndex; i++) {
                prevExprAmount += options.get(i).expressionAmount;
            }
            options.get(optionIndex).setInformation(information, nextMark, prevExprAmount);
        }

        @Override
        public String toString(int mark) {
            if (!containsVariables()) {
                return options.get(0).toString(0);
            }
            int optionIndex = mark % (2 ^ markLength);
            int nextMark = mark >> markLength;
            return options.get(optionIndex).toString(nextMark);
        }
    }

    public static class Concatenation extends SyntaxPiece {
        public final ImmutableList<SyntaxPiece> pieces;

        public Concatenation(ImmutableList<SyntaxPiece> pieces) {
            this.pieces = pieces;
        }

        @Override
        public boolean containsVariables() {
            for (SyntaxPiece syntaxPiece : pieces) {
                if (syntaxPiece.containsVariables) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public int markLength() {
            int result = 0;
            for (SyntaxPiece syntaxPiece : pieces) {
                result += syntaxPiece.markLength;
            }
            return result;
        }

        @Override
        public int expressionAmount() {
            int result = 0;
            for (SyntaxPiece syntaxPiece : pieces) {
                result += syntaxPiece.expressionAmount;
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
                prevMarkLength += syntaxPiece.markLength;
            }
            return joiner.toString();
        }

        @Override
        public void setInformation(MarkSpecificInformation information, int mark, int prevExprAmount) {
            for (SyntaxPiece syntaxPiece : pieces) {
                syntaxPiece.setInformation(information, mark % (2 ^ syntaxPiece.markLength), prevExprAmount);
                mark >>= syntaxPiece.markLength;
                prevExprAmount += syntaxPiece.expressionAmount;
            }
        }

        @Override
        public String toString(int mark) {
            String result = "";
            for (SyntaxPiece syntaxPiece : pieces) {
                String curr = syntaxPiece.toString(mark % (2 ^ syntaxPiece.markLength));
                if (result.length() > 0 && result.charAt(result.length() - 1) == ' ' && curr.length() > 0 && curr.charAt(0) == ' ') {
                    curr = curr.substring(1);
                }
                result += curr;
                mark >>= syntaxPiece.markLength;
            }
            return result;
        }
    }

}
