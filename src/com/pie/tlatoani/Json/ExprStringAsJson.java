package com.pie.tlatoani.Json;

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
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) (new JSONParser()).parse(stringExpression.getSingle(event));
        } catch (ParseException | ClassCastException e) {}
        return new JSONObject[]{jsonObject};
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
        return true;
    }
}
