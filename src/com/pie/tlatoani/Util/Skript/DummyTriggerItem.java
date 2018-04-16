package com.pie.tlatoani.Util.Skript;

import ch.njol.skript.lang.TriggerItem;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 6/27/17.
 */
public class DummyTriggerItem extends TriggerItem {

    @Override
    protected boolean run(Event event) {
        return true;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "MUNDOSK DUMMY TRIGGER ITEM";
    }
}
