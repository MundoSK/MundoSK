package com.pie.tlatoani.Book;

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

public class ExprTitleOfBook extends SimpleExpression<String>{
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
	protected String[] get(Event arg0) {
		ItemStack input = book.getSingle(arg0);
		BookMeta meta = (BookMeta) input.getItemMeta();
		return new String[]{meta.getTitle()};
	}
	
	public void change(Event arg0, Object[] delta, Changer.ChangeMode mode){
		if (mode == ChangeMode.SET){
			BookMeta meta = (BookMeta) book.getSingle(arg0).getItemMeta();
			meta.setTitle((String)delta[0]);
			book.getSingle(arg0).setItemMeta(meta);
		}
		if (mode == ChangeMode.ADD) {
			BookMeta meta = (BookMeta) book.getSingle(arg0).getItemMeta();
			meta.setTitle(meta.getTitle() + (String)delta[0]);
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
		return null;
	}

}