package com.pie.tlatoani.Book;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class ExprBook extends SimpleExpression<ItemStack>{
	private Expression<String> title;
	private Expression<String> author;
	private Expression<String> texts;

	@Override
	public Class<? extends ItemStack> getReturnType() {
		return ItemStack.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult arg3) {
		title = (Expression<String>) expr[0];
		author = (Expression<String>) expr[1];
		texts = (Expression<String>) expr[2];
		return true;
	}

	@Override
	public String toString(Event event, boolean arg1) {
		return "written book titled " + title + " written by " + author + " with pages " + texts;
	}

	@Override
	protected ItemStack[] get(Event event) {
		ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta meta = (BookMeta) book.getItemMeta();
		meta.setTitle(title.getSingle(event));
		meta.setAuthor(author.getSingle(event));
		meta.setPages(texts.getArray(event));
		book.setItemMeta(meta);
		return new ItemStack[]{book};
	}

}