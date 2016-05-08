package com.pie.tlatoani.Json;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Json.API.Json;
import com.pie.tlatoani.Json.API.JsonObject;
import com.pie.tlatoani.Json.API.stream.JsonParsingException;
import org.bukkit.event.Event;

import java.io.StringReader;

/**
 * Created by Tlatoani on 5/8/16.
 */
public class ExprStringAsJson extends SimpleExpression<JsonObject> {
    private Expression<String> stringExpression;

    @Override
    protected JsonObject[] get(Event event) {
        JsonObject jsonObject = null;
        try {
            jsonObject = Json.createReader(new StringReader(stringExpression.getSingle(event))).readObject();
        } catch (JsonParsingException e) {}
        return new JsonObject[]{jsonObject};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends JsonObject> getReturnType() {
        return JsonObject.class;
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
