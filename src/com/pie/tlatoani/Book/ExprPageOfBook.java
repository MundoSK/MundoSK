package com.pie.tlatoani.Book;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;

import com.pie.tlatoani.Mundo;
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
		return String.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult arg3) {
		pgnum = (Expression<Number>) expr[0];
		book = (Expression<ItemStack>) expr[1];
		Mundo.debug(this, "Look at thisL::::: " + book.getClass());
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "page of book";
	}

	@Override
	@Nullable
	protected String[] get(Event arg0) {
		ItemStack input = book.getSingle(arg0);
		BookMeta meta = (BookMeta) input.getItemMeta();
		Integer index = pgnum != null ? pgnum.getSingle(arg0).intValue() : meta.getPageCount();
		return new String[]{meta.getPageCount() >= index ? meta.getPage(index) : null};
	}
	
	public void change(Event arg0, Object[] delta, Changer.ChangeMode mode){
		BookMeta meta = (BookMeta) book.getSingle(arg0).getItemMeta();
		Integer index = pgnum != null ? pgnum.getSingle(arg0).intValue() : meta.getPageCount();
		if (mode == ChangeMode.SET || mode == ChangeMode.ADD || mode == ChangeMode.RESET) {
			while (meta.getPageCount() < index)  meta.addPage(""); 
			String text = null;
			if (mode == ChangeMode.SET) text = (String)delta[0];
			else if (mode == ChangeMode.ADD) text = meta.getPage(index) + (String)delta[0];
			else if (mode == ChangeMode.RESET) text = "";
			meta.setPage(index, text);
		} else if (mode == ChangeMode.DELETE) {
			if (meta.getPageCount() >= index) {
				List<String> list = meta.getPages();
				List<String> li = new LinkedList<String>(list);
				li.remove(index - 1);
				meta.setPages(li);
			}
		}
		book.getSingle(arg0).setItemMeta(meta);
	}
	
	@SuppressWarnings("unchecked")
	public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
		if (mode == ChangeMode.SET || mode == ChangeMode.ADD || mode == ChangeMode.RESET || mode == ChangeMode.DELETE) return CollectionUtils.array(String.class);
		return null;
	}

}