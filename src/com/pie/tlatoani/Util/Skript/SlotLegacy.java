package com.pie.tlatoani.Util.Skript;

import ch.njol.skript.lang.Debuggable;
import ch.njol.skript.util.Slot;
import com.pie.tlatoani.Core.Registration.Registration;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;

/**
 * Created by Tlatoani on 4/7/18.
 */
public class SlotLegacy extends Slot {
    private final SlotImpl slotImpl;
    public static final Class<? extends Debuggable> SUPERCLASS = Slot.class;

    public static <E extends Event> void registerEventValue(Class<E> event, Function<E, SlotImpl> getter) {
        Registration.registerEventValue(event, Slot.class, e -> new SlotLegacy(getter.apply(e)));
    }

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
