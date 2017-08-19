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
    public final Optional<Long> seed;
    public final Optional<ChunkGenerator> generator;
    public final String generatorSettings;
    public final boolean structures;

    public WorldCreatorData(
            String name,
            @Nullable Dimension dimension,
            Optional<Long> seed,
            @Nullable WorldType type,
            Optional<ChunkGenerator> generator,
            @Nullable String generatorSettings,
            @Nullable Boolean structures
    ) {
        if (name == null) {
            throw new IllegalArgumentException("The name of a Creator cannot be null!");
        }
        this.name = name;
        this.dimension = Optional.ofNullable(dimension).orElse(Dimension.NORMAL);
        this.type = Optional.ofNullable(type).orElse(WorldType.NORMAL);
        this.seed = seed;
        this.generator = generator;
        this.generatorSettings = Optional.ofNullable(generatorSettings).orElse("");
        this.structures = Optional.ofNullable(structures).orElse(true);
    }

    public static WorldCreatorData withGeneratorID(
            String name,
            @Nullable Dimension dimension,
            Optional<Long> seed,
            @Nullable WorldType type,
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
                Optional.of(world.getSeed()),
                world.getWorldType(),
                Optional.ofNullable(world.getGenerator()),
                null,
                world.canGenerateStructures()
        );
    }

    public void createWorld() {
        WorldCreator creator = new WorldCreator(name);
        creator.environment(dimension.toEnvironment());
        creator.type(type);
        creator.seed(seed.orElseGet(() -> new Random().nextLong()));
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
        seed.ifPresent(seedLong -> jsonObject.put("seed", Long.toString(seedLong)));
        jsonObject.put("worldtype", type.toString());
        getGeneratorID().ifPresent(generator -> jsonObject.put("generator", generator));
        jsonObject.put("generatorsettings", generatorSettings);
        jsonObject.put("structures", structures);
        return jsonObject;
    }

    public static Optional<WorldCreatorData> fromJSON(String worldName, JSONObject jsonObject) {
        try {
            Dimension dimension = Dimension.valueOf((String) jsonObject.get("environment"));
            Optional<Long> seed = Optional.ofNullable((String) jsonObject.get("seed")).map(Long::parseLong);
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

    public WorldCreatorData setSeed(Optional<Long> seed) {
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
