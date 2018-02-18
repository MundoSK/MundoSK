package com.pie.tlatoani.Skin;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.pie.tlatoani.Util.Reflection;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Optional;
import java.util.UUID;

/**
 * Created by Tlatoani on 2/18/18.
 */
public abstract class SkullUtil {
    public static final String DEFAULT_SKULL_OWNER = "MundoSK-Name";
    public static final UUID DEFAULT_UUID = UUID.fromString("10001000-1000-3000-8000-100010001000");
    public static final Reflection.FieldAccessor CRAFT_META_SKULL_PROFILE = Reflection.getField(Reflection.getCraftBukkitClass("inventory.CraftMetaSkull"), "profile", Reflection.getClass("com.mojang.authlib.GameProfile"));
    public static final Reflection.FieldAccessor CRAFT_SKULL_PROFILE = Reflection.getField(Reflection.getCraftBukkitClass("block.CraftSkull"), "profile", Reflection.getClass("com.mojang.authlib.GameProfile"));

    abstract WrappedGameProfile getGameProfile();
    abstract void setGameProfile(WrappedGameProfile gameProfile);

    public Skin getSkin() {
        WrappedGameProfile gameProfile = getGameProfile();
        return gameProfile == null ? Skin.EMPTY : Skin.fromGameProfile(gameProfile);
    }

    public void setSkin(Skin skin) {
        String owner = getOwner();
        setSkin(skin, owner == null ? DEFAULT_SKULL_OWNER : owner);
    }

    public void setSkin(Skin skin, String owner) {
        setGameProfile(skin.toGameProfile(owner));
    }

    public String getOwner() {
        WrappedGameProfile gameProfile = getGameProfile();
        return gameProfile == null ? null : gameProfile.getName();
    }

    public void setOwner(String owner) {
        Skin skin = getSkin();
        setGameProfile(skin == null ? new WrappedGameProfile(DEFAULT_UUID, owner) : skin.toGameProfile(owner));
    }

    public static ItemStack createSkullItem(Skin skin) {
        return createSkullItem(skin, DEFAULT_SKULL_OWNER);
    }

    public static ItemStack createSkullItem(Skin skin, String owner) {
        Held heldSkull = new Held();
        heldSkull.item.setDurability((short) SkullType.PLAYER.ordinal());
        heldSkull.setSkin(skin, owner);
        return heldSkull.item;
    }

    public static Optional<Held> fromItemStack(ItemStack itemStack) {
        if (itemStack.getType() == Material.SKULL_ITEM) {
            return Optional.of(new Held(itemStack));
        }
        return Optional.empty();
    }

    public static Optional<Placed> fromBlock(Block block) {
        if (block.getType() == Material.SKULL) {
            return Optional.of(new Placed(block));
        }
        return Optional.empty();
    }

    public static class Held extends SkullUtil {
        public final ItemStack item;

        private Held() {
            this (new ItemStack(Material.SKULL_ITEM));
        }

        private Held(ItemStack item) {
            this.item = item;
            if (item.getType() != Material.SKULL_ITEM) {
                throw new IllegalArgumentException("Illegal type: " + item.getType() + ", should be SKULL_ITEM");
            }
        }

        @Override
        WrappedGameProfile getGameProfile() {
            return WrappedGameProfile.fromHandle(CRAFT_META_SKULL_PROFILE.get(item.getItemMeta()));
        }

        @Override
        void setGameProfile(WrappedGameProfile gameProfile) {
            ItemMeta skullMeta = item.getItemMeta();
            CRAFT_META_SKULL_PROFILE.set(skullMeta, gameProfile.getHandle());
            item.setItemMeta(skullMeta);
        }
    }

    public static class Placed extends SkullUtil {
        public final Block block;

        private Placed(Block block) {
            this.block = block;
            if (block.getType() != Material.SKULL) {
                throw new IllegalArgumentException("Illegal block type: " + block.getType() + ", should be SKULL");
            }
        }

        @Override
        WrappedGameProfile getGameProfile() {
            return WrappedGameProfile.fromHandle(CRAFT_SKULL_PROFILE.get(block.getState()));
        }

        @Override
        void setGameProfile(WrappedGameProfile gameProfile) {
            BlockState skull = block.getState();
            CRAFT_SKULL_PROFILE.set(skull, gameProfile.getHandle());
            skull.update();
        }
    }
}
