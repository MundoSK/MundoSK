package com.pie.tlatoani.Book;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.util.ConvertedExpression;
import com.pie.tlatoani.ListUtil.Transformer;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

/**
 * Created by Tlatoani on 6/15/16.
 */
public class TransBookPages implements Transformer<String>, Transformer.Resettable<String>, Transformer.Addable<String, String> {
    private Expression<ItemStack> book;

    @Override
    public Boolean init(Expression expression) {
        if (expression == null) {
            Skript.error("There needs to be an item!");
            return false;
        }
        if (expression.getReturnType().isAssignableFrom(ItemStack.class) || ItemStack.class.isAssignableFrom(expression.getReturnType())) {
            book = expression;
            return true;
        }
        Expression<ItemStack> convertedExpression = ConvertedExpression.newInstance(expression, ItemStack.class);
        if (convertedExpression != null) {
            book = convertedExpression;
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
    public boolean isSettable() {
        return true;
    }

    @Override
    public String[] get(Event event) {
        return ((BookMeta) book.getSingle(event).getItemMeta()).getPages().toArray(new String[0]);
    }

    @Override
    public void set(Event event, String[] value) {
        ItemStack book = this.book.getSingle(event);
        BookMeta meta = (BookMeta) book.getItemMeta();
        meta.setPages(value);
        book.setItemMeta(meta);
    }

    @Override
    public String reset() {
        return "";
    }

    @Override
    public String add(String orig, String addend) {
        return orig + addend;
    }

    @Override
    public Class<? extends String> getAddendType() {
        return String.class;
    }
}
