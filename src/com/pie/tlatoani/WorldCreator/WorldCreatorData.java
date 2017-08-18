package com.pie.tlatoani.WorldCreator;

import com.pie.tlatoani.Generator.ChunkGeneratorWithID;
import com.pie.tlatoani.Util.MundoUtil;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.generator.ChunkGenerator;
import org.json.simple.JSONObject;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Random;

/**
 * Created by Tlatoani on 8/18/17.
 */
public class WorldCreatorData {
    public final String name;
    public final Dimension dimension;
    public final WorldType type;
    public final long seed;
    public final Optional<ChunkGenerator> generator;
    public final String generatorSettings;
    public final boolean structures;

    public WorldCreatorData(
            String name,
            @Nullable Dimension dimension,
            @Nullable Long seed, @Nullable WorldType type,
            @Nullable Optional<ChunkGenerator> generator,
            @Nullable String generatorSettings,
            @Nullable Boolean structures
    ) {
        if (name == null) {
            throw new IllegalArgumentException("The name of a Creator cannot be null!");
        }
        this.name = name;
        this.dimension = Optional.ofNullable(dimension).orElse(Dimension.NORMAL);
        this.type = Optional.ofNullable(type).orElse(WorldType.NORMAL);
        this.seed = Optional.ofNullable(seed).orElseGet(() -> new Random().nextLong());
        this.generator = generator;
        this.generatorSettings = Optional.ofNullable(generatorSettings).orElse("");
        this.structures = Optional.ofNullable(structures).orElse(true);
    }

    public static WorldCreatorData withGeneratorID(
            String name,
            @Nullable Dimension dimension,
            @Nullable Long seed, @Nullable WorldType type,
            String generatorID,
            @Nullable String generatorSettings,
            @Nullable Boolean structures
    ) {
        return new WorldCreatorData(
                name,
                dimension,
                seed, type,
                Optional.ofNullable(generatorID).map(id ->ChunkGeneratorWithID.getGenerator(generatorID)),
                generatorSettings,
                structures
        );
    }

    public static WorldCreatorData fromWorld(World world) {
        return new WorldCreatorData(
                world.getName(),
                Dimension.fromEnvironment(world.getEnvironment()),
                world.getSeed(), world.getWorldType(),
                Optional.ofNullable(world.getGenerator()),
                null,
                world.canGenerateStructures()
        );
    }

    public void createWorld() {
        WorldCreator creator = new WorldCreator(name);
        creator.environment(dimension.toEnvironment());
        creator.type(type);
        creator.seed(seed);
        generator.ifPresent(creator::generator);
        creator.generatorSettings(generatorSettings);
        creator.generateStructures(structures);
        creator.createWorld();
    }

    public Optional<String> getGeneratorID() {
        return generator
                .flatMap(chunkGenerator -> MundoUtil.cast(chunkGenerator, ChunkGeneratorWithID.class))
                .map(generatorWIthID -> generatorWIthID.id);
    }

    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("environment", dimension.toString());
        jsonObject.put("seed", Long.toString(seed));
        jsonObject.put("worldtype", type.toString());
        getGeneratorID().ifPresent(generator -> jsonObject.put("generator", generator));
        jsonObject.put("generatorsettings", generatorSettings);
        jsonObject.put("structures", structures);
        return jsonObject;
    }

    public static Optional<WorldCreatorData> fromJSON(String worldName, JSONObject jsonObject) {
        try {
            Dimension dimension = Dimension.valueOf((String) jsonObject.get("environment"));
            Long seed = Long.parseLong((String) jsonObject.get("seed"));
            WorldType type = WorldType.valueOf((String) jsonObject.get("worldtype"));
            String generatorID = (String) jsonObject.get("generator");
            String generatorSettings = (String) jsonObject.get("generatorsettings");
            Boolean structures = (Boolean) jsonObject.get("structures");
            return Optional.of(withGeneratorID(worldName, dimension, seed, type, generatorID, generatorSettings, structures));
        } catch (ClassCastException e) {
            return Optional.empty();
        }
    }

    //Modifiers

    public WorldCreatorData setName(String name) {
        return new WorldCreatorData(name, dimension, seed, type, generator, generatorSettings, structures);
    }

    public WorldCreatorData setDimension(Dimension dimension) {
        return new WorldCreatorData(name, dimension, seed, type, generator, generatorSettings, structures);
    }

    public WorldCreatorData setSeed(Long seed) {
        return new WorldCreatorData(name, dimension, seed, type, generator, generatorSettings, structures);
    }

    public WorldCreatorData setType(WorldType type) {
        return new WorldCreatorData(name, dimension, seed, type, generator, generatorSettings, structures);
    }

    public WorldCreatorData setGenerator(Optional<ChunkGenerator> generator) {
        return new WorldCreatorData(name, dimension, seed, type, generator, generatorSettings, structures);
    }

    public WorldCreatorData setGeneratorID(String id) {
        return withGeneratorID(name, dimension, seed, type, id, generatorSettings, structures);
    }

    public WorldCreatorData setGeneratorSettings(String generatorSettings) {
        return new WorldCreatorData(name, dimension, seed, type, generator, generatorSettings, structures);
    }

    public WorldCreatorData setStructures(Boolean structures) {
        return new WorldCreatorData(name, dimension, seed, type, generator, generatorSettings, structures);
    }
}
