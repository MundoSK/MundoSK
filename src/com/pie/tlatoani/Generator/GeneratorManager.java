package com.pie.tlatoani.Generator;

import ch.njol.skript.lang.ExpressionType;
import com.pie.tlatoani.Core.Registration.Registration;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by Tlatoani on 7/4/16.
 */
public final class GeneratorManager {
    private static Map<String, SkriptGenerator> skriptGeneratorMap = new HashMap<>();

    public static void load() {
        Registration.registerType(ChunkGenerator.ChunkData.class, "chunkdata")
                .document("Chunk Data", "1.7", "Represents a 3-dimensional grid of itemstacks. "
                        + "Used in world generators to manipulate the blocks in the chunk to be generated.");
        Registration.registerType(ChunkGenerator.BiomeGrid.class, "biomegrid")
                .document("Biome Grid", "1.7", "Represents a 2-dimensional grid of biomes. "
                        + "Used in world generators to manipulate the biome(s) in the chunk to be generated.");
        Registration.registerEffect(EffSetRegionInChunkData.class,
                "fill region from %number%, %number%, %number% to %number%, %number%, %number% in %chunkdata% with %itemstack%",
                "fill layer %number% in %chunkdata% with %itemstack%",
                "fill layers %number% to %number% in %chunkdata% with %itemstack%")
                .document("Fill Region in ChunkData", "1.7", "Fills a region in the specified chunkdata with the specified material:"
                        + "1. Fills a region between two coordinates in the specified chunkdata"
                        + "2. Fills the specified layer of the specified chunkdata"
                        + "3. Fills the layers from the first to the second specified layer in the specified chunkdata");
        Registration.registerEvent("Generator", ScopeGenerator.class, GeneratorEvent.class, "[custom] [(world|chunk)] generator %string%")
                .document("Custom Generator", "1.7", "Not an actual event, but rather an event-level scope used to code a custom generator, with the specified ID. "
                        + "Under the main \"event\" line you can have three different sub-scopes that handle generation:"
                        , "initiation: This is called once at the beginning of a world's generation. "
                        + "This is specifically intended for optionally setting the world's spawn, but can also be used for other things you need to do at the start of generation."
                        , "generation: This is called whenever a chunk is being generated. All custom generators should use this to do their main generation of the world, "
                        + "using the given chunkdata to set the materials of the blocks in the chunk."
                        , "population: This is called after a chunk and all adjacent chunks (not including ones that are diagonal) have been generated, "
                        + "to add certain blocks on top which may require (or be made easier) by being able to put blocks on more than one chunk (ex. adding flowers).")
                .eventValue(World.class, "1.7", "The world that is being generated.")
                .eventValue(Random.class, "1.7", "A pseudo-random number generator created from the seed that can be used to randomize generation.")
                .eventValue(ChunkGenerator.ChunkData.class, "1.7", "In 'generation', this is the chunkdata object used to specify the materials of the blocks in the chunk.")
                .eventValue(ChunkGenerator.BiomeGrid.class, "1.7", "In 'generation', this is the biomegrid object used to specify the biome(s) in the chunk.")
                .eventValue(Chunk.class, "1.7", "In 'population', this is the chunk that is being populated (along with possibly its adjacent chunks). "
                        + "Note that this does not exist for 'generation' as the chunk has not yet been generated at that point.");
        Registration.registerEventValue(GeneratorEvent.class, World.class, event -> event.world);
        Registration.registerEventValue(GeneratorEvent.class, Random.class, event -> event.random);
        Registration.registerEventValue(GeneratorEvent.Generation.class, ChunkGenerator.ChunkData.class, event -> event.chunkData);
        Registration.registerEventValue(GeneratorEvent.Generation.class, ChunkGenerator.BiomeGrid.class, event -> event.biomeGrid);
        Registration.registerEventValue(GeneratorEvent.Population.class, Chunk.class, event -> event.chunk);
        Registration.registerExpression(ExprCurrentChunkCoordinate.class, Number.class, ExpressionType.SIMPLE, "current x", "current z")
                .document("Current Chunk Coordinates", "1.7.2", "An expression, used in the 'generation' section of a custom generator, for a coordinate of the chunk currently being generated");
        Registration.registerExpression(ExprMaterialInChunkData.class, ItemStack.class, ExpressionType.PROPERTY, "material at %number%, %number%, %number% in %chunkdata%")
                .document("Material in Chunk Data", "1.7", "An expression for the material the specified coordinates in the specified chunkdata.");
        Registration.registerExpression(ExprBiomeInGrid.class, Biome.class, ExpressionType.PROPERTY, "biome at %number%, %number% in grid %biomegrid%")
                .document("Biome in Biome Grid", "1.7", "An expression for the biome at the specified x and z coordinates the specified biomegrid.");
    }

    public static SkriptGenerator getSkriptGenerator(String id) {
        return skriptGeneratorMap.computeIfAbsent(id, k -> new SkriptGenerator());
    }

    static void unregisterAllSkriptGenerators() {
        skriptGeneratorMap.forEach((id, generator) -> {
            generator.functionality.unload();
        });
    }
}