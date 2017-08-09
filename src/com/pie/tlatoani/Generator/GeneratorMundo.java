package com.pie.tlatoani.Generator;

import ch.njol.skript.lang.ExpressionType;
import com.pie.tlatoani.Miscellaneous.Random.ExprNewRandom;
import com.pie.tlatoani.Mundo;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

/**
 * Created by Tlatoani on 8/8/17.
 */
public class GeneratorMundo {
    
    public static void load() {
        Mundo.registerType(ChunkGenerator.ChunkData.class, "chunkdata");
        Mundo.registerType(ChunkGenerator.BiomeGrid.class, "biomegrid");
        Mundo.registerType(Random.class, "random").defaultExpression((new ExprNewRandom()).setDefault());
        Mundo.registerEffect(EffSetRegionInChunkData.class,
                "fill region from %number%, %number%, %number% to %number%, %number%, %number% in %chunkdata% with %itemstack%",
                "fill layer %number% in %chunkdata% with %itemstack%",
                "fill layers %number% to %number% in %chunkdata% with %itemstack%");
        Mundo.registerEvent("World Generator", EvtChunkGenerator.class, SkriptGeneratorEvent.class, "[custom] [(world|chunk)] generator %string%");
        Mundo.registerEventValue(SkriptGeneratorEvent.class, World.class, event -> event.world);
        Mundo.registerEventValue(SkriptGeneratorEvent.class, World.class, e -> e.world);
        Mundo.registerEventValue(SkriptGeneratorEvent.class, Random.class, e -> e.random);
        Mundo.registerEventValue(SkriptGeneratorEvent.class, ChunkGenerator.BiomeGrid.class, e -> e.biomeGrid);
        Mundo.registerEventValue(SkriptGeneratorEvent.class, Chunk.class, e -> e.chunk);
        Mundo.registerExpression(ExprCurrentChunkCoordinate.class, Number.class, ExpressionType.SIMPLE, "current x", "current z");
        Mundo.registerExpression(ExprMaterialInChunkData.class, ItemStack.class, ExpressionType.PROPERTY, "material at %number%, %number%, %number% in %chunkdata%");
        Mundo.registerExpression(ExprBiomeInGrid.class, Biome.class, ExpressionType.PROPERTY, "biome at %number%, %number% in grid %biomegrid%");
        Mundo.registerScope(ScopeGeneration.class, "generation");
        Mundo.registerScope(ScopePopulation.class, "population");
    }
}
