package com.pie.tlatoani.Book;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import com.pie.tlatoani.ListUtil.Transformer;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

/**
 * Created by Tlatoani on 6/15/16.
 */
public class TransBookPages implements Transformer<String> {
    private Expression<ItemStack> book;

    @Override
    public Boolean init(Expression expression) {
        if (expression.getReturnType().isAssignableFrom(ItemStack.class) || ItemStack.class.isAssignableFrom(expression.getReturnType())) {
            book = expression;
            return true;
        }
        Skript.error("'" + expression + "' is not an item!");
        return false;
    }

    @Override
    public Class getType() {
        return String.class;
    }

    @Override
    public Boolean isSettable() {
        return true;
    }

    @Override
    public String[] get(Event event) {
        return ((BookMeta) book.getSingle(event).getItemMeta()).getPages().toArray(new String[0]);
    }

    @Override
    public void set(Event event, String[] value) {
        ((BookMeta) book.getSingle(event).getItemMeta()).setPages(value);
    }
}
