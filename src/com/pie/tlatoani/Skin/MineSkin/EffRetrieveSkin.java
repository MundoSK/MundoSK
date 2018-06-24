package com.pie.tlatoani.Skin.MineSkin;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.*;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Core.Static.Scheduling;
import com.pie.tlatoani.Skin.Skin;
import org.bukkit.event.Event;

import java.util.Arrays;

public class EffRetrieveSkin extends Effect {
    private Variable<?> variable;
    private ExprRetrievedSkin expression;

    @Override
    protected TriggerItem walk(Event event) {
        Scheduling.async(() -> {
            Skin delta = expression.getSkin(event);
            Scheduling.sync(() -> {
                variable.change(event, new Skin[]{delta}, Changer.ChangeMode.SET);
                TriggerItem.walk(getNext(), event);
            });
        });
        return null;
    }

    @Override
    protected void execute(Event event) {}

    @Override
    public String toString(Event event, boolean b) {
        return expression.toString(event, b).replace("retrieved", "retrieve") + " into " + variable;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        if (!(expressions[0] instanceof Variable)) {
            Skript.error("The 'retrieve skin' effect can only retrieve into variables!");
            return false;
        }
        variable = (Variable) expressions[expressions.length - 1];
        expression = new ExprRetrievedSkin();
        expression.init(Arrays.copyOfRange(expressions, 0, expressions.length - 1), i, kleenean, parseResult);
        return true;
    }
}
