package com.pie.tlatoani.Skin;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

/**
 * Created by Tlatoani on 11/23/16.
 */
public class ExprSkinOfSkull extends SimpleExpression<Skin> {
    private Expression<ItemStack> skull;

    @Override
    protected Skin[] get(Event event) {
        ItemMeta meta = skull.getSingle(event).getItemMeta();
        if (meta instanceof SkullMeta)
            return new Skin[]{Skin.getSkinOfSkull((SkullMeta) meta)};
        else
            return new Skin[0];
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
        return "skin of " + skull;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        skull = (Expression<ItemStack>) expressions[0];
        return true;
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode){
        Skin skinDelta = (Skin) delta[0];
        ItemMeta meta = skull.getSingle(event).getItemMeta();
        if (meta instanceof SkullMeta)
            Skin.setSkinOfSKull((SkullMeta) meta, skinDelta);
    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(Skin.class);
        }
        return null;
    }
}
