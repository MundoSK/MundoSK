package com.pie.tlatoani.Core.Registration;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.*;
import com.pie.tlatoani.Core.Static.Logging;
import com.pie.tlatoani.Core.Static.Reflection;
import com.pie.tlatoani.ZExperimental.EffCustom;
import com.pie.tlatoani.ZExperimental.ExprExpr;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

/**
 * Created by Tlatoani on 2/24/17.
 */
public class ReflectiveRegistration {
    public static Collection<SyntaxElementInfo<? extends Condition>> conditions;
    public static Collection<SyntaxElementInfo<? extends Effect>> effects;
    public static Collection<SyntaxElementInfo<? extends Statement>> statements;
    public static List<ExpressionInfo<?, ?>> expressions;
    public static int[] expressionTypesStartIndices;

    public static Field patternsField;

    static {
        try {
            conditions = (Collection<SyntaxElementInfo<? extends Condition>>) Reflection.getStaticField(Skript.class, "conditions");
            effects = (Collection<SyntaxElementInfo<? extends Effect>>) Reflection.getStaticField(Skript.class, "effects");
            statements = (Collection<SyntaxElementInfo<? extends Statement>>) Reflection.getStaticField(Skript.class, "statements");
            expressions = (List<ExpressionInfo<?, ?>>) Reflection.getStaticField(Skript.class, "expressions");
            expressionTypesStartIndices = (int[]) Reflection.getStaticField(Skript.class, "expressionTypesStartIndices");

            patternsField = SyntaxElementInfo.class.getDeclaredField("patterns");
            patternsField.setAccessible(true);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Logging.reportException(ReflectiveRegistration.class, e);
        }
    }

    public static void onLoad() {
        EffCustom.onLoad();
        ExprExpr.onLoad();
    }

    public static void registerEffect(SyntaxElementInfo<? extends Effect> effectInfo) {
        effects.add(effectInfo);
        statements.add(effectInfo);
    }

    public static void registerCondition(SyntaxElementInfo<? extends Condition> conditionInfo) {
        conditions.add(conditionInfo);
        statements.add(conditionInfo);
    }

    public static void registerExpression(ExpressionInfo expressionInfo, ExpressionType type) {
        for (int i = type.ordinal() + 1; i < ExpressionType.values().length; i++) {
            expressionTypesStartIndices[i]++;
        }
        expressions.add(expressionTypesStartIndices[type.ordinal()], expressionInfo);
    }
}
