package com.pie.tlatoani.Book;

import ch.njol.skript.lang.ExpressionType;
import com.pie.tlatoani.ListUtil.ListUtil;
import com.pie.tlatoani.Util.Registration;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Tlatoani on 8/8/17.
 */
public class BookMundo {

    public static void load() {
        ListUtil.registerTransformer(TransBookPages.class, "itemstack", "page");
        Registration.registerExpression(ExprBook.class, ItemStack.class, ExpressionType.COMBINED,"[written] book titled %string%[,] [written] by %string%[,] [with] pages %strings%");
        Registration.registerPropertyExpression(ExprTitleOfBook.class, String.class, "itemstacks", "title");
        Registration.registerPropertyExpression(ExprAuthorOfBook.class, String.class, "itemstacks", "author");

    }
}
