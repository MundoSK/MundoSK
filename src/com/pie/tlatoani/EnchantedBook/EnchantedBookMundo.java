package com.pie.tlatoani.EnchantedBook;

import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.util.EnchantmentType;
import com.pie.tlatoani.Core.Registration.Registration;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Tlatoani on 8/8/17.
 */
public class EnchantedBookMundo {
    
    public static void load() {
        Registration.registerExpression(ExprEnchBookWithEnch.class,ItemStack.class, ExpressionType.PROPERTY,"%itemstack% containing %enchantmenttypes%")
                .document("Enchanted Book with Enchantments", "1.4.4 or earlier", "An expression for an enchanted book identical to the one specified but also carrying the specified enchantments.");
        Registration.registerExpression(ExprEnchantLevelInEnchBook.class,Integer.class,ExpressionType.PROPERTY,"level of %enchantmenttype% within %itemstack%")
                .document("Level of Enchantment within Enchanted Book", "1.4.4 or earlier", "An expression for the level of the specified enchantment in the specified enchantment book. "
                        + "This is 0 if the specified book does not contain the specified enchantment.");
        Registration.registerExpression(ExprEnchantsInEnchBook.class,EnchantmentType.class,ExpressionType.PROPERTY,"enchants within %itemstack%")
                .document("Enchantments in Enchanted Book", "1.4.9", "An expression for a list of the enchantents contained within the specified enchantment book.");
    }
}
