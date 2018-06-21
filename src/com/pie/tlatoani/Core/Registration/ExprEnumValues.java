package com.pie.tlatoani.Core.Registration;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tlatoani on 9/9/17.
 */
public class ExprEnumValues extends SimpleExpression<Object> {
    static final List<EnumClassInfo> enumClassInfos = new ArrayList<>();
    private static boolean registered = false;
    private static final ModifiableSyntaxElementInfo.Expression expressionInfo =
            new ModifiableSyntaxElementInfo.Expression(ExprEnumValues.class, Object.class, ExpressionType.SIMPLE);

    private EnumClassInfo enumClassInfo;

    static void addEnumClassInfo(EnumClassInfo enumClassInfo) {
        enumClassInfos.add(enumClassInfo);
        expressionInfo.addPattern("[all [of the]] " + enumClassInfo.getPluralCodeName());
        if (!registered) {
            expressionInfo.register();
            registered = true;
        }
    }

    @Override
    protected Object[] get(Event event) {
        return enumClassInfo.getAllValues();
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends Object> getReturnType() {
        return enumClassInfo.getC();
    }

    @Override
    public String toString(Event event, boolean b) {
        return "all " + enumClassInfo.getPluralCodeName();
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        enumClassInfo = enumClassInfos.get(i);
        return true;
    }
}
