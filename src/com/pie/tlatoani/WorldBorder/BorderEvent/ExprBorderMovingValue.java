package com.pie.tlatoani.WorldBorder.BorderEvent;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Core.Static.Config;
import org.bukkit.World;
import org.bukkit.event.Event;

import java.util.function.Function;

public class ExprBorderMovingValue extends SimpleExpression<Number>{
	private Expression<World> worldExpression;
	private Type type;

	public enum Type {
	    ORIGINAL_DIAMETER("original diameter", WorldBorderImpl::getOriginalDiameter),
        EVENTUAL_DIAMETER("eventual diameter", WorldBorderImpl::getEventualDiameter),
        REMAINING_DISTANCE("remaining distance", WorldBorderImpl::getRemainingDistance);

        public final String syntax;
        public final Function<WorldBorderImpl, Double> function;

        Type(String syntax, Function<WorldBorderImpl, Double> function) {
            this.syntax = syntax;
            this.function = function;
        }

        public Double getValue(WorldBorderImpl border) {
            return function.apply(border);
        }
    }

	@Override
	public Class<? extends Number> getReturnType() {
		return Number.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult parseResult) {
		worldExpression = (Expression<World>) expr[0];
		if ((parseResult.mark & 0b1000) == 0b1000) {
			if (Config.DISABLE_SIZE_SYNTAX.getCurrentValue()) {
				return false;
			}
			Skript.warning("The 'size' alias for border diameter will be removed in a future version. Please use 'diameter' instead.");
		}
		type = Type.values()[parseResult.mark & 0b0111];
		return true;
	}

	@Override
	public String toString(Event event, boolean arg1) {
		return type.syntax + " of " + worldExpression;
	}

	@Override
	protected Number[] get(Event event) {
		World world = worldExpression.getSingle(event);
		if (world.getWorldBorder() instanceof WorldBorderImpl) {
		    WorldBorderImpl border = (WorldBorderImpl) world.getWorldBorder();
		    return new Number[]{type.getValue(border)};
        }
        return new Number[0];
	}

}