package com.pie.tlatoani.Miscellaneous;

import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.Slot;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import javax.annotation.Nullable;

/**
 * Created by Tlatoani on 6/23/16.
 */
public class ArmorStandEquipmentSlot extends Slot {
    private final ArmorStand e;
    private final EquipSlot slot;

    public ArmorStandEquipmentSlot(ArmorStand e, EquipSlot slot) {
        this.e = e;
        this.slot = slot;
    }

    @Nullable
    public ItemStack getItem() {
        return slot.get(this.e);
    }

    public void setItem(@Nullable ItemStack item) {
        slot.set(this.e, item);
    }

    public String toString_i() {
        return "the " + slot.name().toLowerCase() + " of " + Classes.toString(e);
    }

    public enum EquipSlot {
        TOOL {
            @Nullable
            public ItemStack get(ArmorStand e) {
                return e.getItemInHand();
            }

            public void set(ArmorStand e, @Nullable ItemStack item) {
                e.setItemInHand(item);
            }
        },
        HELMET {
            @Nullable
            public ItemStack get(ArmorStand e) {
                return e.getHelmet();
            }

            public void set(ArmorStand e, @Nullable ItemStack item) {
                e.setHelmet(item);
            }
        },
        CHESTPLATE {
            @Nullable
            public ItemStack get(ArmorStand e) {
                return e.getChestplate();
            }

            public void set(ArmorStand e, @Nullable ItemStack item) {
                e.setChestplate(item);
            }
        },
        LEGGINGS {
            @Nullable
            public ItemStack get(ArmorStand e) {
                return e.getLeggings();
            }

            public void set(ArmorStand e, @Nullable ItemStack item) {
                e.setLeggings(item);
            }
        },
        BOOTS {
            @Nullable
            public ItemStack get(ArmorStand e) {
                return e.getBoots();
            }

            public void set(ArmorStand e, @Nullable ItemStack item) {
                e.setBoots(item);
            }
        };

        @Nullable
        public abstract ItemStack get(ArmorStand e);

        public abstract void set(ArmorStand e, @Nullable ItemStack item);

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
