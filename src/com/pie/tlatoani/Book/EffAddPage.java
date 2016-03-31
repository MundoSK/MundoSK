package com.pie.tlatoani.Book;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class EffAddPage extends Effect{
	private Expression<String> text;
	private Expression<Number> pgnum;
	private Expression<ItemStack> book;
	private int after;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean paramKleenean, ParseResult paramParseResult) {
		text = (Expression<String>) expr[0];
		pgnum = (Expression<Number>) expr[1];
		book = (Expression<ItemStack>) expr[2];
		after = paramParseResult.mark;
		return true;
	}

	@Override
	public String toString(@Nullable Event paramEvent, boolean paramBoolean) {
		// TODO Auto-generated method stub
		return " set world border of world";
	}

	@Override
	protected void execute(Event arg0) {
		Integer page;
		ItemStack input = book.getSingle(arg0);
		BookMeta meta = (BookMeta) input.getItemMeta();
		if (pgnum == null) page = meta.getPageCount() - after;
		else page = pgnum.getSingle(arg0).intValue() - after;
		List<String> list = new LinkedList<String>(meta.getPages());
		String[] strings = text.getAll(arg0);
		for (int i = 0; i < strings.length; i++) list.add(page + i, strings[i]);
		meta.setPages(list);
		book.getSingle(arg0).setItemMeta(meta);
	}

}