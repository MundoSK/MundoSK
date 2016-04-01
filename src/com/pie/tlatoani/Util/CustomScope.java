package com.pie.tlatoani.Util;

import org.bukkit.event.Event;
import javax.annotation.Nullable;

import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Conditional;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public abstract class CustomScope extends Condition {
	private TriggerItem section;
	private TriggerItem first;
	private Boolean scope;
	protected Expression<?>[] exprs;
	protected Integer arg1;
	protected Kleenean arg2;
	protected ParseResult arg3;

	@Override
	public boolean init(Expression<?>[] exprs, int arg1, Kleenean arg2, ParseResult arg3) {
		this.exprs = exprs;
		this.arg1 = arg1;
		this.arg2 = arg2;
		this.arg3 = arg3;
		return true;
	}

	@Override
	public abstract String toString(@Nullable Event e, boolean debug);
	
	public abstract void go(Event e, TriggerItem next, Integer indent);

	@Override
	public boolean check(Event e) {
		if (scope) go(e, first, section.getIndentation().length());
		else go(e, section, section.getIndentation().length());
		return true;
	}
	
	@Override
	public TriggerItem walk(final Event e) {
		run(e);
		debug(e, true);
		return section.getNext();
	}
	
	@Override
	public TriggerItem setNext(final @Nullable TriggerItem next) {
		this.section = next;
		scope = false;
		return this;
	}
	
	public void setScopeNext(Conditional next, TriggerItem first) {
		this.section = next;
		this.first = first;
		scope = true;
	}
	
	@Override
	public TriggerItem getNext() {
		return section.getNext();
	}

}
