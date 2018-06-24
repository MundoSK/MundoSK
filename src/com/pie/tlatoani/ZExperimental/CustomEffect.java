package com.pie.tlatoani.ZExperimental;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAPIException;
import ch.njol.skript.lang.*;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.Pair;
import com.pie.tlatoani.Core.Static.Utilities;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Tlatoani on 2/24/17.
 */
public class CustomEffect extends SelfRegisteringSkriptEvent {
    private String rawSyntax;
    private String syntax;
    private ArrayList<String> exprNames = new ArrayList<>();
    private Trigger trigger;

    public String getSyntax() {
        return syntax;
    }

    public String getExprName(int index) {
        return exprNames.get(index);
    }

    public HashMap<String, Expression<?>> organizeExprs(Expression<?>[] expressions) {
        HashMap<String, Expression<?>> exprs = new HashMap<>();
        for (int i = 0; i < expressions.length; i++) {
            if (expressions[i] != null) {
                exprs.put(getExprName(i), expressions[i]);
            }
        }
        return exprs;
    }

    public void execute(Event event, HashMap<String, Expression<?>> expressions, SkriptParser.ParseResult parseResult) {
        trigger.execute(new CustomElementEvent(event, parseResult, expressions));
    }

    @Override
    public void register(Trigger trigger) {
        this.trigger = trigger;
        ExprExpr.disable();
    }

    @Override
    public void unregister(Trigger trigger) {
        EffCustom.unregisterEffect(this);
    }

    @Override
    public void unregisterAll() {
        EffCustom.unregisterAllEffects();
    }

    @Override
    public boolean init(Literal<?>[] literals, int i, SkriptParser.ParseResult parseResult) {
        rawSyntax = ((Literal<String>) literals[0]).getSingle();
        HashMap<String, Pair<Class, Boolean>> guarantees = new HashMap<>();
        syntax = "";
        for (int j = 0; j < rawSyntax.length(); j++) {
            syntax += rawSyntax.charAt(j);
            if (rawSyntax.charAt(j) == '\\') {
                j++;
                syntax += rawSyntax.charAt(j);
                continue;
            }
            if (rawSyntax.charAt(j) == '%') {
                String focus = rawSyntax.substring(j + 1);
                int nextPerc = focus.indexOf('%');
                if (nextPerc < 0) {
                    Skript.error("'" + rawSyntax + "' is not a valid alias, as there is an odd number of percentage points");
                    return false;
                }
                focus = focus.substring(0, nextPerc);
                int equalsSign = focus.indexOf('=');
                if (equalsSign < 0) {
                    Skript.error("'%" + focus + "%' should contain an equals sign naming the expression!");
                    return false;
                }
                String exprName = focus.substring(0, equalsSign);
                j += (equalsSign + 1);
                focus = focus.substring(equalsSign + 1);
                syntax += '-';
                if (focus.charAt(0) == '-') {
                    focus = focus.substring(1);
                    j++;
                }
                if (focus.charAt(0) == '*' || focus.charAt(0) == '~') {
                    syntax += focus.charAt(0);
                    focus = focus.substring(1);
                    j++;
                }
                String suffix = "";
                int atSymbol = focus.indexOf('@');
                if (atSymbol >= 0) {
                    suffix = focus.substring(atSymbol);
                    focus = focus.substring(0, atSymbol);
                }
                String[] codeNames = focus.split("/");
                ArrayList<Class> classes = new ArrayList<>();
                boolean alwaysSingle = true;
                for (String codeName : codeNames) {
                    if (codeName.charAt(codeName.length() - 1) == 's') {
                        alwaysSingle = false;
                        codeName = codeName.substring(0, codeName.length() - 1);
                    }
                    try {
                        classes.add(Classes.getClass(codeName));
                    } catch (SkriptAPIException e) {
                        Skript.error("'" + codeName + "' is not a valid type name!");
                        return false;
                    }
                }
                Class commonSuperClass = Utilities.commonSuperClass(classes.toArray(new Class[0]));
                exprNames.add(exprName);
                Pair<Class, Boolean> pair = guarantees.get(exprName);
                guarantees.put(exprName, new Pair<>(Utilities.commonSuperClass(commonSuperClass, pair.getFirst()), alwaysSingle && pair.getSecond()));
                syntax = syntax + focus + suffix + "%";
                j += focus.length() + suffix.length() + 1;
            }
        }
        ExprExpr.enable(guarantees);
        EffCustom.registerEffect(this);
        return true;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "effect \"" + rawSyntax + "\"";
    }
}
