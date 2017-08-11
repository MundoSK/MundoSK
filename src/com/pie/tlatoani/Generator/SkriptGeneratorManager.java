package com.pie.tlatoani.Generator;

import ch.njol.skript.lang.ExpressionType;
import com.pie.tlatoani.Util.Registration;
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
public final class SkriptGeneratorManager {
    private static Map<String, SkriptGenerator> skriptGeneratorMap = new HashMap<>();

    public static void load() {
        Registration.registerType(ChunkGenerator.ChunkData.class, "chunkdata");
        Registration.registerType(ChunkGenerator.BiomeGrid.class, "biomegrid");
        Registration.registerEffect(EffSetRegionInChunkData.class,
                "fill region from %number%, %number%, %number% to %number%, %number%, %number% in %chunkdata% with %itemstack%",
                "fill layer %number% in %chunkdata% with %itemstack%",
                "fill layers %number% to %number% in %chunkdata% with %itemstack%");
        Registration.registerEvent("Generator", ScopeGenerator.class, GeneratorEvent.class, "[custom] [(world|chunk)] generator %string%");
        //Registration.registerEvent("World Generator", EvtChunkGenerator.class, SkriptGeneratorEvent.class, "[custom] [(world|chunk)] generator %string%");
        Registration.registerEventValue(GeneratorEvent.class, World.class, event -> event.world);
        Registration.registerEventValue(GeneratorEvent.class, Random.class, event -> event.random);
        Registration.registerEventValue(GeneratorEvent.Generation.class, ChunkGenerator.ChunkData.class, event -> event.chunkData);
        Registration.registerEventValue(GeneratorEvent.Generation.class, ChunkGenerator.BiomeGrid.class, event -> event.biomeGrid);
        Registration.registerEventValue(GeneratorEvent.Population.class, Chunk.class, event -> event.chunk);
        Registration.registerExpression(ExprCurrentChunkCoordinate.class, Number.class, ExpressionType.SIMPLE, "current x", "current z");
        Registration.registerExpression(ExprMaterialInChunkData.class, ItemStack.class, ExpressionType.PROPERTY, "material at %number%, %number%, %number% in %chunkdata%");
        Registration.registerExpression(ExprBiomeInGrid.class, Biome.class, ExpressionType.PROPERTY, "biome at %number%, %number% in grid %biomegrid%");
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