package com.pie.tlatoani.Generator;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Core.Static.MathUtil;
import org.bukkit.event.Event;
import org.bukkit.generator.ChunkGenerator.ChunkData;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

/**
 * Created by Tlatoani on 7/3/16.
 */
public class EffSetRegionInChunkData extends Effect {
    private Expression<Number> x1Expression;
    private Expression<Number> y1Expression;
    private Expression<Number> z1Expression;
    private Expression<Number> x2Expression;
    private Expression<Number> y2Expression;
    private Expression<Number> z2Expression;
    private Expression<ChunkData> chunkDataExpression;
    private Expression<ItemStack> itemStackExpression;
    private int matchedPattern;

    @Override
    protected void execute(Event event) {
        int x1 = 0;
        int y1 = y1Expression.getSingle(event).intValue();
        int z1 = 0;
        int x2 = 15;
        int y2 = y1;
        int z2 = 15;
        switch (matchedPattern) {
            case 0:
                x1 = MathUtil.intMod(x1Expression.getSingle(event).intValue(), 16);
                z1 = MathUtil.intMod(z1Expression.getSingle(event).intValue(), 16);
                x2 = MathUtil.intMod(x2Expression.getSingle(event).intValue(), 16);
                z2 = MathUtil.intMod(z2Expression.getSingle(event).intValue(), 16);
                break;
            case 2:
                y2 = y2Expression.getSingle(event).intValue();
        }
        ChunkData chunkData = chunkDataExpression.getSingle(event);
        ItemStack itemStack = itemStackExpression.getSingle(event);
        MaterialData materialData = itemStack.getData();
        chunkData.setRegion(Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2), Math.max(x1, x2) + 1, Math.max(y1, y2) + 1, Math.max(z1, z2) + 1, materialData);
    }

    @Override
    public String toString(Event event, boolean b) {
        switch (matchedPattern) {
            case 0: return "fill region from " + x1Expression + ", " + y1Expression + ", " + z1Expression + " to " + x2Expression + ", " + y2Expression + ", " + z2Expression + " in " + chunkDataExpression + " with " + itemStackExpression;
            case 1: return "fill layer " + y1Expression + " in " + chunkDataExpression + " with " + itemStackExpression;
            case 2: return "fill layers " + y1Expression + " to " + y2Expression + " in " + chunkDataExpression + " with " + itemStackExpression;
            default: return null; //Cannot be reached
        }
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        matchedPattern = i;
        switch (matchedPattern) {
            case 0:
                x1Expression = (Expression<Number>) expressions[0];
                y1Expression = (Expression<Number>) expressions[1];
                z1Expression = (Expression<Number>) expressions[2];
                x2Expression = (Expression<Number>) expressions[3];
                y2Expression = (Expression<Number>) expressions[4];
                z2Expression = (Expression<Number>) expressions[5];
                chunkDataExpression = (Expression<ChunkData>) expressions[6];
                itemStackExpression = (Expression<ItemStack>) expressions[7];
                break;
            case 1:
                y1Expression = (Expression<Number>) expressions[0];
                chunkDataExpression = (Expression<ChunkData>) expressions[1];
                itemStackExpression = (Expression<ItemStack>) expressions[2];
                break;
            case 2:
                y1Expression = (Expression<Number>) expressions[0];
                y2Expression = (Expression<Number>) expressions[1];
                chunkDataExpression = (Expression<ChunkData>) expressions[2];
                itemStackExpression = (Expression<ItemStack>) expressions[3];
        }
        return true;
    }
}
