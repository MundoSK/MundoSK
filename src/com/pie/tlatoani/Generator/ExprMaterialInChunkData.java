package com.pie.tlatoani.Generator;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.pie.tlatoani.Core.Static.MathUtil;
import org.bukkit.event.Event;
import org.bukkit.generator.ChunkGenerator.ChunkData;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

/**
 * Created by Tlatoani on 7/3/16.
 */
public class ExprMaterialInChunkData extends SimpleExpression<ItemStack> {
    private Expression<Number> xExpression;
    private Expression<Number> yExpression;
    private Expression<Number> zExpression;
    private Expression<ChunkData> chunkDataExpression;

    @Override
    protected ItemStack[] get(Event event) {
        Integer x = MathUtil.intMod(xExpression.getSingle(event).intValue(), 16);
        Integer y = yExpression.getSingle(event).intValue();
        Integer z = MathUtil.intMod(zExpression.getSingle(event).intValue(), 16);
        ChunkData chunkData = chunkDataExpression.getSingle(event);
        MaterialData materialData = chunkData.getTypeAndData(x, y, z);
        ItemStack result = new ItemStack(materialData.getItemType());
        result.setData(materialData);
        return new ItemStack[]{result};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends ItemStack> getReturnType() {
        return ItemStack.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "material at " + xExpression + ", " + yExpression + ", " + zExpression + " in %chunkdata%";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        xExpression = (Expression<Number>) expressions[0];
        yExpression = (Expression<Number>) expressions[1];
        zExpression = (Expression<Number>) expressions[2];
        chunkDataExpression = (Expression<ChunkData>) expressions[3];
        return true;
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode){
        Integer x = MathUtil.intMod(xExpression.getSingle(event).intValue(), 16);
        Integer y = yExpression.getSingle(event).intValue();
        Integer z = MathUtil.intMod(zExpression.getSingle(event).intValue(), 16);
        ChunkData chunkData = chunkDataExpression.getSingle(event);
        ItemStack itemStack = (ItemStack) delta[0];
        MaterialData materialData = itemStack.getData();
        chunkData.setBlock(x, y, z, materialData);
    }

    @SuppressWarnings("unchecked")
    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) return CollectionUtils.array(ItemStack.class);
        return null;
    }
}
