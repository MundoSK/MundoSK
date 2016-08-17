package com.pie.tlatoani.WorldManagement.WorldLoader;

import com.pie.tlatoani.Generator.ChunkGeneratorWithID;
import com.pie.tlatoani.Mundo;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.function.BiConsumer;

/**
 * Created by Tlatoani on 7/3/16.
 */
public final class UtilWorldLoader {
    private static Map<String, WorldCreator> worldLoaderSaver = new HashMap<String, WorldCreator>();
    private final static String filename = "worldloader.json";

    private UtilWorldLoader() {} //Cannot be initialized

    public static void load() throws IOException {
        readJSONObject(getLoaderFile()).forEach(new BiConsumer() {
            @Override
            public void accept(Object key, Object value) {
                String s = (String) key;
                JSONObject jsonObject = (JSONObject) value;
                WorldCreator creator = getCreatorFromJSON(s, jsonObject);
                setCreator(creator);
                creator.createWorld();
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
        /*JsonReader reader = Json.createReader(new FileReader(jsonFile));
        JsonObject worldLoaders = reader.readObject();
        Map<String, JsonObject> creatorJsons = new HashMap<>();
        worldLoaders.forEach(new BiConsumer<String, JsonValue>() {
            @Override
            public void accept(String s, JsonValue jsonValue) {
                creatorJsons.put(s, (JsonObject) jsonValue);
            }
        });
        return creatorJsons;*/
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
        //JsonObjectBuilder builder = Json.createObjectBuilder();
        JSONObject jsonObject = new JSONObject();
        worldLoaderSaver.forEach(new BiConsumer<String, WorldCreator>() {
            @Override
            public void accept(String s, WorldCreator worldCreator) {
                jsonObject.put(s, getCreatorJSON(worldCreator));
            }
        });
        //return builder.build();
        return jsonObject;
    }

    //Map Interactions

    public static WorldCreator getCreator(String worldname) {
        return worldLoaderSaver.get(worldname);
    }

    public static void setCreator(WorldCreator worldCreator) {
        worldLoaderSaver.put(worldCreator.name(), worldCreator);
    }

    public static void removeCreator(String worldname) {
        worldLoaderSaver.remove(worldname);
    }

    public static List<WorldCreator> getAllCreators() {
        List<WorldCreator> result = new ArrayList<>();
        worldLoaderSaver.forEach(new BiConsumer<String, WorldCreator>() {
            @Override
            public void accept(String s, WorldCreator creator) {
                result.add(creator);
            }
        });
        return result;
    }

    public static void clearAllCreators() {
        worldLoaderSaver.clear();
    }

    //Conversion

    public static JSONObject getCreatorJSON(WorldCreator creator) {
        //JsonObjectBuilder creatorJsonBuilder = Json.createObjectBuilder();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("environment", creator.environment().toString());
        jsonObject.put("worldtype", creator.type().toString());
        jsonObject.put("structures", creator.generateStructures());
        jsonObject.put("seed", Long.toString(creator.seed()));
        jsonObject.put("generatorsettings", creator.generatorSettings());
        if (creator.generator() instanceof ChunkGeneratorWithID) {
            jsonObject.put("generator", ((ChunkGeneratorWithID) creator.generator()).id);
        }
        //return creatorJsonBuilder.build();
        return jsonObject;
    }

    public static WorldCreator getCreatorFromJSON(String worldname, JSONObject creatorJSON) {
        WorldCreator creator = new WorldCreator(worldname);
        creator.environment(World.Environment.valueOf((String) creatorJSON.get("environment")));
        creator.type(WorldType.valueOf((String) creatorJSON.get("worldtype")));
        creator.generateStructures((Boolean) creatorJSON.get("structures"));
        creator.seed(Long.parseLong((String) creatorJSON.get("seed")));
        String generator;
        if ((generator = (String) creatorJSON.get("generator")) != null) {
            creator.generator(generator);
        }
        creator.generatorSettings((String) creatorJSON.get("generatorsettings"));
        return creator;
    }
}
