package com.pie.tlatoani.WorldCreator;

import com.pie.tlatoani.Generator.ChunkGeneratorWithID;
import com.pie.tlatoani.Core.Static.OptionalUtil;
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
    public final Optional<String> name;
    public final Dimension dimension;
    public final WorldType type;
    public final Optional<String> seed;
    public final Optional<ChunkGenerator> generator;
    public final String generatorSettings;
    public final boolean structures;

    public final Optional<Long> seedLong;

    public WorldCreatorData(
            @Nullable String name,
            @Nullable Dimension dimension,
            @Nullable String seed,
            @Nullable WorldType type,
            @Nullable ChunkGenerator generator,
            @Nullable String generatorSettings,
            @Nullable Boolean structures
    ) {
        this.name = Optional.ofNullable(name);
        this.dimension = Optional.ofNullable(dimension).orElse(Dimension.NORMAL);
        this.type = Optional.ofNullable(type).orElse(WorldType.NORMAL);
        this.seed = Optional.ofNullable(seed);
        this.generator = Optional.ofNullable(generator);
        this.generatorSettings = Optional.ofNullable(generatorSettings).orElse("");
        this.structures = Optional.ofNullable(structures).orElse(true);

        this.seedLong = this.seed.map(str -> {
            try {
                return Long.parseLong(str);
            } catch (NumberFormatException e) {
                return (long) str.hashCode(); //According to minecraftwiki, this is the same way Minecraft itself determines the seed from a non-numeric string
            }
        });
    }

    public static WorldCreatorData withGeneratorID(
            @Nullable String name,
            @Nullable Dimension dimension,
            @Nullable String seed,
            @Nullable WorldType type,
            @Nullable String generatorID,
            @Nullable String generatorSettings,
            @Nullable Boolean structures
    ) {
        return new WorldCreatorData(
                name,
                dimension,
                seed,
                type,
                Optional.ofNullable(generatorID).map(ChunkGeneratorWithID::getGenerator).orElse(null),
                generatorSettings,
                structures
        );
    }

    public static WorldCreatorData fromWorld(World world) {
        if (world == null) {
            throw new NullPointerException("The world parameter should not be null");
        }
        return new WorldCreatorData(
                world.getName(),
                Dimension.fromEnvironment(world.getEnvironment()),
                Long.toString(world.getSeed()),
                world.getWorldType(),
                world.getGenerator(),
                null,
                world.canGenerateStructures()
        );
    }

    public void createWorld() {
        if (!name.isPresent()) {
            throw new UnsupportedOperationException("You must supply a name if you want to create a world using a nameless creator: " + this);
        }
        WorldCreator creator = new WorldCreator(name.get());
        creator.environment(dimension.toEnvironment());
        creator.type(type);
        creator.seed(seedLong.orElseGet(() -> new Random().nextLong()));
        generator.ifPresent(creator::generator);
        creator.generatorSettings(generatorSettings);
        creator.generateStructures(structures);
        creator.createWorld();
    }

    public void createWorld(String name) {
        if (name == null) {
            throw new NullPointerException("The name parameter should not be null");
        }
        WorldCreator creator = new WorldCreator(name);
        creator.environment(dimension.toEnvironment());
        creator.type(type);
        creator.seed(seedLong.orElseGet(() -> new Random().nextLong()));
        generator.ifPresent(creator::generator);
        creator.generatorSettings(generatorSettings);
        creator.generateStructures(structures);
        creator.createWorld();
    }

    public Optional<String> getGeneratorID() {
        return generator
                .flatMap(chunkGenerator -> OptionalUtil.cast(chunkGenerator, ChunkGeneratorWithID.class))
                .map(generatorWIthID -> generatorWIthID.id);
    }

    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("environment", dimension.toString());
        seed.ifPresent(str -> jsonObject.put("seed", str));
        jsonObject.put("worldtype", type.toString());
        getGeneratorID().ifPresent(generator -> jsonObject.put("generator", generator));
        jsonObject.put("generatorsettings", generatorSettings);
        jsonObject.put("structures", structures);
        return jsonObject;
    }

    public static Optional<WorldCreatorData> fromJSON(@Nullable String worldName, JSONObject jsonObject) {
        if (jsonObject == null) {
            throw new NullPointerException("The jsonObject parameter should not be null");
        }
        try {
            Dimension dimension = Dimension.valueOf((String) jsonObject.get("environment"));
            String seed = (String) jsonObject.get("seed");
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

    public WorldCreatorData setName(@Nullable String name) {
        return new WorldCreatorData(name, dimension, seed.orElse(null), type, generator.orElse(null), generatorSettings, structures);
    }

    public WorldCreatorData setDimension(@Nullable Dimension dimension) {
        return new WorldCreatorData(name.orElse(null), dimension, seed.orElse(null), type, generator.orElse(null), generatorSettings, structures);
    }

    public WorldCreatorData setSeed(@Nullable String seed) {
        return new WorldCreatorData(name.orElse(null), dimension, seed, type, generator.orElse(null), generatorSettings, structures);
    }

    public WorldCreatorData setType(@Nullable WorldType type) {
        return new WorldCreatorData(name.orElse(null), dimension, seed.orElse(null), type, generator.orElse(null), generatorSettings, structures);
    }

    public WorldCreatorData setGenerator(@Nullable ChunkGenerator generator) {
        return new WorldCreatorData(name.orElse(null), dimension, seed.orElse(null), type, generator, generatorSettings, structures);
    }

    public WorldCreatorData setGeneratorID(@Nullable String id) {
        return withGeneratorID(name.orElse(null), dimension, seed.orElse(null), type, id, generatorSettings, structures);
    }

    public WorldCreatorData setGeneratorSettings(@Nullable String generatorSettings) {
        return new WorldCreatorData(name.orElse(null), dimension, seed.orElse(null), type, generator.orElse(null), generatorSettings, structures);
    }

    public WorldCreatorData setStructures(@Nullable Boolean structures) {
        return new WorldCreatorData(name.orElse(null), dimension, seed.orElse(null), type, generator.orElse(null), generatorSettings, structures);
    }

    //

    public String toString() {
        return name.map(str -> str + ":").orElse("") + toJSON();
    }
}
