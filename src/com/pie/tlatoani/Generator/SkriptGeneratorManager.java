package com.pie.tlatoani.Generator;

import ch.njol.skript.lang.Conditional;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.TriggerSection;
import com.pie.tlatoani.Util.CustomScope;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.function.Consumer;

import static com.pie.tlatoani.Util.CustomScope.condition;

/**
 * Created by Tlatoani on 7/4/16.
 */
public final class SkriptGeneratorManager {
    private static Map<String, SkriptGenerator>  skriptGeneratorMap = new HashMap<>();
    private static ArrayList<String> countedIDs = new ArrayList<>();

    private SkriptGeneratorManager() {} //Cannot be initialized

    /*
    For the information of whoever is reading this:
    This method gives out empty SkriptChunkGenerators for use in worlds
    So that they can be modified later
    When Skript has loaded
    To include the triggers that do the actual generating
     */
    public static SkriptGenerator getSkriptGenerator(String id) {
        return skriptGeneratorMap.computeIfAbsent(id, k -> new SkriptGenerator());
    }

    static void unregisterAllSkriptGenerators() {
        skriptGeneratorMap.forEach((id, generator) -> {
            generator.trigger = null;
            generator.generation = null;
            generator.population = null;
        });
    }

    /*public static boolean addID(String id) {
        if (countedIDs.contains(id)) {
            return false;
        }
        countedIDs.add(id);
        return true;
    }

    public static void finalizeID(String id) {
        countedIDs.remove(id);
    }

    public static void registerTriggers(Collection<Trigger> triggers) {
        for (Trigger trigger : triggers) {
            String generatorID = ((EvtChunkGenerator) trigger.getEvent()).getGeneratorID();
            SkriptGenerator generator = SkriptGeneratorManager.getSkriptGenerator(generatorID);
            generator.trigger = trigger;
            try {
                TriggerItem going = (TriggerItem) CustomScope.firstitem.get(trigger);
                while (going != null) {
                    if (going instanceof Conditional) {
                        Object goingcond = condition.get((TriggerSection) going);
                        if (goingcond instanceof ScopeGeneration) {
                            ((ScopeGeneration) goingcond).setScope((Conditional) going);
                            generator.generation = ((CustomScope) goingcond).getFirst();
                        } else if (goingcond instanceof ScopePopulation) {
                            ((ScopePopulation) goingcond).setScope((Conditional) going);
                            generator.population = ((CustomScope) goingcond).getFirst();
                        }
                    }
                    going = going.getNext();
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        countedIDs.clear();
    }*/
}
