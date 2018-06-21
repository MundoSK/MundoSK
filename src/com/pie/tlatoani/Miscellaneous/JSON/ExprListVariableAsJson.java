package com.pie.tlatoani.Miscellaneous.JSON;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Variable;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Core.Static.Logging;
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

    @Override
    protected JSONObject[] get(Event event) {
        TreeMap<String, Object> treeMap = (TreeMap) Variables.getVariable(listVariable.isLocal() ? listVariable.toString().substring(2, listVariable.toString().length() - 1) : listVariable.toString().substring(1, listVariable.toString().length() - 1), event, listVariable.isLocal());
        if (isArray) {
            if (treeMap == null) {
                return new JSONObject[0];
            } else {
                List<Object> result = getJSONArray(treeMap);
                return result.toArray(new JSONObject[0]);
            }
        } else {
            if (treeMap == null) {
                return new JSONObject[]{new JSONObject()};
            } else {
                JSONObject result = getJSONObject(treeMap);
                Logging.debug(this, "Final JSON: " + result);
                return new JSONObject[]{result};
            }
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
