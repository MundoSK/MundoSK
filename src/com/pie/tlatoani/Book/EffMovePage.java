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

public class EffMovePage extends Effect{
	private Expression<Number> pgnum1;
	private Expression<Number> pgnum2;
	private Expression<Number> pgnum3;
	private Expression<ItemStack> book;
	private Expression<Number> move;
	private int direc;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean paramKleenean, ParseResult paramParseResult) {
		pgnum1 = (Expression<Number>) expr[0];
		pgnum2 = (Expression<Number>) expr[1];
		pgnum3 = (Expression<Number>) expr[2];
		book = (Expression<ItemStack>) expr[3];
		move = (Expression<Number>) expr[4];
		direc = paramParseResult.mark;
		return true;
	}

	@Override
	public String toString(@Nullable Event paramEvent, boolean paramBoolean) {
		return "move pages of book";
	}

	@Override
	protected void execute(Event arg0) {
		Integer page1 = null;
		Integer page2 = null;
		ItemStack input = book.getSingle(arg0);
		BookMeta meta = (BookMeta) input.getItemMeta();
		if (pgnum3 == null) {
			if (pgnum1 == null) page1 = meta.getPageCount() - 1;
			else page1 = pgnum1.getSingle(arg0).intValue() - 1;
			if (pgnum2 == null) page2 = page1;
			else page2 = pgnum2.getSingle(arg0).intValue() - 1;
		} else {
			page1 = meta.getPageCount() - pgnum3.getSingle(arg0).intValue();
			page2 = meta.getPageCount() - 1;
		}
		if (page2 < page1) {
			int tempto = page2;
			page2 = page1;
			page1 = tempto;
		}
		if (page1 >= meta.getPageCount()) return;
		if (page2 >= meta.getPageCount()) page2 = meta.getPageCount() - 1;
		Integer velo = move.getSingle(arg0).intValue() * direc;
		while (meta.getPageCount() < (page2 + 1 + velo)) meta.addPage("");
		if (page1 + velo < 0) velo += (page1 + velo) * -1;
		List<String> list = new LinkedList<String>(meta.getPages());
		List<String> pages = new LinkedList<String>();
		for (int i = page1; i <= page2; i++) {
			pages.add(list.get(page1));
			list.remove(page1.intValue());
		}
		for (int i = 0; i < pages.size(); i++) list.add(page1 + i + velo, pages.get(i));
		meta.setPages(list);
		book.getSingle(arg0).setItemMeta(meta);
	}

}