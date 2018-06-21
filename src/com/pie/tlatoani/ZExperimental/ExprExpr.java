package com.pie.tlatoani.ZExperimental;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.Pair;
import com.pie.tlatoani.Core.Registration.ModifiableSyntaxElementInfo;
import org.bukkit.event.Event;

import java.util.HashMap;

/**
 * Created by Tlatoani on 2/24/17.
 */
public class ExprExpr extends SimpleExpression<Object> {
    private static String[] exprNames = null;
    private static HashMap<String, Pair<Class, Boolean>> guarantees = null;
    private static final ModifiableSyntaxElementInfo.Expression<ExprExpr, Object> exprInfo = new ModifiableSyntaxElementInfo.Expression<>(ExprExpr.class, Object.class, ExpressionType.SIMPLE);

    private String exprName;
    private Class guaranteedClass;
    private boolean guaranteedSingle;

    public static void onLoad() {
        exprInfo.register();
    }

    public static void enable(HashMap<String, Pair<Class, Boolean>> guarantees) {
        ExprExpr.guarantees = guarantees;
        exprNames = guarantees.keySet().toArray(new String[0]);
        String[] exprNamesWithIndex = new String[exprNames.length];
        for (int i = 0; i < exprNames.length; i++) {
            exprNamesWithIndex[i] = i + "¦" + exprNames[i];
        }
        String syntax = "expr-(" + String.join("|", exprNamesWithIndex) + ")";
        exprInfo.setPatterns(syntax);
    }

    public static void disable() {
        exprNames = null;
        guarantees = null;
        exprInfo.setPatterns();

    }

    @Override
    protected Object[] get(Event event) {
        if (event instanceof CustomElementEvent) {
            CustomElementEvent customElementEvent = (CustomElementEvent) event;
            return customElementEvent.evalExpr(exprName);
        }
        throw new IllegalArgumentException();
    }

    @Override
    public boolean isSingle() {
        return guaranteedSingle;
    }

    @Override
    public Class getReturnType() {
        return guaranteedClass;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "expr-" + exprName;
    }

    @Override
    public boolean init(ch.njol.skript.lang.Expression[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        if (exprNames == null) {
            Skript.error("The 'expr' expression should not be able to be used here");
            return false;
        }
        exprName = exprNames[parseResult.mark];
        Pair<Class, Boolean> guarantee = guarantees.get(exprName);
        guaranteedClass = guarantee.getFirst();
        guaranteedSingle = guarantee.getSecond();
        return true;
    }
}
