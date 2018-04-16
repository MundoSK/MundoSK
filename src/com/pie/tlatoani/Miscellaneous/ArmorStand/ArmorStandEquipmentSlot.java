package com.pie.tlatoani.Miscellaneous.ArmorStand;

import ch.njol.skript.registrations.Classes;
import com.pie.tlatoani.Util.Skript.SlotImpl;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Created by Tlatoani on 6/23/16.
 */
public class ArmorStandEquipmentSlot extends SlotImpl {
    private final ArmorStand e;
    private final EquipSlot slot;

    public ArmorStandEquipmentSlot(ArmorStand e, EquipSlot slot) {
        this.e = e;
        this.slot = slot;
    }

    public ArmorStandEquipmentSlot(ArmorStand e, EquipmentSlot slot) {
        this(e, EquipSlot.getByEquipmentSlot(slot));
    }

    @Nullable
    public ItemStack getItem() {
        return slot.get(this.e);
    }

    public void setItem(@Nullable ItemStack item) {
        slot.set(this.e, item);
    }

    @Override
    public boolean isSameSlot(SlotImpl slot) {
        return slot instanceof ArmorStandEquipmentSlot && ((ArmorStandEquipmentSlot) slot).slot == this.slot;
    }

    public String toString_i() {
        return "the " + slot.name().toLowerCase() + " of " + Classes.toString(e);
    }

    public enum EquipSlot {
        TOOL(ArmorStand::getItemInHand, ArmorStand::setItemInHand),
        HELMET(ArmorStand::getHelmet, ArmorStand::setHelmet),
        CHESTPLATE(ArmorStand::getChestplate, ArmorStand::setChestplate),
        LEGGINGS(ArmorStand::getLeggings, ArmorStand::setLeggings),
        BOOTS(ArmorStand::getBoots, ArmorStand::setBoots);

        private final Function<ArmorStand, ItemStack> getter;
        private final BiConsumer<ArmorStand, ItemStack> setter;

        EquipSlot(Function<ArmorStand, ItemStack> getter, BiConsumer<ArmorStand, ItemStack> setter) {
            this.getter = getter;
            this.setter = setter;
        }


        @Nullable
        public ItemStack get(ArmorStand e) {
            return getter.apply(e);
        }

        public void set(ArmorStand e, @Nullable ItemStack item) {
            setter.accept(e, item);
        }

        public static EquipSlot getByEquipmentSlot(EquipmentSlot equipmentSlot) {
            switch (equipmentSlot) {
                case HEAD: return HELMET;
                case CHEST: return CHESTPLATE;
                case LEGS: return LEGGINGS;
                case FEET: return BOOTS;
                default: return TOOL;
            }
        }
    }

}
