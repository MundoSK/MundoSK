package com.pie.tlatoani.EnchantedBook;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.EnchantmentType;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;

public class ExprEnchantsInEnchBook extends SimpleExpression<EnchantmentType>{
	private Expression<ItemStack> book;

	@Override
	public Class<? extends EnchantmentType> getReturnType() {
		// TODO Auto-generated method stub
		return EnchantmentType.class;
	}

	@Override
	public boolean isSingle() {
		// TODO Auto-generated method stub
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean arg2, ParseResult arg3) {
		book = (Expression<ItemStack>) expr[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event event, boolean arg1) {
		// TODO Auto-generated method stub
		return "border length of world";
	}

	@Override
	@Nullable
	protected EnchantmentType[] get(Event event) {
		ItemStack input = book.getSingle(event);
		EnchantmentStorageMeta meta = (EnchantmentStorageMeta) input.getItemMeta();
		Map<Enchantment, Integer> map = meta.getStoredEnchants();
		final List<EnchantmentType> l = new ArrayList<EnchantmentType>();
		for (Map.Entry<Enchantment, Integer> ansh : map.entrySet()) {
			l.add(new EnchantmentType(ansh.getKey(), ansh.getValue()));
		}
		return l.toArray((EnchantmentType[])new EnchantmentType[l.size()]);
	}
	
	public Iterator<EnchantmentType> iterator(Event event) {
		ItemStack input = book.getSingle(event);
		EnchantmentStorageMeta meta = (EnchantmentStorageMeta) input.getItemMeta();
		Map<Enchantment, Integer> map = meta.getStoredEnchants();
		final List<EnchantmentType> l = new ArrayList<EnchantmentType>();
		for (Map.Entry<Enchantment, Integer> ansh : map.entrySet()) {
			l.add(new EnchantmentType(ansh.getKey(), ansh.getValue()));
		}
		return l.iterator();
	}
	
	public void change(Event event, Object[] delta, Changer.ChangeMode mode){
		EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getSingle(event).getItemMeta();
		if (mode == ChangeMode.SET) {
			Map<Enchantment, Integer> map = meta.getStoredEnchants();
			for (Map.Entry<Enchantment, Integer> ansh : map.entrySet()) {
				meta.removeStoredEnchant(ansh.getKey());
			}
			for (int i = 0; i < delta.length; i++) {
				Enchantment adde = ((EnchantmentType)delta[i]).getType();
				Integer addi = ((EnchantmentType)delta[i]).getLevel();
				if (meta.hasStoredEnchant(adde)) {
					meta.removeStoredEnchant(adde);
				}
				meta.addStoredEnchant(adde, addi, true);
			}
		}
		if (mode == ChangeMode.ADD) {
			for (int i = 0; i < delta.length; i++) {
				Enchantment adde = ((EnchantmentType)delta[i]).getType();
				Integer addi = ((EnchantmentType)delta[i]).getLevel();
				if (meta.hasStoredEnchant(adde)) {
					meta.removeStoredEnchant(adde);
				}
				meta.addStoredEnchant(adde, addi, true);
			}
			
		}
		if (mode == ChangeMode.DELETE) {
			Map<Enchantment, Integer> map = meta.getStoredEnchants();
			for (Map.Entry<Enchantment, Integer> ansh : map.entrySet()) {
				meta.removeStoredEnchant(ansh.getKey());
			}
		}
		if (mode == ChangeMode.REMOVE) {
			for (int i = 0; i < delta.length; i++) {
				Enchantment adde = ((EnchantmentType)delta[i]).getType();
				if (meta.hasStoredEnchant(adde)) {
					meta.removeStoredEnchant(adde);
				}
			}
		}
		book.getSingle(event).setItemMeta(meta);
	}
	
	@SuppressWarnings("unchecked")
	public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
		if (mode == ChangeMode.ADD || mode == ChangeMode.REMOVE || mode == ChangeMode.DELETE || mode == ChangeMode.SET) return CollectionUtils.array(EnchantmentType[].class);
		return null;
	}

}