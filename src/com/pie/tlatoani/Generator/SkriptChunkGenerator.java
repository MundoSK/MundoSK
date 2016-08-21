package com.pie.tlatoani.Generator;

import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerItem;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

/**
 * Created by Tlatoani on 8/21/16.
 */
public class SkriptChunkGenerator extends ChunkGenerator {
    public Trigger trigger = null;

    @Override
    public ChunkData generateChunkData(World world, Random random, int x, int z, ChunkGenerator.BiomeGrid biome) {
        ChunkData chunkData = createChunkData(world);
        SkriptChunkGenerationEvent event = new SkriptChunkGenerationEvent(x, z, world, chunkData, random, biome);
        TriggerItem.walk(trigger, event);
        return chunkData;
    }
}
