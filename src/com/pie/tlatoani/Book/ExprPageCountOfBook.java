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

public class ExprPageCountOfBook extends SimpleExpression<Integer>{
	private Expression<ItemStack> book;

	@Override
	public Class<? extends Integer> getReturnType() {
		// TODO Auto-generated method stub
		return Integer.class;
	}

	@Override
	public boolean isSingle() {
		// TODO Auto-generated method stub
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult arg3) {
		book = (Expression<ItemStack>) expr[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return "border length of world";
	}

	@Override
	@Nullable
	protected Integer[] get(Event arg0) {
		ItemStack input = book.getSingle(arg0);
		BookMeta meta = (BookMeta) input.getItemMeta();
		return new Integer[]{meta.getPageCount()};
	}
	
	public void change(Event arg0, Object[] delta, Changer.ChangeMode mode){
		if (mode == ChangeMode.SET){
			BookMeta meta = (BookMeta) book.getSingle(arg0).getItemMeta();
			book.getSingle(arg0).setItemMeta(meta);
			Integer pageamount = ((Long)delta[0]).intValue();
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
					List<String> list = meta.getPages();
					List<String> l = new LinkedList<String>(list);
					for (i = 0; i < n; i++) {
						l.remove(l.size() - 1);
					}
					meta.setPages(l);
				}
			}
			book.getSingle(arg0).setItemMeta(meta);
		}
		if (mode == ChangeMode.ADD) {
			BookMeta meta = (BookMeta) book.getSingle(arg0).getItemMeta();
			Integer n = ((Long)delta[0]).intValue();
			int i;
			for (i = 0; i < n; i++) {
				meta.addPage("");
			}
			book.getSingle(arg0).setItemMeta(meta);
		}
		if (mode == ChangeMode.REMOVE) {
			BookMeta meta = (BookMeta) book.getSingle(arg0).getItemMeta();
			Integer n = ((Long)delta[0]).intValue();
			int i;
			List<String> list = meta.getPages();
			List<String> l = new LinkedList<String>(list);
			for (i = 0; i < n; i++) {
				l.remove(l.size() - 1);
			}
			meta.setPages(l);
			book.getSingle(arg0).setItemMeta(meta);
		}
	}
	
	@SuppressWarnings("unchecked")
	public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
		if (mode == ChangeMode.SET) {
			return CollectionUtils.array(Long.class);
		}
		if (mode == ChangeMode.ADD) {
			return CollectionUtils.array(Long.class);
		}
		if (mode == ChangeMode.REMOVE) {
			return CollectionUtils.array(Long.class);
		}
		return null;
	}

}