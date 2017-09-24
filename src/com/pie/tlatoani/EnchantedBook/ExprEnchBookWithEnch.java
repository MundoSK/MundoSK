package com.pie.tlatoani.EnchantedBook;

import javax.annotation.Nullable;

import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.EnchantmentType;
import ch.njol.util.Kleenean;

public class ExprEnchBookWithEnch extends SimpleExpression<ItemStack>{
	private Expression<ItemStack> book;
	private Expression<EnchantmentType> enchants;

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
		book = (Expression<ItemStack>) expr[0];
		enchants = (Expression<EnchantmentType>) expr[1];
		return true;
	}

	@Override
	public String toString(@Nullable Event event, boolean arg1) {
		// TODO Auto-generated method stub
		return "border length of world";
	}

	@Override
	@Nullable
	protected ItemStack[] get(Event event) {
		ItemStack input = book.getSingle(event);
		EnchantmentStorageMeta meta = (EnchantmentStorageMeta) input.getItemMeta();
		final EnchantmentType[] enchs = enchants.getArray(event);
		for (final EnchantmentType ench : enchs) {
			if (meta.hasStoredEnchant(ench.getType())) {
				meta.removeEnchant(ench.getType());
			}
			meta.addStoredEnchant(ench.getType(), ench.getLevel(), true);
		}
		ItemStack result = book.getSingle(event);
		result.setItemMeta(meta);
		return new ItemStack[]{result};
	}

}