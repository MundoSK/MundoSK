package com.pie.tlatoani.Miscellaneous.JSON;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

import org.bukkit.event.Event;


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Created by Tlatoani on 5/8/16.
 */
public class ExprStringAsJson extends SimpleExpression<JSONObject> {
    private Expression<String> stringExpression;

    @Override
    protected JSONObject[] get(Event event) {
        /*JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) (new JSONParser()).parse(stringExpression.getSingle(event));
        } catch (ParseException | ClassCastException e) {}
        return new JSONObject[]{jsonObject};*/
        throw new UnsupportedOperationException("The 'json of string %string%' expression will be removed in a future version, please use '%string% parsed as a jsonobject' instead");
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends JSONObject> getReturnType() {
        return JSONObject.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "json of string %string%";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        stringExpression = (Expression<String>) expressions[0];
        Skript.error("The 'json of string %string%' expression will be removed in a future version, please use '%string% parsed as a jsonobject' instead");
        return false;
    }
}
