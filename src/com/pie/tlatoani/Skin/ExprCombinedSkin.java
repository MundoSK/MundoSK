package com.pie.tlatoani.Skin;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.json.simple.JSONArray;

/**
 * Created by Tlatoani on 3/5/17.
 */
public class ExprCombinedSkin extends SimpleExpression<Skin> {
    private Expression<Skin> skinExpression;

    @Override
    protected Skin[] get(Event event) {
        Skin[] skins = skinExpression.getArray(event);
        JSONArray[] jsonArrays = new JSONArray[skins.length];
        for (int i = 0; i < skins.length; i++) {
            jsonArrays[i] = skins[i].toJSONArray();
        }
        return new Skin[]{new Skin.JSON(jsonArrays)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Skin> getReturnType() {
        return Skin.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "combined skin from " + skinExpression;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        skinExpression = (Expression<Skin>) expressions[0];
        return true;
    }
}
