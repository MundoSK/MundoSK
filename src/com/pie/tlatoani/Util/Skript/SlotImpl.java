package com.pie.tlatoani.Util.Skript;

import ch.njol.skript.lang.Debuggable;
import com.pie.tlatoani.Registration.Registration;
import com.pie.tlatoani.Util.SlotNew;
import com.pie.tlatoani.Util.Static.Reflection;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * Created by Tlatoani on 4/7/18.
 */
public abstract class SlotImpl implements InvocationHandler {
    private static Class<? extends Debuggable> skriptSlotClass = null;
    public static final String OLD_CLASS_NAME = "ch.njol.skript.util.Slot";
    public static final String NEW_CLASS_NAME = "ch.njol.skript.util.slot.Slot";

    public static Class<? extends Debuggable> getSkriptSlotClass() {
        if (skriptSlotClass == null) {
            if (Reflection.classExists(OLD_CLASS_NAME)) {
                skriptSlotClass = ch.njol.skript.util.Slot.class;
            } else if (Reflection.classExists(NEW_CLASS_NAME)) {
                skriptSlotClass = ch.njol.skript.util.slot.Slot.class;
            } else {
                throw new IllegalStateException("Either " + OLD_CLASS_NAME + " or " + NEW_CLASS_NAME + " should exist");
            }
        }
        return skriptSlotClass;
    }

    public static boolean isLegacy() {
        if (getSkriptSlotClass().getName().equals(OLD_CLASS_NAME)) {
            return true;
        } else if (getSkriptSlotClass().getName().equals(NEW_CLASS_NAME)) {
            return false;
        }
        throw new IllegalStateException("Either " + OLD_CLASS_NAME + " or " + NEW_CLASS_NAME + " should exist");
    }

    public static <E extends Event> void registerEventValue(Class<E> event, Function<E, SlotImpl> getter) {
        if (isLegacy()) {
            Registration.registerEventValue(event, ch.njol.skript.util.Slot.class, e -> new SlotLegacy(getter.apply(e)));
        } else {
            Registration.registerEventValue(event, ch.njol.skript.util.slot.Slot.class, e -> new SlotNew(getter.apply(e)));
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
