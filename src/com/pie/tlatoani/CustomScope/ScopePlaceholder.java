package com.pie.tlatoani.CustomScope;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.log.SkriptLogger;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Util.UtilReflection;
import org.bukkit.event.Event;

import java.util.ArrayList;

/**
 * Created by Tlatoani on 3/31/17.
 * Thanks to Tuke_Nuke for telling me about his discovery of this method
 */
public class ScopePlaceholder extends Condition {
    public static UtilReflection.FieldAccessor<ArrayList> NODE_NODES = UtilReflection.getField(SectionNode.class, "nodes", ArrayList.class);

    @Override
    public boolean check(Event event) {
        throw new UnsupportedOperationException("The scope placeholder should never be check()ed");
    }

    @Override
    public String toString(Event event, boolean b) {
        return null;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        Node oldNode = SkriptLogger.getNode();
        if (!(oldNode instanceof SectionNode)) {
            Skript.error("Scopes should not be used as free standing conditions");
            return false;
        }
        SectionNode newNode = new SectionNode(oldNode.getKey(), "", oldNode.getParent(), oldNode.getLine());
        for (Node subNode : (SectionNode) oldNode) {
            newNode.add(subNode);
            ((SectionNode) oldNode).remove(subNode);
        }
        SkriptLogger.setNode(oldNode); //Change
        SimpleScope simpleScope = null; //Change
        ScriptLoader.currentSections.add(simpleScope);
        simpleScope.setSectionNode(newNode);
        boolean result = simpleScope.init(expressions, i /* change the i to be correct*/, kleenean, parseResult);
        if (result) {

        }
        return true;
    }

    public static class SpyNode extends SectionNode {
        public SpyNode(String key, String comment, SectionNode parent, int lineNum) {
            super(key, comment, parent, lineNum);
        }
    }
}
