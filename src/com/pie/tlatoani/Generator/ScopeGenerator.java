package com.pie.tlatoani.Generator;

import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.log.SkriptLogger;
import com.pie.tlatoani.Core.Static.Logging;
import com.pie.tlatoani.Util.Skript.MundoEventScope;
import com.pie.tlatoani.Util.Skript.ScopeUtil;
import org.bukkit.event.Event;

import java.util.Optional;

/**
 * Created by Tlatoani on 8/11/17.
 */
public class ScopeGenerator extends MundoEventScope {
    private GeneratorFunctionality generatorFunctionality;
    private GeneratorFunctionality.Nebula nebula;

    @Override
    public void afterInit() {
        generatorFunctionality.load(nebula);
        Logging.debug(this, "registered: " + generatorFunctionality);
    }

    @Override
    public void unregister(Trigger trigger) {
        generatorFunctionality.unload();
        Logging.debug(this, "unregistered");
    }

    @Override
    public void unregisterAll() {
        GeneratorManager.unregisterAllSkriptGenerators();
    }

    @Override
    public boolean init(Literal<?>[] literals, int i, SkriptParser.ParseResult parseResult) {
        generatorFunctionality = GeneratorManager.getSkriptGenerator(((Literal<String>) literals[0]).getSingle()).functionality;
        nebula = new GeneratorFunctionality.Nebula();
        SectionNode topNode = (SectionNode) SkriptLogger.getNode();
        Logging.debug(this, "init()ing");
        try {
            if (generatorFunctionality.isLoaded()) {
                Skript.warning("You may have two 'generator' instances with the id \"" + generatorFunctionality.id + "\" in your code."
                        + " If you do, note that only one of them will be used."
                        + " If you don't, you can ignore this warning.");
                generatorFunctionality.unload();
            }
            for (Node node : topNode) {
                SkriptLogger.setNode(node);
                Logging.debug(this, "Current node: " + node.getKey());
                if (!(node instanceof SectionNode)) {
                    Skript.error("Code under 'generator' to be run initially should be put under the 'initiation' section!");
                    return false;
                }
                SectionNode subNode = (SectionNode) node;
                if (subNode.getKey().equals("initiation")) {
                    if (nebula.initiation.isPresent()) {
                        Skript.error("You cannot have two 'initiation' sections here!");
                        return false;
                    }
                    nebula.initiation = Optional.of(subNode);
                } else if (subNode.getKey().equals("generation")) {
                    if (nebula.generation.isPresent()) {
                        Skript.error("You cannot have two 'generation' sections here!");
                        return false;
                    }
                    nebula.generation = Optional.of(subNode);
                } else if (subNode.getKey().equals("population")) {
                    if (nebula.population.isPresent()) {
                        Skript.error("You cannot have two 'population' sections here!");
                        return false;
                    }
                    nebula.population = Optional.of(subNode);
                } else {
                    Skript.error("The only sections allowed under 'generator' are 'initiation', 'generation', and 'population'!");
                    return false;
                }
            }
            return true;
        } finally {
            ScopeUtil.removeSubNodes(topNode);
        }

    }

    @Override
    public String toString(Event event, boolean b) {
        return "generator \"" + generatorFunctionality.id + "\"";
    }
}