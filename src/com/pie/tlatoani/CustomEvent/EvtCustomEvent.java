package com.pie.tlatoani.CustomEvent;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

public class EvtCustomEvent extends SkriptEvent {
	private List<String> ids = new ArrayList<String>();

	@Override
	public String toString(@Nullable Event event, boolean arg1) {
		return "custom event";
	}

	@Override
	public boolean check(Event event) {
		if (event instanceof UtilCustomEvent) {
			if (ids.contains(((UtilCustomEvent) event).getID().toLowerCase())) {
				return true;
			} return false;
		} return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Literal<?>[] lit, int arg1, ParseResult arg2) {
		String[] strings = ((Literal<String>) lit[0]).getAll();
		for (int i = 0; i < strings.length; i ++) {
			ids.add(strings[i].toLowerCase());
		}
		return true;
	}

}
