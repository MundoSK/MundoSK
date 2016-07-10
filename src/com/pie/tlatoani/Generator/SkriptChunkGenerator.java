package com.pie.tlatoani.Generator;

import com.pie.tlatoani.CodeBlock.SkriptCodeBlock;
import com.pie.tlatoani.Util.EmptyEvent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

/**
 * Created by Tlatoani on 7/3/16.
 */
public class SkriptChunkGenerator extends ChunkGenerator {
    private SkriptCodeBlock generateChunkData;
    private SkriptCodeBlock getFixedSpawnLocation;

    public SkriptCodeBlock getGenerateChunkData() {
        return generateChunkData;
    }

    public void setGenerateChunkData(SkriptCodeBlock generateChunkData) {
        this.generateChunkData = generateChunkData;
    }

    public SkriptCodeBlock getGetFixedSpawnLocation() {
        return getFixedSpawnLocation;
    }

    public void setGetFixedSpawnLocation(SkriptCodeBlock getFixedSpawnLocation) {
        this.getFixedSpawnLocation = getFixedSpawnLocation;
    }

    @Override
    public ChunkData generateChunkData(World world, Random random, int x, int z, ChunkGenerator.BiomeGrid biome) {
        if (generateChunkData == null) {
            return createChunkData(world);
        }
        EmptyEvent event = new EmptyEvent();
        ChunkData result = createChunkData(world);
        event.setLocalVariable("chunkdata", result);
        event.setLocalVariable("world", world);
        event.setLocalVariable("random", random);
        event.setLocalVariable("x", x);
        event.setLocalVariable("z", z);
        event.setLocalVariable("biomegrid", biome);
        generateChunkData.execute(event);
        return result;
    }

    @Override
    public Location getFixedSpawnLocation(World world, Random random) {
        if (getFixedSpawnLocation == null) {
            return null;
        }
        EmptyEvent event = new EmptyEvent();
        event.setLocalVariable("world", world);
        event.setLocalVariable("random", random);
        return (Location) event.getLocalVariable("spawn");
    }
}
