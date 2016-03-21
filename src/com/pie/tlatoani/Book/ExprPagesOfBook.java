package com.pie.tlatoani.Book;

import java.util.Iterator;
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

public class ExprPagesOfBook extends SimpleExpression<String>{
	private Expression<Long> pgnum1;
	private Expression<Long> pgnum2;
	private Expression<ItemStack> book;

	@Override
	public Class<? extends String> getReturnType() {
		// TODO Auto-generated method stub
		return String.class;
	}

	@Override
	public boolean isSingle() {
		// TODO Auto-generated method stub
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult arg3) {
		pgnum1 = (Expression<Long>) expr[0];
		pgnum2 = (Expression<Long>) expr[1];
		book = (Expression<ItemStack>) expr[2];
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
		List<String> list = meta.getPages();
		List<String> l = new LinkedList<String>(list);
		Integer from = null;
		if (pgnum1 != null) {
			from = pgnum1.getSingle(arg0).intValue();
		} else {
			from = 1;
		}
		Integer to = null;
		if (pgnum2 != null) {
			to = pgnum2.getSingle(arg0).intValue();
		} else {
			to = meta.getPageCount();
		}
		if (from > 1) {
			Integer del1 = (from - 1);
			int i;
			for (i = 0; i < del1; i++) {
				l.remove(0);
			}
		}
		if (to < meta.getPageCount()) {
			Integer del2 = (meta.getPageCount() - to);
			int j;
			for (j = 0; j < del2; j++) {
				l.remove(l.size() - 1);
			}
		}
		return l.toArray((String[])new String[l.size()]);
	}
	
	public Iterator<String> iterator(Event arg0) {
		ItemStack input = book.getSingle(arg0);
		BookMeta meta = (BookMeta) input.getItemMeta();
		List<String> list = meta.getPages();
		List<String> l = new LinkedList<String>(list);
		Integer from = null;
		if (pgnum1 != null) {
			from = pgnum1.getSingle(arg0).intValue();
		} else {
			from = 1;
		}
		Integer to = null;
		if (pgnum2 != null) {
			to = pgnum2.getSingle(arg0).intValue();
		} else {
			to = meta.getPageCount();
		}
		if (from > 1) {
			Integer del1 = (from - 1);
			int i;
			for (i = 0; i < del1; i++) {
				l.remove(0);
			}
		}
		if (to < meta.getPageCount()) {
			Integer del2 = (meta.getPageCount() - to);
			int j;
			for (j = 0; j < del2; j++) {
				l.remove(l.size() - 1);
			}
		}
		return l.iterator();
	}
	
	public void change(Event arg0, Object[] delta, Changer.ChangeMode mode){
		if (mode == ChangeMode.ADD) {
			BookMeta meta = (BookMeta) book.getSingle(arg0).getItemMeta();
			Integer to = null;
			if (pgnum2 != null) {
				to = pgnum2.getSingle(arg0).intValue();
			} else {
				to = meta.getPageCount();
			}
			List<String> list = meta.getPages();
			List<String> li = new LinkedList<String>(list);
			li.add(to, (String)delta[0]);
			meta.setPages(li);
			book.getSingle(arg0).setItemMeta(meta);
		}
		if (mode == ChangeMode.RESET) {
			BookMeta meta = (BookMeta) book.getSingle(arg0).getItemMeta();
			Integer from = null;
			if (pgnum1 != null) {
				from = pgnum1.getSingle(arg0).intValue();
			} else {
				from = 1;
			}
			Integer to = null;
			if (pgnum2 != null) {
				to = pgnum2.getSingle(arg0).intValue();
			} else {
				to = meta.getPageCount();
			}
			Integer delamount = (to - from);
			delamount++;
			int j;
			for (j = 0; j < delamount; j++) {
				meta.setPage(from, "");
				from++;
			}
			book.getSingle(arg0).setItemMeta(meta);
		}
		if (mode == ChangeMode.DELETE) {
			BookMeta meta = (BookMeta) book.getSingle(arg0).getItemMeta();
			Integer from = null;
			if (pgnum1 != null) {
				from = pgnum1.getSingle(arg0).intValue();
			} else {
				from = 1;
			}
			Integer to = null;
			if (pgnum2 != null) {
				to = pgnum2.getSingle(arg0).intValue();
			} else {
				to = meta.getPageCount();
			}
			List<String> list = meta.getPages();
			List<String> li = new LinkedList<String>(list);
			Integer delamount = (to - from);
			delamount++;
			int j;
			for (j = 0; j < delamount; j++) {
				li.remove(from - 1);
			}
			meta.setPages(li);
			book.getSingle(arg0).setItemMeta(meta);
		}
	}
	
	@SuppressWarnings("unchecked")
	public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
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