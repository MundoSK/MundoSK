package com.pie.tlatoani.Miscellaneous;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.function.Functions;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

@SuppressWarnings("rawtypes")
public class ExprReturnTypeOfFunction extends SimpleExpression<ClassInfo>{
	private Expression<String> func;

	@Override
	public Class<? extends ClassInfo> getReturnType() {
		return ClassInfo.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult arg3) {
		func = (Expression<String>) expr[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event event, boolean arg1) {
		// TODO Auto-generated method stub
		return "border length of world";
	}

	@Override
	@Nullable
	protected ClassInfo<?>[] get(Event event) {
		return new ClassInfo<?>[]{Functions.getFunction(func.getSingle(event)).getReturnType()};
	}

}