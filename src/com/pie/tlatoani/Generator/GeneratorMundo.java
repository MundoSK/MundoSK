package com.pie.tlatoani.Generator;

import ch.njol.skript.lang.ExpressionType;
import com.pie.tlatoani.Miscellaneous.Random.ExprNewRandom;
import com.pie.tlatoani.Util.Registration;
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
        Registration.registerType(ChunkGenerator.ChunkData.class, "chunkdata");
        Registration.registerType(ChunkGenerator.BiomeGrid.class, "biomegrid");
        Registration.registerType(Random.class, "random").defaultExpression((new ExprNewRandom()).setDefault());
        Registration.registerEffect(EffSetRegionInChunkData.class,
                "fill region from %number%, %number%, %number% to %number%, %number%, %number% in %chunkdata% with %itemstack%",
                "fill layer %number% in %chunkdata% with %itemstack%",
                "fill layers %number% to %number% in %chunkdata% with %itemstack%");
        Registration.registerEvent("World Generator", EvtChunkGenerator.class, SkriptGeneratorEvent.class, "[custom] [(world|chunk)] generator %string%");
        Registration.registerEventValue(SkriptGeneratorEvent.class, World.class, event -> event.world);
        Registration.registerEventValue(SkriptGeneratorEvent.class, World.class, e -> e.world);
        Registration.registerEventValue(SkriptGeneratorEvent.class, Random.class, e -> e.random);
        Registration.registerEventValue(SkriptGeneratorEvent.class, ChunkGenerator.BiomeGrid.class, e -> e.biomeGrid);
        Registration.registerEventValue(SkriptGeneratorEvent.class, Chunk.class, e -> e.chunk);
        Registration.registerExpression(ExprCurrentChunkCoordinate.class, Number.class, ExpressionType.SIMPLE, "current x", "current z");
        Registration.registerExpression(ExprMaterialInChunkData.class, ItemStack.class, ExpressionType.PROPERTY, "material at %number%, %number%, %number% in %chunkdata%");
        Registration.registerExpression(ExprBiomeInGrid.class, Biome.class, ExpressionType.PROPERTY, "biome at %number%, %number% in grid %biomegrid%");
        Registration.registerScope(ScopeGeneration.class, "generation");
        Registration.registerScope(ScopePopulation.class, "population");
    }
}
