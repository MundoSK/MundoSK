package com.pie.tlatoani.Generator;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Tlatoani on 7/26/16.
 */
public class ChunkGeneratorWithID extends ChunkGenerator {
    public final String id;
    public final ChunkGenerator wrappedGenerator;

    private ChunkGeneratorWithID(String id, ChunkGenerator wrappedGenerator) {
        this.id = id;
        this.wrappedGenerator = wrappedGenerator;
    }

    public static ChunkGeneratorWithID getGenerator(String id) {
        WorldCreator worldCreator = new WorldCreator("util");
        ChunkGenerator generator = worldCreator.generator(id).generator();
        return generator == null ? null : new ChunkGeneratorWithID(id, generator);
    }

    @Override
    public ChunkGenerator.ChunkData generateChunkData(World world, Random random, int x, int z, ChunkGenerator.BiomeGrid biome) {
        return wrappedGenerator.generateChunkData(world, random, x, z, biome);
    }

    @Override
    public boolean canSpawn(World world, int x, int z) {
        return wrappedGenerator.canSpawn(world, x, z);
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        return wrappedGenerator.getDefaultPopulators(world);
    }

    @Override
    public Location getFixedSpawnLocation(World world, Random random) {
        return wrappedGenerator.getFixedSpawnLocation(world, random);
    }
}
