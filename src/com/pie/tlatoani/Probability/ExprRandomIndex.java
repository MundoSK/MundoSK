package com.pie.tlatoani.Probability;

import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAPIException;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Variable;
import ch.njol.skript.lang.util.ContainerExpression;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.Container;
import ch.njol.skript.util.Container.ContainerType;
import ch.njol.util.Kleenean;
import ch.njol.util.Pair;

public class ExprRandomIndex extends SimpleExpression<String> {
	private Variable<?> numbers;

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		if (exprs[0] instanceof Variable && ((Variable) exprs[0]).isList()) {
			numbers = (Variable) exprs[0];
			return true;
		}
		Skript.error("'random from %number% probs' must be used with a list variable!");;
		return false;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "random from %numbers% prob[abilitie]s";
	}

	@Override
	@Nullable
	protected String[] get(Event e) {
		Iterator<Pair<String, Object>> iter = numbers.variablesIterator(e);
		List<Number> probs = new ArrayList<Number>();
		List<String> indeces = new ArrayList<String>();
		Number sum = 0;
		while (iter.hasNext()) {
			Pair<String, Object> pair = iter.next();
			if (pair.getValue() instanceof Number) {
				indeces.add(pair.getKey());
				sum = sum.doubleValue() + ((Number) pair.getValue()).doubleValue();
				probs.add(sum);
			}
		}
		Number random = Math.random() * sum.doubleValue();
		Boolean searching = true;
		int j = 0;
		while (searching) {
			if (random.doubleValue() <= probs.get(j).doubleValue()) searching = false;
			else j++;
		}
		return new String[]{indeces.get(j)};
	}

}
