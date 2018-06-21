package com.pie.tlatoani.WorldManagement.WorldLoader;

import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.Core.Static.Logging;
import com.pie.tlatoani.WorldCreator.WorldCreatorData;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by Tlatoani on 7/3/16.
 */
public final class WorldLoader {
    private static Map<String, WorldCreatorData> worldLoaderSaver = new HashMap<>();
    private final static String FILENAME = "worldloader.json";

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
        File result = new File(Mundo.get().getDataFolder().getAbsolutePath() + File.separator + FILENAME);
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
        if (!creator.name.isPresent()) {
            throw new IllegalArgumentException("You cannot set a nameless creator as automatic!");
        }
        worldLoaderSaver.put(creator.name.get(), creator);
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
}
