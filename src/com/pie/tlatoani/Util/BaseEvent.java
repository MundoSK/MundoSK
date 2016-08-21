package com.pie.tlatoani.Util;

import ch.njol.skript.variables.Variables;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Tlatoani on 7/4/16.
 */
public class BaseEvent extends Event {
    public static HandlerList handlerList = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public void setLocalVariable(String varname, Object value) {
        Variables.setVariable(varname, value, this, true);
    }

    public Object getLocalVariable(String varname) {
        return Variables.getVariable(varname, this, true);
    }

}
