package com.pie.tlatoani.Book;

import ch.njol.skript.classes.Changer;
import com.pie.tlatoani.Core.Skript.ChangeablePropertyExpression;
import com.pie.tlatoani.Core.Static.OptionalUtil;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

/**
 * Created by Tlatoani on 8/16/17.
 */
public class ExprTitleOfBook extends ChangeablePropertyExpression<ItemStack, String> {

    @Override
    public void change(ItemStack itemStack, String s, Changer.ChangeMode changeMode) {
        OptionalUtil.cast(itemStack.getItemMeta(), BookMeta.class).ifPresent(bookMeta -> {
            if (changeMode == Changer.ChangeMode.SET) {
                bookMeta.setTitle(s);
            } else if (changeMode == Changer.ChangeMode.ADD) {
                bookMeta.setTitle(bookMeta.getTitle() + s);
            }
            itemStack.setItemMeta(bookMeta);
        });
    }

    @Override
    public Changer.ChangeMode[] getChangeModes() {
        return new Changer.ChangeMode[]{Changer.ChangeMode.SET, Changer.ChangeMode.ADD};
    }

    @Override
    public String convert(ItemStack itemStack) {
        return OptionalUtil.cast(itemStack.getItemMeta(), BookMeta.class).map(BookMeta::getTitle).orElse(null);
    }
}
