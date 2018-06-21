package com.pie.tlatoani.Miscellaneous.JSON;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Variable;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Core.Static.Logging;
import org.bukkit.event.Event;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Arrays;
import java.util.List;
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
        jsonObject.forEach((o, value) -> {
            String key = (String) o;
            String subVarName = variableName.substring(0, variableName.length() - 1) + key;
            setFromJSONCompatibleObject(subVarName, value, isLocal, event);
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

    @Override
    protected void execute(Event event) {
        Variable<?> listVariable = (Variable<?>) this.listVariable;
        listVariable.change(event, null, Changer.ChangeMode.DELETE);
        if (isArray) {
            JSONObject[] jsonObjects = jsonObjectExpression.getArray(event);
            Logging.debug(this, "Expression: " + jsonObjectExpression);
            Logging.debug(this, "Array size: " + jsonObjects.length);
            Logging.debug(this, "Actual array: " + jsonObjects);
            List<Object> jsonObjectList = Arrays.asList((Object[]) jsonObjects);
            Logging.debug(this, "List size: " + jsonObjectList.size());
            Logging.debug(this, "Actual list: " + jsonObjects);
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
        Logging.debug(this, "Expression class: " + exprs[1].getClass());
        jsonObjectExpression = (Expression<JSONObject>) exprs[0];
        isArray = i == 1;
        Logging.debug(this, "Return type: " + exprs[1].getReturnType());
        if (exprs[1] instanceof Variable && ((Variable) exprs[1]).isList()) {
            listVariable = (Variable) exprs[1];
            listVariableName = listVariable.isLocal() ? listVariable.toString().substring(2, listVariable.toString().length() - 1) : listVariable.toString().substring(1, listVariable.toString().length() - 1);
            return true;
        }
        Skript.error("'put json %jsonobject% in listvar %objects%' must be used with a list variable!");;
        return false;
    }
}
