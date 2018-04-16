package com.pie.tlatoani.Util.Skript;

import ch.njol.skript.variables.Variables;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Tlatoani on 7/4/16.
 */
public class BaseEvent extends Event {
    public static final HandlerList HANDLER_LIST = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    public void setLocalVariable(String varname, Object value) {
        Variables.setVariable(varname, value, this, true);
    }

    public Object getLocalVariable(String varname) {
        return Variables.getVariable(varname, this, true);
    }

}
