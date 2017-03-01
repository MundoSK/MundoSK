package com.pie.tlatoani.ZExperimental;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.*;
import com.pie.tlatoani.Mundo;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

/**
 * Created by Tlatoani on 2/24/17.
 */
public class UtilSyntaxRegistration {
    public static Collection<SyntaxElementInfo<? extends Condition>> conditions;
    public static Collection<SyntaxElementInfo<? extends Effect>> effects;
    public static Collection<SyntaxElementInfo<? extends Statement>> statements;
    public static List<ExpressionInfo<?, ?>> expressions;
    public static int[] expressionTypesStartIndices;

    public static Field patternsField;

    static {
        try {
            conditions = (Collection<SyntaxElementInfo<? extends Condition>>) Mundo.getStaticField(Skript.class, "conditions");
            effects = (Collection<SyntaxElementInfo<? extends Effect>>) Mundo.getStaticField(Skript.class, "effects");
            statements = (Collection<SyntaxElementInfo<? extends Statement>>) Mundo.getStaticField(Skript.class, "statements");
            expressionTypesStartIndices = (int[]) Mundo.getStaticField(Skript.class, "expressionTypesStartIndices");

            patternsField = SyntaxElementInfo.class.getDeclaredField("patterns");
            patternsField.setAccessible(true);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Mundo.reportException(UtilSyntaxRegistration.class, e);
        }
    }

    public static void onLoad() {
        EffCustom.onLoad();
        ExprExpr.onLoad();
    }

    public static void setPatterns(SyntaxElementInfo<?> syntaxElementInfo, String... patterns) {
        try {
            patternsField.set(syntaxElementInfo, patterns);
        } catch (IllegalAccessException e) {
            Mundo.reportException(UtilSyntaxRegistration.class, e);
        }
    }

    public static void registerEffect(SyntaxElementInfo<? extends Effect> effectInfo) {
        effects.add(effectInfo);
        statements.add(effectInfo);
    }

    public static void registerExpression(ExpressionInfo expressionInfo, ExpressionType type) {
        for (int i = type.ordinal() + 1; i < ExpressionType.values().length; i++) {
            expressionTypesStartIndices[i]++;
        }
        expressions.add(expressionTypesStartIndices[type.ordinal()], expressionInfo);
    }
}
