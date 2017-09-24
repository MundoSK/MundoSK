package com.pie.tlatoani.Miscellaneous;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 11/28/16.
 */
public class ExprAllTypes extends SimpleExpression<ClassInfo> {

    @Override
    protected ClassInfo[] get(Event event) {
        return Classes.getClassInfos().toArray(new ClassInfo[0]);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends ClassInfo> getReturnType() {
        return ClassInfo.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "all types";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        return true;
    }
}
