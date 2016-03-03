package com.pie.tlatoani.Book;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;

public class ExprPageOfBook extends SimpleExpression<String>{
	private Expression<Long> pgnum;
	private Expression<ItemStack> book;

	@Override
	public Class<? extends String> getReturnType() {
		// TODO Auto-generated method stub
		return String.class;
	}

	@Override
	public boolean isSingle() {
		// TODO Auto-generated method stub
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult arg3) {
		pgnum = (Expression<Long>) expr[0];
		book = (Expression<ItemStack>) expr[1];
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return "border length of world";
	}

	@Override
	@Nullable
	protected String[] get(Event arg0) {
		ItemStack input = book.getSingle(arg0);
		BookMeta meta = (BookMeta) input.getItemMeta();
		return new String[]{meta.getPage(pgnum.getSingle(arg0).intValue())};
	}
	
	public void change(Event arg0, Object[] delta, Changer.ChangeMode mode){
		if (mode == ChangeMode.SET){
			BookMeta meta = (BookMeta) book.getSingle(arg0).getItemMeta();
			meta.setPage(pgnum.getSingle(arg0).intValue(), (String)delta[0]);
			book.getSingle(arg0).setItemMeta(meta);
		}
		if (mode == ChangeMode.ADD) {
			BookMeta meta = (BookMeta) book.getSingle(arg0).getItemMeta();
			meta.setPage(pgnum.getSingle(arg0).intValue(), meta.getPage(pgnum.getSingle(arg0).intValue()) + (String)delta[0]);
			book.getSingle(arg0).setItemMeta(meta);
		}
		if (mode == ChangeMode.RESET) {
			BookMeta meta = (BookMeta) book.getSingle(arg0).getItemMeta();
			meta.setPage(pgnum.getSingle(arg0).intValue(), "");
			book.getSingle(arg0).setItemMeta(meta);
		}
		if (mode == ChangeMode.DELETE) {
			BookMeta meta = (BookMeta) book.getSingle(arg0).getItemMeta();
			List<String> list = meta.getPages();
			List<String> li = new LinkedList<String>(list);
			li.remove(pgnum.getSingle(arg0).intValue() - 1);
			meta.setPages(li);
			book.getSingle(arg0).setItemMeta(meta);
		}
	}
	
	@SuppressWarnings("unchecked")
	public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
		if (mode == ChangeMode.SET) {
			return CollectionUtils.array(String.class);
		}
		if (mode == ChangeMode.ADD) {
			return CollectionUtils.array(String.class);
		}
		if (mode == ChangeMode.RESET) {
			return CollectionUtils.array(String.class);
		}
		if (mode == ChangeMode.DELETE) {
			return CollectionUtils.array(String.class);
		}
		return null;
	}

}