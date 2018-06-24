package com.pie.tlatoani.Skin;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.pie.tlatoani.Skin.Skull.SkullUtil;
import com.pie.tlatoani.Core.Static.Utilities;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Tlatoani on 12/22/16.
 */
public class ExprSkinOf extends SimpleExpression<Skin> {
    private Expression expression;

    @Override
    protected Skin[] get(Event event) {
        Object value = expression.getSingle(event);
        if (value instanceof Player) {
            return new Skin[]{ProfileManager.getProfile((Player) value).getActualSkin()};
        } else if (value instanceof ItemStack) {
            return new Skin[]{SkullUtil.from((ItemStack) value).map(SkullUtil::getSkin).orElse(null)};
        } else if (value instanceof Block) {
            return new Skin[]{SkullUtil.from((Block) value).map(SkullUtil::getSkin).orElse(null)};
        }
        return new Skin[]{null};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Skin> getReturnType() {
        return Skin.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return expression + "'s skin";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        expression = expressions[0];
        return true;
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode){
        Object value = expression.getSingle(event);
        Skin skinDelta = (Skin) delta[0];
        if (value == null || skinDelta == null) {
            return;
        } else if (value instanceof ItemStack) {
            SkullUtil.make((ItemStack) value).setSkin(skinDelta);
        } else if (value instanceof Block) {
            SkullUtil.make((Block) value).setSkin(skinDelta);
        }
    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET && Utilities.classesCompatible(ItemStack.class, expression.getReturnType()) || Utilities.classesCompatible(Block.class, expression.getReturnType())) {
            return CollectionUtils.array(Skin.class);
        }
        return null;
    }
}
