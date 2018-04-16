package com.pie.tlatoani.Util.Skript;

import ch.njol.skript.util.Slot;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Tlatoani on 4/7/18.
 */
public class SlotLegacy extends Slot {
    private final SlotImpl slotImpl;

    public SlotLegacy(SlotImpl slotImpl) {
        this.slotImpl = slotImpl;
    }

    @Override
    public ItemStack getItem() {
        return slotImpl.getItem();
    }

    @Override
    public void setItem(ItemStack itemStack) {
        slotImpl.setItem(itemStack);
    }

    @Override
    protected String toString_i() {
        return slotImpl.toString_i();
    }
}
