package com.pie.tlatoani.EnchantedBook;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.EnchantmentType;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.pie.tlatoani.Core.Static.Logging;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import javax.annotation.Nullable;

public class ExprEnchantLevelInEnchBook extends SimpleExpression<Integer>{
	private Expression<EnchantmentType> enchant;
	private Expression<ItemStack> book;

	@Override
	public Class<? extends Integer> getReturnType() {
		return Integer.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult arg3) {
		enchant = (Expression<EnchantmentType>) expr[0];
		book = (Expression<ItemStack>) expr[1];
		return true;
	}

	@Override
	public String toString(@Nullable Event event, boolean arg1) {
		return "border length of world";
	}

	@Override
	@Nullable
	protected Integer[] get(Event event) {
		ItemStack input = book.getSingle(event);
		EnchantmentStorageMeta meta = (EnchantmentStorageMeta) input.getItemMeta();
		return new Integer[]{meta.getStoredEnchantLevel(enchant.getSingle(event).getType())};
	}
	
	public void change(Event event, Object[] delta, Changer.ChangeMode mode){
		EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getSingle(event).getItemMeta();
		Enchantment ench = enchant.getSingle(event).getType();
		Integer level = meta.getStoredEnchantLevel(ench);
		Logging.info("Initial level: " + level);
		if (meta.hasStoredEnchant(ench)) meta.removeStoredEnchant(ench);
		if (mode == ChangeMode.SET) level = ((Number) delta[0]).intValue();
		if (mode == ChangeMode.ADD) level += ((Number) delta[0]).intValue();
		if (mode == ChangeMode.REMOVE) level -= ((Number) delta[0]).intValue();
		Logging.info("New level: " + level);
		if (level > 0) meta.addStoredEnchant(ench, level, true);
		book.getSingle(event).setItemMeta(meta);
	}
	
	@SuppressWarnings("unchecked")
	public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
		if (mode == ChangeMode.ADD || mode == ChangeMode.REMOVE || mode == ChangeMode.SET) return CollectionUtils.array(Number.class);
		return null;
	}

}