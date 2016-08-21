package com.pie.tlatoani.Generator;

import ch.njol.skript.lang.TriggerItem;
import com.pie.tlatoani.Util.BaseEvent;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

/**
 * Created by Tlatoani on 8/21/16.
 */
public class SkriptChunkGenerationEvent extends BaseEvent {
    public final int x;
    public final int z;
    public final World world;
    public final ChunkGenerator.ChunkData chunkData;
    public final Random random;
    public final ChunkGenerator.BiomeGrid biomeGrid;
    public TriggerItem generation;

    public SkriptChunkGenerationEvent(int x, int z, World world, ChunkGenerator.ChunkData chunkData, Random random, ChunkGenerator.BiomeGrid biomeGrid) {
        this.x = x;
        this.z = z;
        this.world = world;
        this.chunkData = chunkData;
        this.random = random;
        this.biomeGrid = biomeGrid;
    }
}
