package com.pie.tlatoani.WorldBorder.BorderEvent;

import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import com.pie.tlatoani.Core.Static.Utilities;
import org.bukkit.World;
import org.bukkit.event.Event;

public class EvtBorderStabilize extends SkriptEvent {
	private Literal<World> worldLiteral;

	@Override
	public String toString(Event event, boolean arg1) {
		return "border stabilize" + (worldLiteral == null ? "" : " in " + worldLiteral);
	}

	@Override
	public boolean check(Event event) {
		if (event instanceof BorderStabilizeEvent) {
			if (worldLiteral != null) {
			    World world = ((BorderStabilizeEvent) event).getWorld();
			    return Utilities.check(worldLiteral, event, world::equals);
			} else {
                return true;
            }
		} else {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Literal<?>[] lit, int arg1, ParseResult arg2) {
		worldLiteral = (Literal<World>) lit[0];
        return true;
	}

}
