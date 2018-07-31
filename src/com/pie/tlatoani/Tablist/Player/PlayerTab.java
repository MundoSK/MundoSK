package com.pie.tlatoani.Tablist.Player;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.pie.tlatoani.ProtocolLib.PacketUtil;
import com.pie.tlatoani.Skin.Skin;
import com.pie.tlatoani.Tablist.Tab;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * Used to simplify creation of {@link Tab}s corresponding to players,
 * catch bugs related to players who are not online,
 * and catch bugs related to calling {@link Tab#setIcon(Skin)}
 * (since this will cause problems with the player's actual skin).
 */
public class PlayerTab extends Tab {
    private final Player objPlayer;

    /**
     * Initializes a PlayerTab corresponding to {@code player}.
     * @param player
     * @throws IllegalArgumentException If {@code !player.isOnline()}
     */
    PlayerTab(PlayerTablist playerTablist, Player player) {
        super(playerTablist.tablist, player.getName(), player.getUniqueId());
        if (!player.isOnline()) {
            throw new IllegalArgumentException("The player parameter in the constructor of PlayerTab must be online: " + player);
        }
        objPlayer = player;
    }

    @Override
    public PacketContainer playerInfoPacket(EnumWrappers.PlayerInfoAction action) {
        return PacketUtil.playerInfoPacket(objPlayer, action);
    }

    @Override
    public Optional<Skin> getIcon() {
        throw new UnsupportedOperationException("You can't get the icon of a PlayerTab!");
    }

    @Override
    public void setIcon(Skin value) {
        throw new UnsupportedOperationException("You can't set the icon of a PlayerTab!");
    }
}
