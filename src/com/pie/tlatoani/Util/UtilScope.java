package com.pie.tlatoani.Util;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.TriggerSection;
import ch.njol.skript.log.SkriptLogger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tlatoani on 5/4/17.
 */
public class UtilScope {

    public static TriggerItem loadSectionNode(SectionNode sectionNode, TriggerSection parent) {
        if (parent != null) {
            ScriptLoader.currentSections.add(parent);
        }
        List<TriggerItem> triggerItems = ScriptLoader.loadItems(sectionNode);
        if (parent != null && !triggerItems.isEmpty()) {
            triggerItems.get(triggerItems.size() - 1).setNext(parent.getNext());
            for (TriggerItem triggerItem : triggerItems) {
                triggerItem.setParent(parent);
            }
            ScriptLoader.currentSections.remove(parent);
        }
        return triggerItems.isEmpty() ? null : triggerItems.get(0);
    }

    public static Node[] getSection() {
        SectionNode sectionNode = (SectionNode) SkriptLogger.getNode();
        Node[] nodes = new Node[sectionNode.size()];
        int i = 0;
        for (Node node : sectionNode) {
            nodes[i] = node;
            i++;
        }
        for (Node node : nodes) { //Prevents ConcurrentModificationException
            sectionNode.remove(node);
        }
        return nodes;
    }
}
