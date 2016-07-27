package com.pie.tlatoani.Json;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Variable;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Mundo;
import org.bukkit.event.Event;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by Tlatoani on 5/7/16.
 */
public class EffPutJsonInListVariable extends Effect {
    private Expression<JSONObject> jsonObjectExpression;
    private Variable listVariable;
    String listVariableName;
    private Boolean isArray;

    private static void setFromJSONCompatibleObject(String varName, Object value, Boolean isLocal, Event event) {
        if (value instanceof JSONArray) {
            setToJSONArray(varName + "::*", (JSONArray) value, isLocal, event);
        } else if (value instanceof JSONObject) {
            setToJSONObject(varName + "::*", (JSONObject) value, isLocal, event);
        } else {
            Variables.setVariable(varName, value, event, isLocal);
        }
    }

    private static void setToJSONObject(String variableName, JSONObject jsonObject, Boolean isLocal, Event event) {
        jsonObject.forEach(new BiConsumer() {
            @Override
            public void accept(Object o, Object value) {
                String key = (String) o;
                String subVarName = variableName.substring(0, variableName.length() - 1) + key;
                setFromJSONCompatibleObject(subVarName, value, isLocal, event);
            }
        });
    }

    private static void setToJSONArray(String variableName, List<Object> jsonArray, Boolean isLocal, Event event) {
        jsonArray.forEach(new Consumer<Object>() {
            private int index = 0;

            @Override
            public void accept(Object value) {
                index++;
                String subVarName = variableName.substring(0, variableName.length() - 1) + index;
                setFromJSONCompatibleObject(subVarName, value, isLocal, event);
            }
        });
    }

    /*
    private static void setToJsonObject(String variableName, Map<String, JsonValue> jsonObject, Boolean isLocal, Event event) {
        Mundo.debug(EffPutJsonInListVariable.class, "Variable name: " + variableName);
        Mundo.debug(EffPutJsonInListVariable.class, "Is local: " + isLocal);
        jsonObject.forEach(new BiConsumer<String, JsonValue>() {
            @Override
            public void accept(String s, JsonValue jsonValue) {
                if (jsonValue instanceof JsonString) {
                    Mundo.debug(EffPutJsonInListVariable.class, "Jsonstring");
                    String indexname = variableName.substring(0, variableName.length() - 1) + s;
                    String result = ((JsonString) jsonValue).getString();
                    Mundo.debug(EffPutJsonInListVariable.class, "Indexname: " + indexname);
                    Mundo.debug(EffPutJsonInListVariable.class, "Result: " + result);
                    Variables.setVariable(indexname, result, event, isLocal);
                } else if (jsonValue instanceof JsonNumber) {
                    Mundo.debug(EffPutJsonInListVariable.class, "Jsonnumber");
                    String indexname = variableName.substring(0, variableName.length() - 1) + s;
                    Number result = ((JsonNumber) jsonValue).doubleValue();
                    Mundo.debug(EffPutJsonInListVariable.class, "Indexname: " + indexname);
                    Mundo.debug(EffPutJsonInListVariable.class, "Result: " + result);
                    Variables.setVariable(indexname, result, event, isLocal);
                } else if (jsonValue instanceof JsonArray) {
                    Mundo.debug(EffPutJsonInListVariable.class, "Jsonarray");
                    String indexname = variableName.substring(0, variableName.length() - 1) + s + "::*";
                    JsonArray result = (JsonArray) jsonValue;
                    Mundo.debug(EffPutJsonInListVariable.class, "Indexname: " + indexname);
                    Mundo.debug(EffPutJsonInListVariable.class, "Result: " + result);
                    setToJsonArray(indexname, result, isLocal, event);
                } else if (jsonValue instanceof JsonObject) {
                    Mundo.debug(EffPutJsonInListVariable.class, "Jsonobject");
                    String indexname = variableName.substring(0, variableName.length() - 1) + s + "::*";
                    JsonObject result = (JsonObject) jsonValue;
                    Mundo.debug(EffPutJsonInListVariable.class, "Indexname: " + indexname);
                    Mundo.debug(EffPutJsonInListVariable.class, "Result: " + result);
                    setToJsonObject(indexname, result, isLocal, event);
                }
            }
        });
    }

    private static void setToJsonArray(String variableName, List<JsonValue> jsonArray, Boolean isLocal, Event event) {
        for (int i = 1; i <= jsonArray.size(); i++) {
            JsonValue jsonValue = jsonArray.get(i - 1);
            if (jsonValue instanceof JsonString) {
                Mundo.debug(EffPutJsonInListVariable.class, "Jsonstring");
                String indexname = variableName.substring(0, variableName.length() - 1) + i;
                String result = ((JsonString) jsonValue).getString();
                Mundo.debug(EffPutJsonInListVariable.class, "Indexname: " + indexname);
                Mundo.debug(EffPutJsonInListVariable.class, "Result: " + result);
                Variables.setVariable(indexname, result, event, isLocal);
            } else if (jsonValue instanceof JsonNumber) {
                Mundo.debug(EffPutJsonInListVariable.class, "Jsonnumber");
                String indexname = variableName.substring(0, variableName.length() - 1) + i;
                Number result = ((JsonNumber) jsonValue).doubleValue();
                Mundo.debug(EffPutJsonInListVariable.class, "Indexname: " + indexname);
                Mundo.debug(EffPutJsonInListVariable.class, "Result: " + result);
                Variables.setVariable(indexname, result, event, isLocal);
            } else if (jsonValue instanceof JsonArray) {
                Mundo.debug(EffPutJsonInListVariable.class, "Jsonarray");
                String indexname = variableName.substring(0, variableName.length() - 1) + i + "::*";
                JsonArray result = (JsonArray) jsonValue;
                Mundo.debug(EffPutJsonInListVariable.class, "Indexname: " + indexname);
                Mundo.debug(EffPutJsonInListVariable.class, "Result: " + result);
                setToJsonArray(indexname, result, isLocal, event);
            } else if (jsonValue instanceof JsonObject) {
                Mundo.debug(EffPutJsonInListVariable.class, "Jsonobject");
                String indexname = variableName.substring(0, variableName.length() - 1) + i + "::*";
                JsonObject result = (JsonObject) jsonValue;
                Mundo.debug(EffPutJsonInListVariable.class, "Indexname: " + indexname);
                Mundo.debug(EffPutJsonInListVariable.class, "Result: " + result);
                setToJsonObject(indexname, result, isLocal, event);
            }
        }
    }*/

    @Override
    protected void execute(Event event) {
        Variable<?> listVariable = (Variable<?>) this.listVariable;
        listVariable.change(event, null, Changer.ChangeMode.DELETE);
        if (isArray) {
            JSONObject[] jsonObjects = jsonObjectExpression.getAll(event);
            Mundo.debug(this, "Expression: " + jsonObjectExpression);
            Mundo.debug(this, "Array size: " + jsonObjects.length);
            Mundo.debug(this, "Actual array: " + jsonObjects);
            List<Object> jsonObjectList = Arrays.asList((Object[]) jsonObjects);
            Mundo.debug(this, "List size: " + jsonObjectList.size());
            Mundo.debug(this, "Actual list: " + jsonObjects);
            setToJSONArray(listVariableName, jsonObjectList, listVariable.isLocal(), event);
        } else {
            JSONObject jsonObject = jsonObjectExpression.getSingle(event);
            setToJSONObject(listVariableName, jsonObject, listVariable.isLocal(), event);
        }
    }

    @Override
    public String toString(Event event, boolean b) {
        return "put json %jsonobject% in listvar %objects%";
    }

    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        Mundo.debug(this, "Expression class: " + exprs[1].getClass());
        jsonObjectExpression = (Expression<JSONObject>) exprs[0];
        isArray = i == 1;
        Mundo.debug(this, "Return type: " + exprs[1].getReturnType());
        if (exprs[1] instanceof Variable && ((Variable) exprs[1]).isList()) {
            listVariable = (Variable) exprs[1];
            listVariableName = listVariable.isLocal() ? listVariable.toString().substring(2, listVariable.toString().length() - 1) : listVariable.toString().substring(1, listVariable.toString().length() - 1);
            return true;
        }
        Skript.error("'put json %jsonobject% in listvar %objects%' must be used with a list variable!");;
        return false;
    }
}
