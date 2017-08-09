package com.pie.tlatoani.Book;

import ch.njol.skript.lang.ExpressionType;
import com.pie.tlatoani.ListUtil.ListUtil;
import com.pie.tlatoani.Mundo;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Tlatoani on 8/8/17.
 */
public class BookMundo {

    public static void load() {
        ListUtil.registerTransformer("itemstack", TransBookPages.class, "page");
        Mundo.registerExpression(ExprBook.class,ItemStack.class, ExpressionType.COMBINED,"%itemstack% titled %string%, [written] by %string%, [with] pages %strings%");
        Mundo.registerExpression(ExprTitleOfBook.class,String.class,ExpressionType.PROPERTY,"title of %itemstack%");
        Mundo.registerExpression(ExprAuthorOfBook.class,String.class,ExpressionType.PROPERTY,"author of %itemstack%");
    }
}
