package com.pie.tlatoani.Probability;

import org.bukkit.event.Event;

import com.pie.tlatoani.Util.CustomScope;

import javax.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Conditional;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.TriggerSection;
import ch.njol.util.Kleenean;

public class CondProbabilityValue extends Condition {
	public Boolean ret = true;
	private Expression<Number> num;
	public TriggerSection section;
	private Boolean percent;
	public TriggerItem first;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		num = (Expression<Number>) exprs[0];
		percent = parseResult.mark == 1;
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "%number% prob";
	}

	@Override
	public boolean check(Event e) {
		return ret;
	}
	
	@Override
	public TriggerItem setParent(final @Nullable TriggerSection parent) {
		super.parent = parent;
		if (!(parent instanceof Conditional)) {
			Skript.error("'%number% prob' must be placed within a probability scope!");
		}
		return this;
	}
	
	public void setTriggerSection(TriggerSection section) {
		this.ret = false;
		this.section = section;
		try {
			this.first = (TriggerItem) CustomScope.firstitem.get(section);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public TriggerItem getTriggerItem() {
		if (ret) return this;
		else
			try {
				return (TriggerItem) CustomScope.firstitem.get(section);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
	}
	
	public Number get(Event e) {
		if (percent) return num.getSingle(e).doubleValue() * 0.01;
		else return num.getSingle(e);
	}

}
