package com.pie.tlatoani.WorldManagement;

import com.pie.tlatoani.Generator.ChunkGeneratorManager;
import com.pie.tlatoani.Json.API.*;
import com.pie.tlatoani.Mundo;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.generator.ChunkGenerator;

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
        getCreatorJsonsFromFile(getLoaderFile()).forEach(new BiConsumer<String, JsonObject>() {
            @Override
            public void accept(String s, JsonObject jsonObject) {
                WorldCreator creator = getCreatorFromJson(s, jsonObject);
                addCreator(creator);
                creator.createWorld();
            }
        });
    }

    public static void save() throws IOException {
        FileWriter writer = new FileWriter(getLoaderFile());
        writer.write(getLoaderMapJson().toString());
        writer.flush();
        writer.close();
    }

    public static File getLoaderFile() throws IOException {
        File result = new File(Mundo.pluginFolder + File.separator + filename);
        if (!result.exists()) {
            result.createNewFile();
            JsonObject emptyObject = Json.createObjectBuilder().build();
            FileWriter writer = new FileWriter(result);
            writer.write(emptyObject.toString());
            writer.flush();
            writer.close();
        }
        return result;
    }

    public static Map<String, JsonObject> getCreatorJsonsFromFile(File jsonFile) throws FileNotFoundException {
        JsonReader reader = Json.createReader(new FileReader(jsonFile));
        JsonObject worldLoaders = reader.readObject();
        Map<String, JsonObject> creatorJsons = new HashMap<>();
        worldLoaders.forEach(new BiConsumer<String, JsonValue>() {
            @Override
            public void accept(String s, JsonValue jsonValue) {
                creatorJsons.put(s, (JsonObject) jsonValue);
            }
        });
        return creatorJsons;
    }

    public static JsonObject getLoaderMapJson() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        worldLoaderSaver.forEach(new BiConsumer<String, WorldCreator>() {
            @Override
            public void accept(String s, WorldCreator worldCreator) {
                builder.add(s, getCreatorJson(worldCreator));
            }
        });
        return builder.build();
    }

    //Map Interactions

    public static WorldCreator getCreator(String worldname) {
        return worldLoaderSaver.get(worldname);
    }

    public static void addCreator(WorldCreator worldCreator) {
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

    //Conversion

    public static JsonObject getCreatorJson(WorldCreator creator) {
        JsonObjectBuilder creatorJsonBuilder = Json.createObjectBuilder();
        creatorJsonBuilder.add("environment", creator.environment().toString());
        creatorJsonBuilder.add("worldtype", creator.type().toString());
        creatorJsonBuilder.add("structures", creator.generateStructures());
        creatorJsonBuilder.add("seed", Long.toString(creator.seed()));
        creatorJsonBuilder.add("generatorsettings", creator.generatorSettings());
        String generator;
        if ((generator = ChunkGeneratorManager.getGeneratorName(creator.generator())) != null) {
            creatorJsonBuilder.add("generator", generator);
        }
        return creatorJsonBuilder.build();
    }

    public static WorldCreator getCreatorFromJson(String worldname, JsonObject creatorJson) {
        WorldCreator creator = new WorldCreator(worldname);
        creator.environment(World.Environment.valueOf(creatorJson.getString("environment")));
        creator.type(WorldType.valueOf(creatorJson.getString("worldtype")));
        creator.generateStructures(creatorJson.getBoolean("structures"));
        creator.seed(Long.parseLong(creatorJson.getString("seed")));
        String generator;
        if ((generator = creatorJson.getString("generator", null)) != null) {
            creator.generator(generator);
        }
        creator.generatorSettings(creatorJson.getString("generatorsettings"));
        return creator;
    }
}
