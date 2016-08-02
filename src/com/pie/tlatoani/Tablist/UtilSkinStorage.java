package com.pie.tlatoani.Tablist;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.pie.tlatoani.Mundo;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by Tlatoani on 7/26/16.
 */
public class UtilSkinStorage {
    private static ListMultimap<UUID, UtilSignedProperty> skinSaver = ArrayListMultimap.create();
    private final static String filename = "skinstorage.json";

    private UtilSkinStorage() {} //Cannot be initialized

    public static void load() throws IOException {
        readJSONObject(getLoaderFile()).forEach(new BiConsumer() {
            @Override
            public void accept(Object key, Object value) {
                String s = (String) key;
                UUID playerUUID = UUID.fromString(s);
                JSONArray jsonArray = (JSONArray) value;
                jsonArray.forEach(new Consumer() {
                    @Override
                    public void accept(Object o) {
                        JSONObject jsonObject = (JSONObject) o;
                        UtilSignedProperty property = getPropertyFromJSON(jsonObject);
                        skinSaver.put(playerUUID, property);
                    }
                });
            }
        });
    }

    public static void save() throws IOException {
        FileWriter writer = new FileWriter(getLoaderFile());
        writer.write(getJSONOfData().toString());
        writer.flush();
        writer.close();
    }

    public static File getLoaderFile() throws IOException {
        File result = new File(Mundo.pluginFolder + File.separator + filename);
        if (!result.exists()) {
            result.createNewFile();
            //JsonObject emptyObject = Json.createObjectBuilder().build();
            JSONObject emptyObject = new JSONObject();
            FileWriter writer = new FileWriter(result);
            writer.write(emptyObject.toString());
            writer.flush();
            writer.close();
        }
        return result;
    }

    public static JSONObject readJSONObject(File jsonFile) throws FileNotFoundException {
        JSONParser parser = new JSONParser();
        JSONObject result = null;
        try {
            result = (JSONObject) parser.parse(new FileReader(jsonFile));
        } catch (IOException | ParseException | ClassCastException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static JSONObject getJSONOfData() {
        JSONObject jsonObject = new JSONObject();
        skinSaver.keySet().forEach(new Consumer<UUID>() {
            @Override
            public void accept(UUID uuid) {
                JSONArray jsonArray = new JSONArray();
                skinSaver.get(uuid).forEach(new Consumer<UtilSignedProperty>() {
                    @Override
                    public void accept(UtilSignedProperty property) {
                        jsonArray.add(getPropertyJSON(property));
                    }
                });
                jsonObject.put(uuid.toString(), jsonArray);
            }
        });
        return jsonObject;
    }

    //Map Interactions

    public static List<UtilSignedProperty> getProperties(UUID playerUUID) {
        return new ArrayList<UtilSignedProperty>(skinSaver.get(playerUUID));
    }

    public static void setProperties(UUID playerUUID, Collection<UtilSignedProperty> properties) {
        skinSaver.replaceValues(playerUUID, properties);
    }

    public static void removeProperties(UUID playerUUID) {
        skinSaver.removeAll(playerUUID);
    }

    //Conversion

    public static JSONObject getPropertyJSON(UtilSignedProperty property) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("value", property.value);
        jsonObject.put("signature", property.signature);
        return jsonObject;
    }

    public static UtilSignedProperty getPropertyFromJSON(JSONObject propertyJSON) {
        UtilSignedProperty property = new UtilSignedProperty(
                "textures",
                (String) propertyJSON.get("value"),
                (String) propertyJSON.get("signature")
        );
        return property;
    }
}
