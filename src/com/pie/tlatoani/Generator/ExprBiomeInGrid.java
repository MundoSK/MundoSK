package com.pie.tlatoani.Generator;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.pie.tlatoani.Mundo;
import org.bukkit.block.Biome;
import org.bukkit.event.Event;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.ChunkGenerator.BiomeGrid;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

/**
 * Created by Tlatoani on 7/5/16.
 */
public class ExprBiomeInGrid extends SimpleExpression<Biome> {
    private Expression<Number> xExpression;
    private Expression<Number> zExpression;
    private Expression<BiomeGrid> biomeGridExpression;

    @Override
    protected Biome[] get(Event event) {
        int x = xExpression.getSingle(event).intValue();
        int z = zExpression.getSingle(event).intValue();
        BiomeGrid grid = biomeGridExpression.getSingle(event);
        return new Biome[]{grid.getBiome(x, z)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Biome> getReturnType() {
        return Biome.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "biome at " + xExpression + ", " + zExpression + " in grid " + biomeGridExpression;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        xExpression = (Expression<Number>) expressions[0];
        zExpression = (Expression<Number>) expressions[1];
        biomeGridExpression = (Expression<BiomeGrid>) expressions[2];
        return true;
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode){
        int x = xExpression.getSingle(event).intValue();
        int z = zExpression.getSingle(event).intValue();
        BiomeGrid grid = biomeGridExpression.getSingle(event);
        grid.setBiome(x, z, (Biome) delta[0]);
    }

    @SuppressWarnings("unchecked")
    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) return CollectionUtils.array(Biome.class);
        return null;
    }
}
