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
	private Expression<Number> pgnum;
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
		pgnum = (Expression<Number>) expr[0];
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
		BookMeta meta = (BookMeta) book.getSingle(arg0).getItemMeta();
		if (mode == ChangeMode.SET){
			meta.setPage(pgnum.getSingle(arg0).intValue(), (String)delta[0]);
		}
		if (mode == ChangeMode.ADD) {
			meta.setPage(pgnum.getSingle(arg0).intValue(), meta.getPage(pgnum.getSingle(arg0).intValue()) + (String)delta[0]);
		}
		if (mode == ChangeMode.RESET) {
			meta.setPage(pgnum.getSingle(arg0).intValue(), "");
		}
		if (mode == ChangeMode.DELETE) {
			List<String> list = meta.getPages();
			List<String> li = new LinkedList<String>(list);
			li.remove(pgnum.getSingle(arg0).intValue() - 1);
			meta.setPages(li);
		}
		book.getSingle(arg0).setItemMeta(meta);
	}
	
	@SuppressWarnings("unchecked")
	public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
		if (mode == ChangeMode.SET || mode == ChangeMode.ADD || mode == ChangeMode.RESET || mode == ChangeMode.DELETE) return CollectionUtils.array(String.class);
		return null;
	}

}