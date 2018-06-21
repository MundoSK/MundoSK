package com.pie.tlatoani.Skin.Skull;

import ch.njol.skript.classes.Changer;
import com.pie.tlatoani.Core.Skript.ChangeablePropertyExpression;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

/**
 * Created by Tlatoani on 2/18/18.
 */
public class ExprOwnerOfSkull extends ChangeablePropertyExpression<Object, String> {

    public static Optional<? extends SkullUtil> getSkullUtil(Object value) {
        if (value instanceof ItemStack) {
            return SkullUtil.from((ItemStack) value);
        } else if (value instanceof Block) {
            return SkullUtil.from((Block) value);
        }
        return Optional.empty();
    }

    public static Optional<? extends SkullUtil> makeSkullUtil(Object value) {
        if (value instanceof ItemStack) {
            return Optional.of(SkullUtil.make((ItemStack) value));
        } else if (value instanceof Block) {
            return Optional.of(SkullUtil.make((Block) value));
        }
        return Optional.empty();
    }

    @Override
    public void change(Object o, String s, Changer.ChangeMode changeMode) {
        makeSkullUtil(o).ifPresent(skullUtil -> skullUtil.setOwner(s));
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
