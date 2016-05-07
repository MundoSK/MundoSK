package com.pie.tlatoani.Json;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAPIException;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Variable;
import ch.njol.skript.lang.util.ContainerExpression;
import ch.njol.skript.util.Container;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Json.API.*;
import com.pie.tlatoani.Mundo;
import org.bukkit.event.Event;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by Tlatoani on 5/7/16.
 */
public class EffPutJsonInListVariable extends Effect {
    private Expression<JsonObject> jsonObjectExpression;
    private Variable listVariable;

    private static void setToJsonObject(String variableName, Map<String, JsonValue> jsonObject, Boolean isLocal, Event event) {
        Mundo.classDebug(EffPutJsonInListVariable.class, "Variable name: " + variableName);
        Mundo.classDebug(EffPutJsonInListVariable.class, "Is local: " + isLocal);
        jsonObject.forEach(new BiConsumer<String, JsonValue>() {
            @Override
            public void accept(String s, JsonValue jsonValue) {
                if (jsonValue instanceof JsonString) {
                    Mundo.classDebug(EffPutJsonInListVariable.class, "Jsonstring");
                    String indexname = variableName.substring(0, variableName.length() - 1) + s;
                    String result = ((JsonString) jsonValue).getString();
                    Mundo.classDebug(EffPutJsonInListVariable.class, "Indexname: " + indexname);
                    Mundo.classDebug(EffPutJsonInListVariable.class, "Result: " + result);
                    Variables.setVariable(indexname, result, event, isLocal);
                } else if (jsonValue instanceof JsonNumber) {
                    Mundo.classDebug(EffPutJsonInListVariable.class, "Jsonnumber");
                    String indexname = variableName.substring(0, variableName.length() - 1) + s;
                    Number result = ((JsonNumber) jsonValue).doubleValue();
                    Mundo.classDebug(EffPutJsonInListVariable.class, "Indexname: " + indexname);
                    Mundo.classDebug(EffPutJsonInListVariable.class, "Result: " + result);
                    Variables.setVariable(indexname, result, event, isLocal);
                } else if (jsonValue instanceof JsonArray) {
                    Mundo.classDebug(EffPutJsonInListVariable.class, "Jsonarray");
                    String indexname = variableName.substring(0, variableName.length() - 1) + s + "::*";
                    JsonArray result = (JsonArray) jsonValue;
                    Mundo.classDebug(EffPutJsonInListVariable.class, "Indexname: " + indexname);
                    Mundo.classDebug(EffPutJsonInListVariable.class, "Result: " + result);
                    setToJsonArray(indexname, result, isLocal, event);
                } else if (jsonValue instanceof JsonObject) {
                    Mundo.classDebug(EffPutJsonInListVariable.class, "Jsonobject");
                    String indexname = variableName.substring(0, variableName.length() - 1) + s + "::*";
                    JsonObject result = (JsonObject) jsonValue;
                    Mundo.classDebug(EffPutJsonInListVariable.class, "Indexname: " + indexname);
                    Mundo.classDebug(EffPutJsonInListVariable.class, "Result: " + result);
                    setToJsonObject(indexname, result, isLocal, event);
                }
            }
        });
    }

    private static void setToJsonArray(String variableName, List<JsonValue> jsonArray, Boolean isLocal, Event event) {
        for (int i = 1; i <= jsonArray.size(); i++) {
            JsonValue jsonValue = jsonArray.get(i - 1);
            if (jsonValue instanceof JsonString) {
                Mundo.classDebug(EffPutJsonInListVariable.class, "Jsonstring");
                String indexname = variableName.substring(0, variableName.length() - 1) + i;
                String result = ((JsonString) jsonValue).getString();
                Mundo.classDebug(EffPutJsonInListVariable.class, "Indexname: " + indexname);
                Mundo.classDebug(EffPutJsonInListVariable.class, "Result: " + result);
                Variables.setVariable(indexname, result, event, isLocal);
            } else if (jsonValue instanceof JsonNumber) {
                Mundo.classDebug(EffPutJsonInListVariable.class, "Jsonnumber");
                String indexname = variableName.substring(0, variableName.length() - 1) + i;
                Number result = ((JsonNumber) jsonValue).doubleValue();
                Mundo.classDebug(EffPutJsonInListVariable.class, "Indexname: " + indexname);
                Mundo.classDebug(EffPutJsonInListVariable.class, "Result: " + result);
                Variables.setVariable(indexname, result, event, isLocal);
            } else if (jsonValue instanceof JsonArray) {
                Mundo.classDebug(EffPutJsonInListVariable.class, "Jsonarray");
                String indexname = variableName.substring(0, variableName.length() - 1) + i + "::*";
                JsonArray result = (JsonArray) jsonValue;
                Mundo.classDebug(EffPutJsonInListVariable.class, "Indexname: " + indexname);
                Mundo.classDebug(EffPutJsonInListVariable.class, "Result: " + result);
                setToJsonArray(indexname, result, isLocal, event);
            } else if (jsonValue instanceof JsonObject) {
                Mundo.classDebug(EffPutJsonInListVariable.class, "Jsonobject");
                String indexname = variableName.substring(0, variableName.length() - 1) + i + "::*";
                JsonObject result = (JsonObject) jsonValue;
                Mundo.classDebug(EffPutJsonInListVariable.class, "Indexname: " + indexname);
                Mundo.classDebug(EffPutJsonInListVariable.class, "Result: " + result);
                setToJsonObject(indexname, result, isLocal, event);
            }
        }
    }

    @Override
    protected void execute(Event event) {
        Variable<?> listVariable = (Variable<?>) this.listVariable;
        listVariable.change(event, null, Changer.ChangeMode.DELETE);
        Map<String, JsonValue> jsonObject = jsonObjectExpression.getSingle(event);
        String name = listVariable.isLocal() ? listVariable.toString().substring(2, listVariable.toString().length() - 1) : listVariable.toString().substring(1, listVariable.toString().length() - 1);
        Mundo.debug(this, "Variable name try it: " + name);
        setToJsonObject(name, jsonObject, listVariable.isLocal(), event);
    }

    @Override
    public String toString(Event event, boolean b) {
        return "put json %jsonobject% in listvar %objects%";
    }

    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        Mundo.debug(this, "Expression class: " + exprs[1].getClass());
        jsonObjectExpression = (Expression<JsonObject>) exprs[0];
        Mundo.debug(this, "Return type: " + exprs[1].getReturnType());
        if (exprs[1] instanceof Variable && ((Variable) exprs[1]).isList()) {
            listVariable = (Variable) exprs[1];
            return true;
        }
        Skript.error("'put json %jsonobject% in listvar %objects%' must be used with a list variable!");;
        return false;
    }
}
