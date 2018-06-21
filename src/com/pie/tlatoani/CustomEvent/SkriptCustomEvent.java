package com.pie.tlatoani.CustomEvent;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.registrations.Classes;
import com.google.common.collect.ImmutableList;
import com.pie.tlatoani.Core.Static.Logging;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public class SkriptCustomEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	public final ImmutableList<String> ids;
	private Object[] args;
	private Map<ClassInfo, Object> details = new HashMap<ClassInfo, Object>();
	private boolean cancelled;

	static final WeakHashMap<Event, SkriptCustomEvent> lastCustomEvents = new WeakHashMap<>();
	
	public SkriptCustomEvent(String[] ids, Object[] details, Object[] args) {
		this(ids, details, args, true);
	}

	public SkriptCustomEvent(String[] ids, Object[] details, Object[] args, boolean sync) {
	    super(!sync);
	    if (ids.length == 0) {
	        throw new IllegalArgumentException("There must be at least one ID!");
        }
        this.ids = ImmutableList.copyOf(ids);
        for (int i = 0; i < details.length; i++) {
            Logging.debug(this, "DETAIL " + details[i]);
            Logging.debug(this, ".GETCLASS " + details[i].getClass());
            Logging.debug(this, "GETSUPERCLASSINFO " + Classes.getSuperClassInfo(details[i].getClass()));
            this.details.put(Classes.getSuperClassInfo(details[i].getClass()), details[i]);
        }
        this.args = args;
    }
	
	public Object getDetail(ClassInfo<?> type) {
		return details.containsKey(type) ? details.get(type) : null ;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}

	public boolean matchesID(String id) {
	    return ids.contains(id);
    }

    public String getPrimaryID() {
	    return ids.get(0);
    }
	
	public Object[] getArgs() {
		return args;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean b) {
	    cancelled = b;
	}
}
