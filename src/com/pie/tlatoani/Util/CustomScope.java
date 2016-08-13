package com.pie.tlatoani.Util;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.SkriptEventHandler;
import ch.njol.skript.lang.*;
import com.pie.tlatoani.Mundo;
import org.bukkit.event.Event;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public abstract class CustomScope extends Condition {
	public static Field firstitem;
	public static Field condition;
	public static Field whilecondition;
	public static Method walkmethod;
	public static Method runmethod;

	protected TriggerSection scopeParent;
	protected Conditional scope = null;
	protected TriggerItem first;
	protected Expression<?>[] exprs;
	protected Integer arg1;
	protected Kleenean arg2;
	protected ParseResult arg3;
	
	static {
		try {
			firstitem = TriggerSection.class.getDeclaredField("first");
			firstitem.setAccessible(true);
			condition = Conditional.class.getDeclaredField("cond");
			condition.setAccessible(true);
			whilecondition = While.class.getDeclaredField("c");
			whilecondition.setAccessible(true);
			walkmethod = TriggerItem.class.getDeclaredMethod("walk", Event.class);
			walkmethod.setAccessible(true);
			runmethod = TriggerItem.class.getDeclaredMethod("run", Event.class);
			runmethod.setAccessible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setScopes() {
		try {
			Field triggers = SkriptEventHandler.class.getDeclaredField("triggers");
			triggers.setAccessible(true);
			Map<Class<? extends Event>, List<Trigger>> triggerMap = (Map<Class<? extends Event>, List<Trigger>>) triggers.get(null);
			triggerMap.forEach(new BiConsumer<Class<? extends Event>, List<Trigger>>() {
				@Override
				public void accept(Class<? extends Event> aClass, List<Trigger> triggers) {
					triggers.forEach(new Consumer<Trigger>() {
						@Override
						public void accept(Trigger trigger) {

						}
					});
				}
			});
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public void setScope(Conditional scope) {
		if (scope != null) {
			this.scope = scope;
			try {
				this.first = (TriggerItem) firstitem.get(scope);
			} catch (Exception e) {
				e.printStackTrace();
			}
			afterSetScope();
		}
	}

	private void getScope() {
		try {
			TriggerItem going = (TriggerItem) firstitem.get(scopeParent);
			Conditional scope = null;
			while (scope == null) {
				going = going.getNext();
				if (going instanceof Conditional) {
					Condition condition1 = (Condition) condition.get(going);
					if (this == condition1) {
						scope = (Conditional) going;
					}
				}
			}
			Mundo.debug(this, "FOUND THE CONDITIONAL:: " + scope);
			//setScope(scope);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	//Overriden methods

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return getString();
	}

	@Override
	public boolean init(Expression<?>[] exprs, int arg1, Kleenean arg2, ParseResult arg3) {
		this.exprs = exprs;
		this.arg1 = arg1;
		this.arg2 = arg2;
		this.arg3 = arg3;
		scopeParent = ScriptLoader.currentSections.get(ScriptLoader.currentSections.size() - 1);
		afterInit();
		return true;
	}

	@Override
	public boolean check(Event e) {
		getScope();
		go(e);
		return false;
	}

	//Methods to override

	public abstract String getString();

	public abstract void go(Event e);

	public void afterInit() {}
	
	public void afterSetScope() {}



	//Code that will be removed as custom scopes no longer need to function as standalone conditions
	
	@Override
	public TriggerItem walk(final Event e) {
		run(e);
		debug(e, true);
		return scope.getNext();
	}
	
	@Override
	public TriggerItem setNext(final @Nullable TriggerItem next) {
		this.scope = (Conditional) next;
		return this;
	}
	
	@Override
	public TriggerItem setParent(final @Nullable TriggerSection parent) {
		super.parent = parent;
		try {
			if (this.scope instanceof TriggerSection) this.first = (TriggerItem) firstitem.get(scope);
			else this.first = this.scope;
		} catch (Exception e) {
			e.printStackTrace();
		}
		afterSetScope();
		return this;
	}

	@Override
	public TriggerItem getNext() {
		return scope.getNext();
	}
	


}
