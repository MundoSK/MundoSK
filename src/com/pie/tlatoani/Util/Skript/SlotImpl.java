package com.pie.tlatoani.Util.Skript;

import ch.njol.skript.lang.Debuggable;
import com.pie.tlatoani.Core.Static.Reflection;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * Created by Tlatoani on 4/7/18.
 */
public abstract class SlotImpl implements InvocationHandler {
    public static final String OLD_CLASS_NAME = "ch.njol.skript.util.Slot";
    public static final String NEW_CLASS_NAME = "ch.njol.skript.util.slot.Slot";
    private static Boolean legacy = null;

    public static boolean isLegacy() {
        if (legacy == null) {
            if (Reflection.classExists(OLD_CLASS_NAME)) {
                legacy = true;
            } else if (Reflection.classExists(NEW_CLASS_NAME)) {
                legacy = false;
            } else {
                throw new IllegalStateException("Either " + OLD_CLASS_NAME + " or " + NEW_CLASS_NAME + " should exist");
            }
        }
        return legacy;
    }

    public static Class<? extends Debuggable> getSkriptSlotClass() {
        if (isLegacy()) {
            return SlotLegacy.SUPERCLASS;
        } else {
            return SlotNew.SUPERCLASS;
        }
    }

    public static <E extends Event> void registerEventValue(Class<E> event, Function<E, SlotImpl> getter) {
        if (isLegacy()) {
            SlotLegacy.registerEventValue(event, getter);
        } else {
            SlotNew.registerEventValue(event, getter);
        }
    }

    public Debuggable getSkriptForm() {
        if (isLegacy()) {
            return new SlotLegacy(this);
        } else {
            return new SlotNew(this);
        }
    }

    public abstract ItemStack getItem();

    public abstract void setItem(ItemStack itemStack);

    public abstract boolean isSameSlot(SlotImpl slot);

    public abstract String toString_i();

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        switch (method.getName()) {
            case "getItem": return getItem();
            case "setItem": setItem((ItemStack) args[0]); return null;
            case "toString_i": return toString_i();
        }
        throw new IllegalArgumentException("Illegal method: " + method);
    }
}
