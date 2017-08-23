package com.pie.tlatoani.ZExperimental;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SyntaxElement;
import com.pie.tlatoani.ZExperimental.SyntaxPiece.SyntaxPiece.MarkSpecificInformation;

/**
 * Created by Tlatoani on 4/2/17.
 */
public class UtilSyntax {

    public void setVariables(SyntaxElement element, MarkSpecificInformation information, Expression[] expressions) {
        Class elemClass = element.getClass();
        information.exprIndexes.forEach((var, index) -> {

        });
    }
}
