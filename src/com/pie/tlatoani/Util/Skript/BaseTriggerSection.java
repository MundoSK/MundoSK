package com.pie.tlatoani.Util.Skript;

import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.TriggerSection;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 5/12/17.
 */
public class BaseTriggerSection extends TriggerSection {
    @Override
    protected TriggerItem walk(Event event) {
        return walk(event, true);
    }

    @Override
    public String toString(Event event, boolean b) {
        return "trigger section";
    }
}
