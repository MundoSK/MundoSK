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
        ListUtil.registerTransformer("itemstack", TransBookPages.class, "page");
        Registration.registerExpression(ExprBook.class,ItemStack.class, ExpressionType.COMBINED,"%itemstack% titled %string%, [written] by %string%, [with] pages %strings%");
        Registration.registerExpression(ExprTitleOfBook.class,String.class,ExpressionType.PROPERTY,"title of %itemstack%");
        Registration.registerExpression(ExprAuthorOfBook.class,String.class,ExpressionType.PROPERTY,"author of %itemstack%");
    }
}
