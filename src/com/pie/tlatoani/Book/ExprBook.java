package com.pie.tlatoani.Book;

import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprBook extends SimpleExpression<ItemStack>{
	private Expression<ItemStack> book;
	private Expression<String> title;
	private Expression<String> author;
	private Expression<Long> pgcount;
	private Expression<String> texts;

	@Override
	public Class<? extends ItemStack> getReturnType() {
		// TODO Auto-generated method stub
		return ItemStack.class;
	}

	@Override
	public boolean isSingle() {
		// TODO Auto-generated method stub
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult arg3) {
		// TODO Auto-generated method stub
		book = (Expression<ItemStack>) expr[0];
		title = (Expression<String>) expr[1];
		author = (Expression<String>) expr[2];
		pgcount = (Expression<Long>) expr[3];
		texts = (Expression<String>) expr[4];
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return "border length of world";
	}

	@Override
	@Nullable
	protected ItemStack[] get(Event arg0) {
		ItemStack input = book.getSingle(arg0);
		BookMeta meta = (BookMeta) input.getItemMeta();
			meta.setTitle(title.getSingle(arg0));
			meta.setAuthor(author.getSingle(arg0));
			Integer pageamount = pgcount.getSingle(arg0).intValue();
			if (pageamount != meta.getPageCount()) {
				if (pageamount > meta.getPageCount()) {
					Integer n = (pageamount - meta.getPageCount());
					int i;
					for (i = 0; i < n; i++) {
						meta.addPage("");
					}
				} else {
					Integer n = (meta.getPageCount() - pageamount);
					int i;
					List<String> l = meta.getPages();
					for (i = 0; i < n; i++) {
						l.remove(l.size());
					}
					meta.setPages(l);
				}
			}
		if (texts != null) {
			String words[] = texts.getArray(arg0);
			Integer pagenumber =  1;
			for (String word : words) {
				meta.setPage(pagenumber, word);
				pagenumber++;
			}
		}
		ItemStack result = book.getSingle(arg0);
		result.setItemMeta(meta);
		return new ItemStack[]{result};
	}

}