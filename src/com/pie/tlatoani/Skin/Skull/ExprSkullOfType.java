package com.pie.tlatoani.Skin.Skull;

import com.pie.tlatoani.Core.Skript.MundoPropertyExpression;
import org.bukkit.SkullType;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Tlatoani on 4/16/18.
 */
public class ExprSkullOfType extends MundoPropertyExpression<SkullType, ItemStack> {

    @Override
    public ItemStack convert(SkullType skullType) {
        return SkullUtil.skullItem(skullType);
    }
}
