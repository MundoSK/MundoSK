package com.pie.tlatoani.WorldCreator;

import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprCreatorOfWith extends SimpleExpression<WorldCreator>{
	private Expression<WorldCreator> creator;
	private Expression<String> name;
	private Expression<String> env;
	private Expression<String> seed;
	private Expression<String> type;
	private Expression<String> gen;
	private Expression<String> genset;
	private Expression<Boolean> struct;

	@Override
	public Class<? extends WorldCreator> getReturnType() {
		// TODO Auto-generated method stub
		return WorldCreator.class;
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
		name = (Expression<String>) expr[1];
		env = (Expression<String>) expr[2];
		seed = (Expression<String>) expr[3];
		type = (Expression<String>) expr[4];
		gen = (Expression<String>) expr[5];
		genset = (Expression<String>) expr[6];
		struct = (Expression<Boolean>) expr[7];
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return "border length of world";
	}

	@Override
	@Nullable
	protected WorldCreator[] get(Event arg0) {
		WorldCreator w = creator.getSingle(arg0);
		String z = w.name();
		if (name != null) {
			z = name.getSingle(arg0);
		}
		WorldCreator x = new WorldCreator(z);
		x.environment(w.environment());
		x.type(w.type());
		x.generator(w.generator());
		x.generatorSettings(w.generatorSettings());
		x.generateStructures(w.generateStructures());
		if (seed != null) {
			if (seed.getSingle(arg0).length() > 0) {
			x.seed(Long.parseLong(seed.getSingle(arg0)));
			}
		} else {
			x.seed(w.seed());
		}
		if (gen != null) {
			x.generator(gen.getSingle(arg0));
		}
		if (genset != null) {
			x.generatorSettings(genset.getSingle(arg0));
		}
		if (struct != null) {
			x.generateStructures(struct.getSingle(arg0));
		}
		if (env != null) {
		if (env.getSingle(arg0).equalsIgnoreCase("NORMAL")) {
			x.environment(World.Environment.NORMAL);
		}
		if (env.getSingle(arg0).equalsIgnoreCase("NETHER")) {
			x.environment(World.Environment.NETHER);
		}
		if (env.getSingle(arg0).equalsIgnoreCase("END")) {
			x.environment(World.Environment.THE_END);
		}}
		if (type != null) {
		if (type.getSingle(arg0).equalsIgnoreCase("normal")) {
			x.type(WorldType.NORMAL);
		}
		if (type.getSingle(arg0).equalsIgnoreCase("flat")) {
			x.type(WorldType.FLAT);
		}
		if (type.getSingle(arg0).equalsIgnoreCase("large biomes")) {
			x.type(WorldType.LARGE_BIOMES);
		}
		if (type.getSingle(arg0).equalsIgnoreCase("amplified")) {
			x.type(WorldType.AMPLIFIED);
		}
		if (type.getSingle(arg0).equalsIgnoreCase("version 1.1")) {
			x.type(WorldType.VERSION_1_1);
		}
		if (type.getSingle(arg0).equalsIgnoreCase("customized")) {
			x.type(WorldType.CUSTOMIZED);
		}
		}
		return new WorldCreator[]{x};
	}

}