package com.pie.tlatoani.Skin.Skull;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Skin.Skin;
import com.pie.tlatoani.Skin.Skull.SkullUtil;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Tlatoani on 11/23/16.
 */
public class ExprSkullFromSkin extends SimpleExpression<ItemStack> {
    private Expression<Skin> skinExpression;
    private Expression<String> ownerExpression;

    @Override
    protected ItemStack[] get(Event event) {
        Skin skin = skinExpression.getSingle(event);
        if (skin == null) {
            return new ItemStack[]{null};
        }
        String owner = ownerExpression == null ? null : ownerExpression.getSingle(event);
        return new ItemStack[]{owner == null ? SkullUtil.playerSkullItem(skin) : SkullUtil.playerSkullItem(skin, owner)};
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
        return "skull from " + skinExpression + (ownerExpression == null ? "" : " with owner " + ownerExpression);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        skinExpression = (Expression<Skin>) expressions[0];
        ownerExpression = (Expression<String>) expressions[1];
        return true;
    }
}
