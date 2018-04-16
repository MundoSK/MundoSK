package com.pie.tlatoani.Generator;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.TriggerItem;
import com.pie.tlatoani.Util.Skript.ScopeUtil;

import java.util.Optional;

/**
 * Created by Tlatoani on 8/11/17.
 */
public class GeneratorFunctionality {
    public final String id;

    private boolean loaded = false;

    public Optional<TriggerItem> initiation = Optional.empty();
    public Optional<TriggerItem> generation = Optional.empty();
    public Optional<TriggerItem> population = Optional.empty();

    public GeneratorFunctionality() {
        this(null);
    }

    public GeneratorFunctionality(String id) {
        this.id = id;
    }

    public static class Nebula {
        public Optional<SectionNode> initiation = Optional.empty();
        public Optional<SectionNode> generation = Optional.empty();
        public Optional<SectionNode> population = Optional.empty();
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void load(Nebula nebula) {
        loaded = true;
        initiation = nebula.initiation.flatMap(sectionNode -> {
            ScriptLoader.setCurrentEvent("GeneratorInitiation", GeneratorEvent.Initiation.class);
            return ScopeUtil.loadSectionNode(sectionNode, null);
        });
        generation = nebula.generation.flatMap(sectionNode -> {
            ScriptLoader.setCurrentEvent("GeneratorGeneration", GeneratorEvent.Generation.class);
            return ScopeUtil.loadSectionNode(sectionNode, null);
        });
        population = nebula.population.flatMap(sectionNode -> {
            ScriptLoader.setCurrentEvent("GeneratorPopulation", GeneratorEvent.Population.class);
            return ScopeUtil.loadSectionNode(sectionNode, null);
        });
    }

    public void unload() {
        loaded = false;
        initiation = Optional.empty();
        generation = Optional.empty();
        population = Optional.empty();
    }

    public String toString() {
        return "GeneratorFunctionality(TriggerItems: " +
                initiation + "," +
                generation + "," +
                population + ")";
    }
}