package com.pie.tlatoani.WorldCreator;

import com.pie.tlatoani.Generator.ChunkGeneratorWithID;
import org.bukkit.WorldCreator;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.generator.ChunkGenerator;

public class ExprGenOfCreator extends SimpleExpression<String>{
	private Expression<WorldCreator> creator;

	@Override
	public Class<? extends String> getReturnType() {
		// TODO Auto-generated method stub
		return String.class;
	}

	@Override
	public boolean isSingle() {
		// TODO Auto-generated method stub
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult arg3) {
		// TODO Auto-generated method stub
		creator = (Expression<WorldCreator>) expr[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event event, boolean arg1) {
		// TODO Auto-generated method stub
		return "generator of " + creator;
	}

	@Override
	@Nullable
	protected String[] get(Event event) {
		ChunkGenerator generator = creator.getSingle(event).generator();
		String result = null;
		if (generator instanceof ChunkGeneratorWithID) {
			result = ((ChunkGeneratorWithID) generator).id;
		}
		return new String[]{result};
	}
	
	public void change(Event event, Object[] delta, Changer.ChangeMode mode){
		if (mode == ChangeMode.SET){
			creator.getSingle(event).generator(ChunkGeneratorWithID.getGenerator((String)delta[0]));
		}
	}
	
	@SuppressWarnings("unchecked")
	public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
		if (mode == ChangeMode.SET) {
			return CollectionUtils.array(String.class);
		}
		return null;
	}

}