package com.pie.tlatoani.Json;

import ch.njol.skript.classes.Serializer;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.variables.SerializedVariable;
import ch.njol.yggdrasil.Fields;
import com.pie.tlatoani.Util.Registration;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.NotSerializableException;
import java.io.StreamCorruptedException;
import java.util.function.BiConsumer;

/**
 * Created by Tlatoani on 8/8/17.
 */
public class JSONMundo {
    
    public static void load() {
        Registration.registerType(JSONObject.class, "jsonobject").parser(new Registration.SimpleParser<JSONObject>() {
            @Override
            public JSONObject parse(String s, ParseContext parseContext) {
                JSONObject result = null;
                try {
                    result = (JSONObject) (new JSONParser()).parse(s);
                } catch (ParseException | ClassCastException e) {
                    //If parsing to a JSONObject fails, return null
                }
                return result;
            }
        }).serializer(new Serializer<JSONObject>() {
            @Override
            public Fields serialize(JSONObject jsonObject) throws NotSerializableException {
                JSONObject toBecomeString = new JSONObject();
                jsonObject.forEach(new BiConsumer() {
                    @Override
                    public void accept(Object o, Object o2) {
                        SerializedVariable.Value value = Classes.serialize(o2);
                        if (value != null) {
                            JSONObject valueJSON = new JSONObject();
                            valueJSON.put("type", value.type);
                            valueJSON.put("Data", new String(value.data));
                            toBecomeString.put(o, valueJSON);
                        }
                    }
                });
                Fields fields = new Fields();
                fields.putObject("value", toBecomeString.toJSONString());
                return fields;
            }

            @Override
            public void deserialize(JSONObject jsonObject, Fields fields) throws StreamCorruptedException, NotSerializableException {
                try {
                    JSONObject fromString = (JSONObject) (new JSONParser()).parse((String) fields.getObject("value"));
                    fromString.forEach(new BiConsumer() {
                        @Override
                        public void accept(Object o, Object o2) {
                            JSONObject valueJSON = (JSONObject) o2;
                            Object value = Classes.deserialize((String) valueJSON.get("type"), ((String) valueJSON.get("Data")).getBytes());
                            jsonObject.put(o, value);
                        }
                    });
                } catch (ParseException | ClassCastException e) {
                    throw new StreamCorruptedException();
                }
            }

            @Override
            public boolean mustSyncDeserialization() {
                return false;
            }

            @Override
            protected boolean canBeInstantiated() {
                return true;
            }
        });
        Registration.registerEffect(EffPutJsonInListVariable.class, "put json %jsonobject% in listvar %objects%", "put jsons %jsonobjects% in listvar %objects%");
        Registration.registerExpression(ExprListVariableAsJson.class, JSONObject.class, ExpressionType.PROPERTY, "json (of|from) (listvar|list variable) %objects%", "jsons (of|from) (listvar|list variable) %objects%");
        Registration.registerExpression(ExprStringAsJson.class, JSONObject.class, ExpressionType.PROPERTY, "json of string %string%");
    }
}
