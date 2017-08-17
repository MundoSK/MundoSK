package com.pie.tlatoani.WorldBorder.BorderEvent;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.World;
import org.bukkit.event.Event;

import javax.annotation.Nullable;
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
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult arg3) {
		worldExpression = (Expression<World>) expr[0];
		type = Type.values()[arg3.mark];
		return true;
	}

	@Override
	public String toString(@Nullable Event event, boolean arg1) {
		return type.syntax + " of " + worldExpression;
	}

	@Override
	@Nullable
	protected Number[] get(Event event) {
		World world = worldExpression.getSingle(event);
		if (world.getWorldBorder() instanceof WorldBorderImpl) {
		    WorldBorderImpl border = (WorldBorderImpl) world.getWorldBorder();
		    return new Number[]{type.getValue(border)};
        }
        return new Number[0];
	}

}