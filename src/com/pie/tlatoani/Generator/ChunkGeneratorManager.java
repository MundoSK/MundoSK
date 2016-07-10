package com.pie.tlatoani.Generator;

import org.bukkit.generator.ChunkGenerator;

import java.awt.*;
import java.util.Map;
import java.util.HashMap;
import java.util.function.BiConsumer;

/**
 * Created by Tlatoani on 7/4/16.
 */
public final class ChunkGeneratorManager {
    private static Map<String, SkriptChunkGenerator> skriptGeneratorMap = new HashMap<String, SkriptChunkGenerator>();
    private static Map<String, ChunkGenerator> generatorNameMap = new HashMap<String, ChunkGenerator>();

    private ChunkGeneratorManager() {} //Cannot be initialized

    /*
    For the information of whoever is reading this:
    This method gives out empty SkriptChunkGenerators for use in worlds
    So that they can be modified later
    When Skript has loaded
    To include the CodeBlocks that do the actual generating
     */
    public static SkriptChunkGenerator getSkriptGenerator(String id) {
        if (!skriptGeneratorMap.containsKey(id)) {
            skriptGeneratorMap.put(id, new SkriptChunkGenerator());
        }
        return skriptGeneratorMap.get(id);
    }

    public static void saveGenerator(String name, ChunkGenerator generator) {
        generatorNameMap.put(name, generator);
    }

    public static String getGeneratorName(ChunkGenerator chunkGenerator) {
        if (chunkGenerator == null) {
            return null;
        }
        final String[] resultarray = new String[1];
        generatorNameMap.forEach(new BiConsumer<String, ChunkGenerator>() {
            @Override
            public void accept(String s, ChunkGenerator generator) {
                if (generator == chunkGenerator) {
                    resultarray[0] = s;
                }
            }
        });
        return resultarray[0];
    }
}
