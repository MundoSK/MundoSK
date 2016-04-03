package com.pie.tlatoani.Util;

import org.bukkit.event.Event;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.annotation.Nullable;

import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Conditional;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.TriggerSection;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public abstract class CustomScope extends Condition {
	public static Field firstitem;
	public static Field condition;
	public static Method walkmethod;
	protected TriggerItem section;
	protected TriggerItem first;
	protected Boolean scope;
	protected Expression<?>[] exprs;
	protected Integer arg1;
	protected Kleenean arg2;
	protected ParseResult arg3;
	protected Integer indent;
	
	static {
		try {
			firstitem = TriggerSection.class.getDeclaredField("first");
			firstitem.setAccessible(true);
			condition = Conditional.class.getDeclaredField("cond");
			condition.setAccessible(true);
			walkmethod = TriggerItem.class.getDeclaredMethod("walk", Event.class);
			walkmethod.setAccessible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

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
	
	public abstract void go(Event e);
	
	public void afterSetNext() {}

	@Override
	public boolean check(Event e) {
		go(e);
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
	
	@Override
	public TriggerItem setParent(final @Nullable TriggerSection parent) {
		super.parent = parent;
		try {
			if (this.section instanceof TriggerSection) this.first = (TriggerItem) firstitem.get(section);
			else this.first = this.section;
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.indent = getIndentation().length();
		afterSetNext();
		return this;
	}
	
	public void setScopeNext(Conditional next) {
		this.section = next;
		try {
			this.first = (TriggerItem) firstitem.get(next);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.indent = next.getIndentation().length();
		this.scope = true;
		afterSetNext();
	}
	
	@Override
	public TriggerItem getNext() {
		return section.getNext();
	}

}
