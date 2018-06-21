package com.pie.tlatoani.Book;

import ch.njol.skript.lang.ExpressionType;
import com.pie.tlatoani.ListUtil.ListUtil;
import com.pie.tlatoani.Core.Registration.Registration;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Tlatoani on 8/8/17.
 */
public class BookMundo {

    public static void load() {
        ListUtil.registerTransformer(TransBookPages.class, String.class, "itemstack", "page")
                .document("Page ListUtil", "1.6.8", "A ListUtil specifier (see the ListUtil expression for more info) that allows you to manipulate the pages of a book. "
                        + "This is done by, in a ListUtil effect or expression, writing 'page' where '%listutil%' is, and inputting a book for '%objects%'.");
        Registration.registerExpression(ExprBook.class, ItemStack.class, ExpressionType.COMBINED,"[written] book titled %string%[,] [written] by %string%[,] [with] pages %strings%")
                .document("New Book", "1.8", "An expression for a book with the specified title, author, and pages.")
                .example("give player book titled \"Harry Potter\", by \"J.K. Rowling\", with pages \"Page 1 of Harry Potter\" and \"Page 2 of Harry Potter\"");
        Registration.registerPropertyExpression(ExprTitleOfBook.class, String.class, "itemstacks", "title")
                .document("Title of Book", "1.4 or earlier", "An expression for the title of the specified book.");
        Registration.registerPropertyExpression(ExprAuthorOfBook.class, String.class, "itemstacks", "author")
                .document("Author of Book", "1.4 or earlier", "An expression for the author of the specified book.");
    }
}
