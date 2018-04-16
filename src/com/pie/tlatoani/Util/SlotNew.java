package com.pie.tlatoani.Util;

import ch.njol.skript.util.slot.Slot;
import com.pie.tlatoani.Util.Skript.SlotImpl;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

/**
 * Created by Tlatoani on 4/15/18.
 */
public class SlotNew extends Slot {
    private final SlotImpl slotImpl;

    public SlotNew(SlotImpl slotImpl) {
        this.slotImpl = slotImpl;
    }

    @Nullable
    @Override
    public ItemStack getItem() {
        return slotImpl.getItem();
    }

    @Override
    public void setItem(@Nullable ItemStack item) {
        slotImpl.setItem(item);
    }

    @Override
    public boolean isSameSlot(Slot o) {
        return o instanceof SlotNew && slotImpl.isSameSlot(((SlotNew) o).slotImpl);
    }

    @Override
    public String toString(Event event, boolean b) {
        return slotImpl.toString_i();
    }
}
