package com.pie.tlatoani.Skin.Skull;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.pie.tlatoani.Skin.Skin;
import com.pie.tlatoani.Core.Static.Reflection;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by Tlatoani on 2/18/18.
 * A utility class for dealing with skulls.
 */
public abstract class SkullUtil {
    public static final String DEFAULT_SKULL_OWNER = "MundoSK-Name";
    public static final UUID DEFAULT_UUID = UUID.fromString("10001000-1000-3000-8000-100010001000");
    public static final short DURABILITY = (short) SkullType.PLAYER.ordinal();
    public static final Reflection.FieldAccessor CRAFT_META_SKULL_PROFILE = Reflection.getField(Reflection.getCraftBukkitClass("inventory.CraftMetaSkull"), "profile", Reflection.getClass("com.mojang.authlib.GameProfile"));
    public static final Reflection.FieldAccessor CRAFT_SKULL_PROFILE = Reflection.getField(Reflection.getCraftBukkitClass("block.CraftSkull"), "profile", Reflection.getClass("com.mojang.authlib.GameProfile"));

    /**
     * @return A {@link WrappedGameProfile} containing the information representing the owner and skin of this skull,
     * or null for uncertain reasons
     */
    @Nullable
    abstract WrappedGameProfile getGameProfile();

    /**
     * Sets the game profile of this skull in order to change the owner and/or skin.
     * @param gameProfile The {@link WrappedGameProfile} containing the new owner/skin
     */
    abstract void setGameProfile(WrappedGameProfile gameProfile);

    /**
     * @return The skull type of this skull
     */
    public abstract SkullType getSkullType();

    /**
     * Sets the skull type of this skull if it is not already {@code skullType}.
     * @param skullType The new skull type
     */
    public abstract void setSkullType(SkullType skullType);

    /**
     * @return The skin of this skull, or {@link Skin#EMPTY} if it does not have a skin
     */
    public Skin getSkin() {
        WrappedGameProfile gameProfile = getGameProfile();
        return gameProfile == null ? Skin.EMPTY : Skin.fromGameProfile(gameProfile);
    }

    /**
     * Sets the skin of this skull, setting the skull type to {@link SkullType#PLAYER} if necessary.
     * @param skin The new skin
     */
    public void setSkin(Skin skin) {
        setSkullType(SkullType.PLAYER);
        String owner = getOwner();
        setSkinAndOwner(skin, owner == null ? DEFAULT_SKULL_OWNER : owner);
    }

    /**
     * @return The owner of this skull
     */
    public String getOwner() {
        WrappedGameProfile gameProfile = getGameProfile();
        return gameProfile == null ? null : gameProfile.getName();
    }

    /**
     * Sets the owner of this skull
     * @param owner The new owner
     */
    public void setOwner(String owner) {
        setSkullType(SkullType.PLAYER);
        Skin skin = getSkin();
        setGameProfile(skin == null ? new WrappedGameProfile(DEFAULT_UUID, owner) : skin.toGameProfile(owner));
    }

    /**
     * Sets the skin and the owner of this skull, setting the skull type to {@link SkullType#PLAYER} if necessary.
     * @param skin The new skin
     * @param owner The new owner
     */
    public void setSkinAndOwner(Skin skin, String owner) {
        setGameProfile(skin.toGameProfile(owner));
    }

    /**
     * Creates a skull of type {@code skullType}.
     * @param skullType The type of skull to create
     * @return The created skull
     */
    public static ItemStack skullItem(SkullType skullType) {
        Held heldSkull = new Held();
        heldSkull.setSkullType(skullType);
        return heldSkull.item;
    }

    /**
     * Creates a player skull in the form of an {@link ItemStack} of type {@link Material#SKULL_ITEM},
     * skull type {@link SkullType#PLAYER}, skin {@code skin}, and owner {@link #DEFAULT_SKULL_OWNER}
     * @param skin The skin of the new player skull
     * @return The created player skull
     */
    public static ItemStack playerSkullItem(Skin skin) {
        return playerSkullItem(skin, DEFAULT_SKULL_OWNER);
    }

    /**
     * Creates a player skull in the form of an {@link ItemStack} of type {@link Material#SKULL_ITEM},
     * skull type {@link SkullType#PLAYER}, skin {@code skin}, and owner {@code owner}
     * @param skin The skin of the new player skull
     * @param owner The owner of the new player skull
     * @return The created player skull
     */
    public static ItemStack playerSkullItem(Skin skin, String owner) {
        Held heldSkull = new Held();
        heldSkull.setSkinAndOwner(skin, owner);
        return heldSkull.item;
    }

    /**
     * Returns a Held of {@code itemStack} if {@code itemStack.getType() == Material.SKULL_ITEM},
     * otherwise returns {@link Optional#empty()}.
     * @param itemStack The item to contain in a Held
     * @return An {@link Optional} containing {@code itemStack} if its type is {@link Material#SKULL_ITEM};
     * {@link Optional#empty()} otherwise
     */
    public static Optional<Held> from(ItemStack itemStack) {
        if (itemStack.getType() == Material.SKULL_ITEM) {
            return Optional.of(new Held(itemStack));
        }
        return Optional.empty();
    }

    /**
     * Returns a Placed of {@code block} if {@code block.getType() == Material.SKULL},
     * otherwise returns {@link Optional#empty()}.
     * @param block The block to be contained in a Placed
     * @return An {@link Optional} containing {@code block} if its type is {@link Material#SKULL};
     * {@link Optional#empty()} otherwise
     */
    public static Optional<Placed> from(Block block) {
        if (block.getType() == Material.SKULL) {
            return Optional.of(new Placed(block));
        }
        return Optional.empty();
    }

    public static Held make(ItemStack itemStack) {
        if (itemStack.getType() != Material.SKULL_ITEM) {
            itemStack.setType(Material.SKULL_ITEM);
        }
        return new Held(itemStack);
    }

    public static Placed make(Block block) {
        if (block.getType() != Material.SKULL) {
            block.setType(Material.SKULL);
        }
        return new Placed(block);
    }

    public static class Held extends SkullUtil {
        public final ItemStack item;

        private Held() {
            this(new ItemStack(Material.SKULL_ITEM));
            if (item.getDurability() != DURABILITY) {
                item.setDurability(DURABILITY);
            }
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

        @Override
        public SkullType getSkullType() {
            return SkullType.values()[(int) item.getDurability()];
        }

        @Override
        public void setSkullType(SkullType skullType) {
            if (getSkullType() != skullType) {
                item.setDurability((short) skullType.ordinal());
            }
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

        private Skull getState() {
            return (Skull) block.getState();
        }

        @Override
        WrappedGameProfile getGameProfile() {
            return WrappedGameProfile.fromHandle(CRAFT_SKULL_PROFILE.get(getState()));
        }

        @Override
        void setGameProfile(WrappedGameProfile gameProfile) {
            BlockState skull = getState();
            CRAFT_SKULL_PROFILE.set(skull, gameProfile.getHandle());
            skull.update();
        }

        @Override
        public SkullType getSkullType() {
            return getState().getSkullType();
        }

        @Override
        public void setSkullType(SkullType skullType) {
            Skull skull = getState();
            if (skull.getSkullType() != skullType) {
                skull.setSkullType(skullType);
            }
            skull.update();
        }
    }
}
