package com.pie.tlatoani.Skin;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.pie.tlatoani.Util.MundoUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

/**
 * Created by Tlatoani on 12/22/16.
 */
public class ExprSkinOf extends SimpleExpression<Skin> {
    private Expression expression;

    @Override
    protected Skin[] get(Event event) {
        Object value = expression.getSingle(event);
        if (value instanceof Player) {
            return new Skin[]{SkinManager.getActualSkin((Player) value)};
        } else if (value instanceof ItemStack) {
            ItemMeta meta = ((ItemStack) value).getItemMeta();
            if (meta instanceof SkullMeta)
                return new Skin[]{Skin.getSkinOfSkull((SkullMeta) meta)};
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
        if (value != null && value instanceof ItemStack) {
            Skin skinDelta = (Skin) delta[0];
            ItemMeta meta = ((ItemStack) value).getItemMeta();
            if (meta instanceof SkullMeta)
                Skin.setSkinOfSkull((SkullMeta) meta, skinDelta);
            ((ItemStack) value).setItemMeta(meta);
        }

    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET && MundoUtil.classesCompatible(ItemStack.class, expression.getReturnType())) {
            return CollectionUtils.array(Skin.class);
        }
        return null;
    }
}
