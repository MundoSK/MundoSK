package com.pie.tlatoani.Probability;

import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprRandomNumberIndex extends SimpleExpression<Integer> {
	private Expression<Number> numbers;

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends Integer> getReturnType() {
		return Integer.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		numbers = (Expression<Number>) exprs[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "random number from %numbers% prob[abilitie]s";
	}

	@Override
	@Nullable
	protected Integer[] get(Event e) {
		List<Number> probs = Arrays.asList(numbers.getArray(e));
		List<Number> nums = new ArrayList<Number>();
		Number sum = 0;
		for (int i = 0; i < probs.size(); i++) {
			sum = sum.doubleValue() + (probs.get(i)).doubleValue();
			nums.add(sum);
		}
		Number random = Math.random() * sum.doubleValue();
		Boolean searching = true;
		int j = 0;
		while (searching) {
			if (random.doubleValue() <= nums.get(j).doubleValue()) searching = false;
			else j++;
		}
		return new Integer[]{j + 1};
	}

}