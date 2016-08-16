package com.pie.tlatoani.Generator;

import com.pie.tlatoani.CodeBlock.CodeBlock;
import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.Util.EmptyEvent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

/**
 * Created by Tlatoani on 7/3/16.
 */
public class SkriptChunkGenerator extends ChunkGenerator {
    private CodeBlock generateChunkData;
    private CodeBlock getFixedSpawnLocation;

    public CodeBlock getGenerateChunkData() {
        return generateChunkData;
    }

    public void setGenerateChunkData(CodeBlock generateChunkData) {
        this.generateChunkData = generateChunkData;
    }

    public CodeBlock getGetFixedSpawnLocation() {
        return getFixedSpawnLocation;
    }

    public void setGetFixedSpawnLocation(CodeBlock getFixedSpawnLocation) {
        this.getFixedSpawnLocation = getFixedSpawnLocation;
    }

    @Override
    public ChunkData generateChunkData(World world, Random random, int x, int z, ChunkGenerator.BiomeGrid biome) {
        ChunkData result = createChunkData(world);
        if (generateChunkData != null) {
            EmptyEvent event = new EmptyEvent();
            generateChunkData.execute(new Object[]{x, z, result, world, random, biome}); //This must be the ordering of arguments
            Mundo.debug(this, "5, 1, 5:: " + result.getTypeAndData(5, 1, 5));
        }
        return result;
    }

    @Override
    public Location getFixedSpawnLocation(World world, Random random) {
        if (getFixedSpawnLocation == null) {
            return null;
        }
        Object result = getFixedSpawnLocation.execute(new Object[]{world, random});
        Mundo.debug(this, "REUSLT:: " + result);
        return result instanceof Location ? (Location) result : null;
    }
}
