package com.pie.tlatoani.Util;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import ch.njol.skript.lang.Conditional;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.TriggerSection;
import ch.njol.util.Kleenean;

public class EffScope extends Effect{
	private Conditional section = null;
	private CustomScope scope = null;

	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean paramKleenean, ParseResult paramParseResult) {
		return true;
	}

	@Override
	public String toString(@Nullable Event paramEvent, boolean paramBoolean) {
		return "$ scope";
	}

	@Override
	protected void execute(Event arg0) {
		scope.walk(arg0);
	}
	
	@Override
	protected TriggerItem walk(final Event e) {
		run(e);
		debug(e, true);
		return section.getNext();
	}
	
	@Override
	public TriggerItem setNext(final @Nullable TriggerItem next) {
		if (next instanceof Conditional) {
			try {
				section = (Conditional) next;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else
			try {
				throw new Exception("You must put '$ scope' before a custom scope!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		return this;
	}
	
	@Override
	public TriggerItem setParent(final @Nullable TriggerSection parent) {
		try {
			if (CustomScope.condition.get(section) instanceof CustomScope) scope = (CustomScope) CustomScope.condition.get(section);
			else throw new Exception("You must put '$ scope' before a custom scope!");
			scope.setScopeNext(section);
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.parent = parent;
		return this;
	}
	
	@Override
	public TriggerItem getNext() {
		return section.getNext();
	}

}