package com.pie.tlatoani.Generator;

import ch.njol.skript.lang.Trigger;
import com.pie.tlatoani.Util.CustomScope;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.function.Consumer;

/**
 * Created by Tlatoani on 7/4/16.
 */
public final class ChunkGeneratorManager {
    private static Map<String, OldSkriptChunkGenerator> oldSkriptGeneratorMap = new HashMap<>();

    private static Map<String, SkriptChunkGenerator>  skriptGeneratorMap = new HashMap<>();
    private static ArrayList<String> countedIDs = new ArrayList<>();

    private ChunkGeneratorManager() {} //Cannot be initialized

    /*
    For the information of whoever is reading this:
    This method gives out empty SkriptChunkGenerators for use in worlds
    So that they can be modified later
    When Skript has loaded
    To include the CodeBlocks that do the actual generating
     */
    public static OldSkriptChunkGenerator getOldSkriptGenerator(String id) {
        if (!oldSkriptGeneratorMap.containsKey(id)) {
            oldSkriptGeneratorMap.put(id, new OldSkriptChunkGenerator());
        }
        return oldSkriptGeneratorMap.get(id);
    }

    //

    public static SkriptChunkGenerator getSkriptGenerator(String id) {
        if (!skriptGeneratorMap.containsKey(id)) {
            skriptGeneratorMap.put(id, new SkriptChunkGenerator());
        }
        return skriptGeneratorMap.get(id);
    }

    //Determines whether a generatorID has already been added, to avoid multiple generators with the same ID
    public static boolean addID(String id) {
        if (countedIDs.contains(id)) {
            return false;
        }
        countedIDs.add(id);
        CustomScope.querySetScope();
        return true;
    }

    public static void registerTriggers(Collection<Trigger> triggers) {
        triggers.forEach(new Consumer<Trigger>() {
            @Override
            public void accept(Trigger trigger) {
                String generatorID = ((EvtChunkGenerator) trigger.getEvent()).getGeneratorID();
                ChunkGeneratorManager.getSkriptGenerator(generatorID).trigger = trigger;
            }
        });
        countedIDs.clear();
    }
}
