package com.pie.tlatoani.Util;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EvtCustomEvent extends SkriptEvent {
	private List<String> ids = new ArrayList<String>();

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "custom event";
	}

	@Override
	public boolean check(Event arg0) {
		if (arg0 instanceof UtilCustomEvent) {
			if (ids.contains(((UtilCustomEvent) arg0).getID().toLowerCase())) {
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
