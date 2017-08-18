package com.pie.tlatoani.WorldManagement.WorldLoader;

import com.pie.tlatoani.Generator.ChunkGeneratorWithID;
import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.Util.Logging;
import com.pie.tlatoani.WorldCreator.WorldCreatorData;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * Created by Tlatoani on 7/3/16.
 */
public final class WorldLoader {
    private static Map<String, WorldCreatorData> worldLoaderSaver = new HashMap<>();
    private final static String FILENAME = "worldloader.json";

    private WorldLoader() {} //Cannot be initialized

    public static void load() {
        try {
            readJSONObject(getLoaderFile()).forEach((key, value) -> {
                String s = (String) key;
                JSONObject jsonObject = (JSONObject) value;
                WorldCreatorData creator = WorldCreatorData.fromJSON(s, jsonObject).get();
                setCreator(creator);
                creator.createWorld();
            });
            if (worldLoaderSaver.isEmpty()) {
                Logging.info("No worlds were assigned to load automatically");
            } else {
                Logging.info("Worlds to automatically load were loaded successfully!");
            }
        } catch (ParseException | IOException | ClassCastException | NoSuchElementException e) {
            Logging.info("MundoSK encountered problems while reading the file for automatically loaded worlds");
            Logging.info("Any worlds set to automatically load were not loaded");
            Logging.reportException(WorldLoader.class, e);
        }
    }

    public static void save() throws IOException {
        FileWriter writer = new FileWriter(getLoaderFile());
        writer.write(getJSONOfData().toString());
        writer.flush();
        writer.close();
    }

    public static File getLoaderFile() throws IOException {
        File result = new File(Mundo.INSTANCE.getDataFolder().getAbsolutePath() + File.separator + FILENAME);
        if (!result.exists()) {
            result.createNewFile();
            JSONObject emptyObject = new JSONObject();
            FileWriter writer = new FileWriter(result);
            writer.write(emptyObject.toString());
            writer.flush();
            writer.close();
        }
        return result;
    }

    public static JSONObject readJSONObject(File jsonFile) throws IOException, ParseException, ClassCastException {
        return (JSONObject) new JSONParser().parse(new FileReader(jsonFile));
    }

    public static JSONObject getJSONOfData() {
        JSONObject jsonObject = new JSONObject();
        worldLoaderSaver.forEach((s, creator) -> jsonObject.put(s, creator.toJSON()));
        return jsonObject;
    }

    //Map Interactions

    public static WorldCreatorData getCreator(String worldname) {
        return worldLoaderSaver.get(worldname);
    }

    public static void setCreator(WorldCreatorData creator) {
        worldLoaderSaver.put(creator.name, creator);
    }

    public static void removeCreator(String worldname) {
        worldLoaderSaver.remove(worldname);
    }

    public static WorldCreatorData[] getAllCreators() {
        return worldLoaderSaver.values().toArray(new WorldCreatorData[0]);
    }

    public static Iterator<WorldCreatorData> getCreatorIterator() {
        return worldLoaderSaver.values().iterator();
    }

    public static void clearAllCreators() {
        worldLoaderSaver.clear();
    }

    //Conversion

   /* public static JSONObject getCreatorJSON(WorldCreator creator) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("environment", creator.environment().toString());
        jsonObject.put("worldtype", creator.type().toString());
        jsonObject.put("structures", creator.generateStructures());
        jsonObject.put("seed", Long.toString(creator.seed()));
        jsonObject.put("generatorsettings", creator.generatorSettings());
        if (creator.generator() instanceof ChunkGeneratorWithID) {
            jsonObject.put("generator", ((ChunkGeneratorWithID) creator.generator()).id);
        }
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
            creator.generator(ChunkGeneratorWithID.getGenerator(generator));
        }
        creator.generatorSettings((String) creatorJSON.get("generatorsettings"));
        return creator;
    }*/
}
