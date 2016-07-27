package com.pie.tlatoani.Json;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Variable;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Mundo;
import org.bukkit.event.Event;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.TreeMap;
import java.util.function.BiConsumer;

/**
 * Created by Tlatoani on 5/7/16.
 */
public class ExprListVariableAsJson extends SimpleExpression<JSONObject> {
    private Variable<?> listVariable;
    private Boolean isArray;

    private static Object getJSONCompatibleObject(Object val) {
        if (val instanceof TreeMap) {
            TreeMap<String, Object> treeMap1 = (TreeMap<String, Object>) val;
            if (treeMap1.containsKey("1")) {
                return getJSONArray(treeMap1);
            } else {
                return getJSONObject(treeMap1);
            }
        } else {
            return val;
        }
    }

    private static JSONObject getJSONObject(TreeMap<String, Object> treeMap) {
        JSONObject jsonObject = new JSONObject();
        treeMap.forEach(new BiConsumer<String, Object>() {
            public void accept(String key, Object val) {
                jsonObject.put(key, getJSONCompatibleObject(val));
            }
        });
        return jsonObject;
    }

    private static JSONArray getJSONArray(TreeMap<String, Object> treeMap) {
        JSONArray jsonArray = new JSONArray();
        treeMap.forEach(new BiConsumer<String, Object>() {
            public void accept(String key, Object val) {
                jsonArray.add(getJSONCompatibleObject(val));
            }
        });
        return jsonArray;
    }

    /*
    private static JsonObject getJsonObject(TreeMap<String, Object> treeMap) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        JSONObject jsonObject = new JSONObject();
        treeMap.forEach(new BiConsumer<String, Object>() {
            public void accept(String key, Object val) {
                if (val instanceof String) {
                    Mundo.debug(ExprListVariableAsJson.class, "String found");
                    Mundo.debug(ExprListVariableAsJson.class, "Key: " + key);
                    Mundo.debug(ExprListVariableAsJson.class, "Value: " + val);
                    builder.add(key, (String) val);
                } else if (val instanceof Number) {
                    Mundo.debug(ExprListVariableAsJson.class, "Number found");
                    Mundo.debug(ExprListVariableAsJson.class, "Key: " + key);
                    Mundo.debug(ExprListVariableAsJson.class, "Value: " + val);
                    builder.add(key, ((Number) val).doubleValue());
                } else if (val instanceof TreeMap) {
                    if (((TreeMap) val).containsKey("1")) {
                        Mundo.debug(ExprListVariableAsJson.class, "JSONArray found");
                        Mundo.debug(ExprListVariableAsJson.class, "Key: " + key);
                        Mundo.debug(ExprListVariableAsJson.class, "Value: " + val);
                        JsonArray valarray = getJsonArray((TreeMap<String, Object>) val);
                        Mundo.debug(ExprListVariableAsJson.class, "Polished Val: " + valarray);
                        builder.add(key, valarray);
                    } else {
                        Mundo.debug(ExprListVariableAsJson.class, "JSONObject found");
                        Mundo.debug(ExprListVariableAsJson.class, "Key: " + key);
                        Mundo.debug(ExprListVariableAsJson.class, "Value: " + val);
                        JsonObject valobject = getJsonObject((TreeMap<String, Object>) val);
                        Mundo.debug(ExprListVariableAsJson.class, "Polished Val: " + valobject);
                        builder.add(key, valobject);
                    }
                }

            }
        });
        return builder.build();
    }

    private static JsonArray getJsonArray(TreeMap<String, Object> treeMap) {
        JsonArrayBuilder builder = Json.createArrayBuilder();
        treeMap.forEach(new BiConsumer<String, Object>() {
            public void accept(String key, Object val) {
                if (val instanceof String) {
                    Mundo.debug(ExprListVariableAsJson.class, "String found");
                    Mundo.debug(ExprListVariableAsJson.class, "Key: " + key);
                    Mundo.debug(ExprListVariableAsJson.class, "Value: " + val);
                    builder.add((String) val);
                } else if (val instanceof Number) {
                    Mundo.debug(ExprListVariableAsJson.class, "Number found");
                    Mundo.debug(ExprListVariableAsJson.class, "Key: " + key);
                    Mundo.debug(ExprListVariableAsJson.class, "Value: " + val);
                    builder.add(((Number) val).doubleValue());
                } else if (val instanceof TreeMap) {
                    if (((TreeMap) val).containsKey("1")) {
                        Mundo.debug(ExprListVariableAsJson.class, "JSONArray found");
                        Mundo.debug(ExprListVariableAsJson.class, "Key: " + key);
                        Mundo.debug(ExprListVariableAsJson.class, "Value: " + val);
                        JsonArray valarray = getJsonArray((TreeMap<String, Object>) val);
                        Mundo.debug(ExprListVariableAsJson.class, "Polished Val: " + valarray);
                        builder.add(valarray);
                    } else {
                        Mundo.debug(ExprListVariableAsJson.class, "JSONObject found");
                        Mundo.debug(ExprListVariableAsJson.class, "Key: " + key);
                        Mundo.debug(ExprListVariableAsJson.class, "Value: " + val);
                        JsonObject valobject = getJsonObject((TreeMap<String, Object>) val);
                        Mundo.debug(ExprListVariableAsJson.class, "Polished Val: " + valobject);
                        builder.add(valobject);
                    }
                }
            }
        });
        return builder.build();
    }*/

    @Override
    protected JSONObject[] get(Event event) {
        TreeMap<String, Object> treeMap = (TreeMap) Variables.getVariable(listVariable.isLocal() ? listVariable.toString().substring(2, listVariable.toString().length() - 1) : listVariable.toString().substring(1, listVariable.toString().length() - 1), event, listVariable.isLocal());
        if (isArray) {
            List<Object> result = getJSONArray(treeMap);
            return result.toArray(new JSONObject[0]);
        } else {
            JSONObject result = getJSONObject(treeMap);
            Mundo.debug(this, "Final Json: " + result);
            return new JSONObject[] {result};
        }

    }

    @Override
    public boolean isSingle() {
        return !isArray;
    }

    @Override
    public Class<? extends JSONObject> getReturnType() {
        return JSONObject.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "listvar %objects% as json";
    }

    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        isArray = i == 1;
        if (exprs[0] instanceof Variable && ((Variable) exprs[0]).isList()) {
            listVariable = (Variable) exprs[0];
            return true;
        }
        Skript.error("'listvar %objects% as json' must be used with a list variable!");;
        return false;
    }
}
