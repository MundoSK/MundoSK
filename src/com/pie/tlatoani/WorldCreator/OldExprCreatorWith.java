package com.pie.tlatoani.WorldCreator;

import com.pie.tlatoani.Generator.ChunkGeneratorWithID;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class OldExprCreatorWith extends SimpleExpression<WorldCreator>{
	private Expression<WorldCreator> creator;
	private Expression<String> name;
	private Expression<Environment> env;
	private Expression<String> seed;
	private Expression<WorldType> type;
	private Expression<String> gen;
	private Expression<String> genset;
	private Expression<Boolean> struct;

	@Override
	public Class<? extends WorldCreator> getReturnType() {
		return WorldCreator.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult arg3) {
		creator = (Expression<WorldCreator>) expr[0];
		name = (Expression<String>) expr[1];
		env = (Expression<Environment>) expr[2];
		seed = (Expression<String>) expr[3];
		type = (Expression<WorldType>) expr[4];
		gen = (Expression<String>) expr[5];
		genset = (Expression<String>) expr[6];
		struct = (Expression<Boolean>) expr[7];
		return true;
	}

	@Override
	public String toString(@Nullable Event event, boolean arg1) {
		// TODO Auto-generated method stub
		return "border length of world";
	}

	@Override
	@Nullable
	protected WorldCreator[] get(Event event) {
		WorldCreator oldCreator = creator.getSingle(event);
		String worldName = oldCreator.name();
		if (name != null) {
			worldName = name.getSingle(event);
		}
		WorldCreator newCreator = new WorldCreator(worldName);
		newCreator.environment(oldCreator.environment());
		newCreator.type(oldCreator.type());
		newCreator.generator(oldCreator.generator());
		newCreator.generatorSettings(oldCreator.generatorSettings());
		newCreator.generateStructures(oldCreator.generateStructures());
		if (seed != null) {
			if (seed.getSingle(event).length() > 0) newCreator.seed(Long.parseLong(seed.getSingle(event)));
		} else newCreator.seed(oldCreator.seed());
		if (gen != null)  {
			newCreator.generator(ChunkGeneratorWithID.getGenerator(gen.getSingle(event)));
		}
		if (genset != null) newCreator.generatorSettings(genset.getSingle(event));
		if (struct != null) newCreator.generateStructures(struct.getSingle(event));
		if (env != null) newCreator.environment(env.getSingle(event));
		if (type != null) newCreator.type(type.getSingle(event));
		return new WorldCreator[]{newCreator};
	}

}