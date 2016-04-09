package com.pie.tlatoani.Util;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;

public class EvtCustomEvent extends SkriptEvent {
	private Literal<String> id;

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "custom event";
	}

	@Override
	public boolean check(Event arg0) {
		if (arg0 instanceof UtilCustomEvent) {
			if (id != null) {
				if (((UtilCustomEvent) arg0).getID().equalsIgnoreCase(id.getSingle())) return true;
				else return false;
			} else 
			return true;
		} else return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Literal<?>[] lit, int arg1, ParseResult arg2) {
		id = (Literal<String>) lit[0];
		return true;
	}

}
