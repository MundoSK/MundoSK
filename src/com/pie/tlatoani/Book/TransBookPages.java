package com.pie.tlatoani.Book;

import ch.njol.skript.lang.Expression;
import com.pie.tlatoani.ListUtil.Transformer;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.function.Function;

/**
 * Created by Tlatoani on 6/15/16.
 */
public class TransBookPages implements Transformer<String>, Transformer.Resettable<String>, Transformer.Addable<String, String> {
    private Expression<ItemStack> book;

    @Override
    public boolean init(Expression expression) {
        book = expression;
        return true;
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
    public void set(Event event, Function<Object[], Object[]> changer) {
        ItemStack book = this.book.getSingle(event);
        BookMeta meta = (BookMeta) book.getItemMeta();
        meta.setPages((String[]) changer.apply(meta.getPages().toArray()));
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
