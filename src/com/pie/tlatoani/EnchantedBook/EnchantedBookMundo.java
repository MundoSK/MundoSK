package com.pie.tlatoani.EnchantedBook;

import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.util.EnchantmentType;
import com.pie.tlatoani.Util.Registration;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Tlatoani on 8/8/17.
 */
public class EnchantedBookMundo {
    
    public static void load() {
        Registration.registerExpression(ExprEnchBookWithEnch.class,ItemStack.class, ExpressionType.PROPERTY,"%itemstack% containing %enchantmenttypes%");
        Registration.registerExpression(ExprEnchantLevelInEnchBook.class,Integer.class,ExpressionType.PROPERTY,"level of %enchantmenttype% within %itemstack%");
        Registration.registerExpression(ExprEnchantsInEnchBook.class,EnchantmentType.class,ExpressionType.PROPERTY,"enchants within %itemstack%");
    }
}
