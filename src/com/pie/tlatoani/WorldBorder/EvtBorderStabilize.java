package com.pie.tlatoani.WorldBorder;

import org.bukkit.World;
import org.bukkit.event.Event;
import javax.annotation.Nullable;

import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;

public class EvtBorderStabilize extends SkriptEvent {
	private Literal<World> border;

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "border stabilize";
	}

	@Override
	public boolean check(Event arg0) {
		if (arg0 instanceof BorderStabilizeEvent) {
			if (border != null) {
				if (((BorderStabilizeEvent) arg0).getWorld() == border.getSingle()) return true;
				else return false;
			} else 
			return true;
		} else return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Literal<?>[] lit, int arg1, ParseResult arg2) {
		border = (Literal<World>) lit[0];
		return true;
	}

}
