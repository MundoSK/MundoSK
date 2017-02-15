package com.pie.tlatoani.Util;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.SkriptEventHandler;
import ch.njol.skript.command.Commands;
import ch.njol.skript.command.ScriptCommand;
import ch.njol.skript.lang.*;
import com.pie.tlatoani.Generator.SkriptGeneratorManager;
import com.pie.tlatoani.Generator.SkriptGeneratorEvent;
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
	public static Field lastitem;
	public static Field condition;
	public static Field whilecondition;
	public static Field triggers;
	public static Field commands;
	public static Method walkmethod;
	public static Method runmethod;
	public static Field commandTrigger;
	private static boolean getScopesWasRun = true;

	protected boolean canStandFree = false;
	protected TriggerSection scopeParent;
	protected TriggerItem scopeNext;
	protected Conditional scope = null;
	protected TriggerItem first;
	protected TriggerItem last;

	protected Expression<?>[] exprs;
	protected Integer arg1;
	protected Kleenean arg2;
	protected ParseResult arg3;
	
	static {
		try {
			firstitem = TriggerSection.class.getDeclaredField("first");
			firstitem.setAccessible(true);
			lastitem = TriggerSection.class.getDeclaredField("last");
			lastitem.setAccessible(true);
			condition = Conditional.class.getDeclaredField("cond");
			condition.setAccessible(true);
			whilecondition = While.class.getDeclaredField("c");
			whilecondition.setAccessible(true);
			triggers = SkriptEventHandler.class.getDeclaredField("triggers");
			triggers.setAccessible(true);
			commandTrigger = ScriptCommand.class.getDeclaredField("trigger");
			commandTrigger.setAccessible(true);
			commands = Commands.class.getDeclaredField("commands");
			commands.setAccessible(true);
			walkmethod = TriggerItem.class.getDeclaredMethod("walk", Event.class);
			walkmethod.setAccessible(true);
			runmethod = TriggerItem.class.getDeclaredMethod("run", Event.class);
			runmethod.setAccessible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Map<Class<? extends Event>, List<Trigger>> triggerMap = null;

	public static Map<Class<? extends Event>, List<Trigger>> getTriggerMap() {
	    if (triggerMap == null) {
	        try {
	            triggerMap = (Map<Class<? extends Event>, List<Trigger>>) triggers.get(null);
                Mundo.debug(CustomScope.class, "TRIGGERMAP:: " + triggerMap);
            } catch (IllegalAccessException e) {
                Mundo.reportException(CustomScope.class, e);
            }
        }
        return triggerMap;
    }

	public static void getScopes() {
		if (!getScopesWasRun) {
			try {
				for (List<Trigger> triggers : getTriggerMap().values()) {
					for (Trigger trigger : triggers) {
						try {
							TriggerItem going = (TriggerItem) CustomScope.firstitem.get(trigger);
							while (going != null) {
								if (going instanceof Conditional) {
									Condition condition1 = (Condition) CustomScope.condition.get(going);
									if (condition1 instanceof CustomScope) {
										((CustomScope) condition1).setScope((Conditional) going);
									}
								}
								going = going instanceof Loop ? ((Loop) going).getActualNext() : going instanceof While ? ((While) going).getActualNext() : going.getNext();

							}
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
					}
				}

				List<Trigger> triggerList = triggerMap.get(SkriptGeneratorEvent.class);
				if (triggerList != null) {
					SkriptGeneratorManager.registerTriggers(triggerList);
				}

				Map<String, ScriptCommand> commandMap = (Map<String, ScriptCommand>) commands.get(null);
				for (ScriptCommand scriptCommand : commandMap.values()) {
					try {
						Trigger trigger = (Trigger) commandTrigger.get(scriptCommand);
						TriggerItem going = (TriggerItem) CustomScope.firstitem.get(trigger);
						while (going != null) {
							if (going instanceof Conditional) {
								Condition condition1 = (Condition) CustomScope.condition.get(going);
								if (condition1 instanceof CustomScope) {
									((CustomScope) condition1).setScope((Conditional) going);
								}
							}
							going = going instanceof Loop ? ((Loop) going).getActualNext() : going instanceof While ? ((While) going).getActualNext() : going.getNext();

						}
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			getScopesWasRun = true;
		}
	}

	public static void querySetScope() {
		if (getScopesWasRun) {
			getScopesWasRun = false;
			Mundo.sync(CustomScope::getScopes);
		}
	}

	public static boolean getScopesWasRun() {
		return getScopesWasRun;
	}

	public void setScope(Conditional scope) {
		if (scope != null) {
			this.scope = scope;
			try {
				this.first = (TriggerItem) firstitem.get(scope);
				this.last = (TriggerItem) lastitem.get(scope);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Mundo.debug(this, "GUTEN ROUNDEN:: " + first);
			if (scopeParent == null) {
				scopeParent = scope.getParent();
			}
			scopeNext = scope.getNext();
			setScope();
		}
	}

	private void getScope() {
		try {
			TriggerItem going = (TriggerItem) firstitem.get(scopeParent);
			Conditional scope = null;
			while (scope == null) {
				Mundo.debug(this, "GOING::: " + going);
				if (going instanceof Conditional) {
					Condition condition1 = (Condition) condition.get(going);
					if (this == condition1) {
						scope = (Conditional) going;
					}
				}
				going = going instanceof Loop ? ((Loop) going).getActualNext() : going instanceof While ? ((While) going).getActualNext() : going.getNext();
			}
			Mundo.debug(this, "FOUND THE CONDITIONAL:: " + scope);
			setScope(scope);
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
		int currentSectionsSize = ScriptLoader.currentSections.size();
		if (currentSectionsSize > 0) {
			scopeParent = ScriptLoader.currentSections.get(currentSectionsSize - 1);
		} else {
			querySetScope();
		}
		return init();
	}

	@Override
	public boolean check(Event e) {
		if (scope == null) {
			if (scopeParent != null)
				getScope();
			else
				getScopes();
		}
		return go(e);
	}

	@Override
	public TriggerItem setNext(TriggerItem next) {
		if (!canStandFree) {
			Skript.error("Custom scopes cannot be used as free standing conditions!");
		}
		return super.setNext(next);
	}

	//Methods to override

	public abstract String getString();

	public boolean go(Event e) {
		return false;
	}

	public boolean init() {
		return true;
	}
	
	public void setScope() {}

	//Public Utility Methods

	public TriggerItem getFirst() {
		return first;
	}

}
