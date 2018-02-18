package com.pie.tlatoani.Skin;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Util.ChangeablePropertyExpression;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

/**
 * Created by Tlatoani on 2/18/18.
 */
public class ExprOwnerOfSkull extends ChangeablePropertyExpression<Object, String> {

    private Optional<? extends SkullUtil> getSkullUtil(Object value) {
        if (value instanceof ItemStack) {
            return SkullUtil.fromItemStack((ItemStack) value);
        } else if (value instanceof Block) {
            return SkullUtil.fromBlock((Block) value);
        }
        return Optional.empty();
    }

    @Override
    public void change(Object o, String s, Changer.ChangeMode changeMode) {
        getSkullUtil(o).ifPresent(skullUtil -> skullUtil.setOwner(s));
    }

    @Override
    public Changer.ChangeMode[] getChangeModes() {
        return new Changer.ChangeMode[]{Changer.ChangeMode.SET};
    }

    @Override
    public String convert(Object o) {
        return getSkullUtil(o).map(SkullUtil::getOwner).orElse(null);
    }
}
