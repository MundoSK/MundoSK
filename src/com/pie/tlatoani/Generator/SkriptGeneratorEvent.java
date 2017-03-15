package com.pie.tlatoani.Generator;

import ch.njol.skript.lang.TriggerItem;
import com.pie.tlatoani.Util.BaseEvent;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.world.ChunkEvent;
import org.bukkit.event.world.WorldEvent;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

/**
 * Created by Tlatoani on 8/21/16.
 */
public class SkriptGeneratorEvent extends BaseEvent {
    public final Integer x;
    public final Integer z;
    public final World world;
    public final ChunkGenerator.ChunkData chunkData;
    public final Random random;
    public final ChunkGenerator.BiomeGrid biomeGrid;
    public final Chunk chunk;

    public SkriptGeneratorEvent(int x, int z, World world, ChunkGenerator.ChunkData chunkData, Random random, ChunkGenerator.BiomeGrid biomeGrid) {
        this.x = x;
        this.z = z;
        this.world = world;
        this.chunkData = chunkData;
        this.random = random;
        this.biomeGrid = biomeGrid;
        this.chunk = null;
    }

    public SkriptGeneratorEvent(World world, Chunk chunk, Random random) {
        this.x = null;
        this.z = null;
        this.world = world;
        this.chunkData = null;
        this.random = random;
        this.biomeGrid = null;
        this.chunk = chunk;
    }
}
