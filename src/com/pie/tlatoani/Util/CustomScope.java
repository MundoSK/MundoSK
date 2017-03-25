package com.pie.tlatoani.Util;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.SkriptEventHandler;
import ch.njol.skript.command.Commands;
import ch.njol.skript.command.ScriptCommand;
import ch.njol.skript.lang.*;
import ch.njol.skript.lang.function.Functions;
import ch.njol.skript.lang.function.ScriptFunction;
import com.pie.tlatoani.Mundo;
import org.bukkit.event.Event;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

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
	public static Method getTriggersMethod;

	public static UtilReflection.FieldAccessor<TriggerItem> triggerSectionFirst;
    public static UtilReflection.FieldAccessor<TriggerItem> triggerSectionLast;
	public static UtilReflection.FieldAccessor<Condition> conditionalCond;
	public static UtilReflection.FieldAccessor<Trigger> scriptFunctionTrigger;

	private static boolean getScopesWasRun = true;

	protected boolean canStandFree = false;
	protected ScriptFunction function = null;
	protected TriggerSection scopeParent;
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
			getTriggersMethod = SkriptEventHandler.class.getDeclaredMethod("getTriggers", Class.class);
			getTriggersMethod.setAccessible(true);
			walkmethod = TriggerItem.class.getDeclaredMethod("walk", Event.class);
			walkmethod.setAccessible(true);
			runmethod = TriggerItem.class.getDeclaredMethod("run", Event.class);
			runmethod.setAccessible(true);

			triggerSectionFirst = UtilReflection.getField(TriggerSection.class, "first", TriggerItem.class);
            triggerSectionLast = UtilReflection.getField(TriggerSection.class, "last", TriggerItem.class);
			conditionalCond = UtilReflection.getField(Conditional.class, "cond", Condition.class);
			scriptFunctionTrigger = UtilReflection.getField(ScriptFunction.class, "trigger", Trigger.class);
		} catch (Exception e) {
			Mundo.reportException(CustomScope.class, e);
		}
	}

	public static void registerImmediateScopes(Trigger trigger) {
        TriggerItem going = triggerSectionFirst.get(trigger);
        while (going != null) {
            if (going instanceof Conditional) {
                Condition condition1 = conditionalCond.get(going);
                if (condition1 instanceof CustomScope) {
                    ((CustomScope) condition1).setScope((Conditional) going);
                }
            }
            going = going instanceof Loop ? ((Loop) going).getActualNext() : going instanceof While ? ((While) going).getActualNext() : going.getNext();

        }
    }

	public static void getScopes() {
		if (!getScopesWasRun) {
			try {
				Map<Class<? extends Event>, List<Trigger>> triggerMap = (Map<Class<? extends Event>, List<Trigger>>) triggers.get(null);
				Mundo.debug(CustomScope.class, "TRIGGERMAP:: " + triggerMap);
				for (List<Trigger> triggers : triggerMap.values()) {
					triggers.forEach(CustomScope::registerImmediateScopes);
				}
				Map<String, ScriptCommand> commandMap = (Map<String, ScriptCommand>) commands.get(null);
				for (ScriptCommand scriptCommand : commandMap.values()) {
                    Trigger trigger = (Trigger) commandTrigger.get(scriptCommand);
                    registerImmediateScopes(trigger);

				}
			} catch (IllegalAccessException e) {
				Mundo.reportException(CustomScope.class, e);
			}
			getScopesWasRun = true;
		}
	}

	public static void querySetScope() {
		if (getScopesWasRun) {
			getScopesWasRun = false;
			Mundo.scheduler.runTask(Mundo.instance, new Runnable() {
				@Override
				public void run() {
					CustomScope.getScopes();
				}
			});
		}
	}

	public static boolean getScopesWasRun() {
		return getScopesWasRun;
	}

	public void setScope(Conditional scope) {
		if (scope != null) {
			this.scope = scope;
            this.first = triggerSectionFirst.get(scope);
            this.last = triggerSectionLast.get(scope);
			Mundo.debug(this, "GUTEN ROUNDEN:: " + first);
			if (scopeParent == null) {
				scopeParent = scope.getParent();
			}
			setScope();
		}
	}

	private void getScope() {
		setScope(getScope(scopeParent, this));
	}

	public static Conditional getScope(TriggerSection parent, CustomScope scope) {
	    TriggerItem going = triggerSectionFirst.get(parent);
	    while (true) {
	        Mundo.debug(CustomScope.class, "GOING :: " + going);
	        if (going instanceof Conditional) {
	            Condition condition = conditionalCond.get(going);
	            if (scope == condition) {
                    Mundo.debug(CustomScope.class, "FOUND THE CONDITIONAL :: " + going);
	                return (Conditional) going;
                }
            }
            going = going instanceof Loop ? ((Loop) going).getActualNext() : going instanceof While ? ((While) going).getActualNext() : going.getNext();
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
        } else if (Functions.currentFunction != null) {
		    function = Functions.currentFunction;
        } else {
            querySetScope();
        }
		return init();
	}

	@Override
	public boolean check(Event e) {
		if (scope == null) {
		    if (function != null) {
                scopeParent = scriptFunctionTrigger.get(function);
            }
			if (scopeParent != null) {
                getScope();
            } else {
                getScopes();
            }
		}
		Mundo.debug(this, "Go");
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

	protected boolean go(Event e) {
		return false;
	}

	protected boolean init() {
		return true;
	}
	
	protected void setScope() {}

	//Public Utility Methods

	public TriggerItem getFirst() {
		return first;
	}

}
