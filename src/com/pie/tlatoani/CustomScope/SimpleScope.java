package com.pie.tlatoani.CustomScope;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.*;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;

import java.util.List;

/**
 * Created by Tlatoani on 3/31/17.
 */
public abstract class SimpleScope extends TriggerSection implements SyntaxElement {
    protected TriggerItem first = null; //

    public void setSectionNode(SectionNode node) {
        setTriggerItems(ScriptLoader.loadItems(node));
    }

    @Override
    protected void setTriggerItems(final List<TriggerItem> triggerItems) {
        if (!triggerItems.isEmpty()) {
            first = triggerItems.get(0);
        }
        super.setTriggerItems(triggerItems);
    }

    @Override
    public TriggerItem getNext() {
        return super.getNext().getNext();
    }

    @Override
    protected abstract TriggerItem walk(Event event);

    @Override
    public abstract String toString(Event event, boolean b);

    @Override
    public abstract boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult);
}
