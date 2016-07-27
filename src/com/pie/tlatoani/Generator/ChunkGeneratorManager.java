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
}
