package com.pie.tlatoani.Generator;

import com.pie.tlatoani.Util.Skript.BaseEvent;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

/**
 * Created by Tlatoani on 8/11/17.
 */
public class GeneratorEvent extends BaseEvent {
    public final World world;
    public final Random random;

    public GeneratorEvent(World world, Random random) {
        this.world = world;
        this.random = random;
    }

    public static class Initiation extends GeneratorEvent {

        public Initiation(World world, Random random) {
            super(world, random);
        }
    }

    public static class Generation extends GeneratorEvent {
        public final Integer x;
        public final Integer z;
        public final ChunkGenerator.ChunkData chunkData;
        public final ChunkGenerator.BiomeGrid biomeGrid;

        public Generation(World world, Random random, Integer x, Integer z, ChunkGenerator.ChunkData chunkData, ChunkGenerator.BiomeGrid biomeGrid) {
            super(world, random);
            this.x = x;
            this.z = z;
            this.chunkData = chunkData;
            this.biomeGrid = biomeGrid;
        }
    }

    public static class Population extends GeneratorEvent {
        public final Chunk chunk;

        public Population(World world, Random random, Chunk chunk) {
            super(world, random);
            this.chunk = chunk;
        }
    }
}
