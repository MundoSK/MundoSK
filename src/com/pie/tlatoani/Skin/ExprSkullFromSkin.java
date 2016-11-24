package com.pie.tlatoani.Skin;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

/**
 * Created by Tlatoani on 11/23/16.
 */
public class ExprSkullFromSkin extends SimpleExpression<ItemStack> {
    private Expression<Skin> skinExpression;

    @Override
    protected ItemStack[] get(Event event) {
        ItemStack result = new ItemStack(Material.SKULL_ITEM);
        SkullMeta skullMeta = (SkullMeta) result.getItemMeta();
        Skin.setSkinOfSKull(skullMeta, skinExpression.getSingle(event));
        result.setItemMeta(skullMeta);
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
        return "skull from " + skinExpression;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        skinExpression = (Expression<Skin>) expressions[0];
        return true;
    }
}
