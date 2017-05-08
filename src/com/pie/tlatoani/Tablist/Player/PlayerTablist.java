package com.pie.tlatoani.Tablist.Player;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.pie.tlatoani.ProtocolLib.UtilPacketEvent;
import com.pie.tlatoani.Tablist.Tab;
import com.pie.tlatoani.Tablist.Tablist;
import com.pie.tlatoani.Tablist.TablistManager;
import com.pie.tlatoani.Util.Either;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Optional;

/**
 * Created by Tlatoani on 4/14/17.
 */
public class PlayerTablist {
    public final Tablist tablist;
    private final Tablist.Storage storage;
    private final HashMap<Player, Optional<Tab>> tabs = new HashMap<>();

    public PlayerTablist(Tablist.Storage storage) {
        this.tablist = storage.tablist;
        this.storage = storage;
    }

    public Tab getTab(Player player) {
        Optional<Tab> tabOptional = tabs.get(player);
        if (tabOptional == null) {
            return null;
        } else {
            return tabOptional.orElse(null);
        }
    }

    public Tab forceTab(Player player) {
        Optional<Tab> tabOptional = tabs.get(player);
        if (tabOptional == null) {
            Tab tab = new PlayerTab(storage, player);
            tabs.put(player, Optional.of(tab));
            return tab;

        } else {
            return tabOptional.orElse(null);
        }
    }

    public boolean isPlayerVisible(Player player) {
        Optional<Tab> tabOptional = tabs.get(player);
        return tabOptional == null || tabOptional.isPresent();
    }

    public void showPlayer(Player player) {
        tabs.computeIfPresent(player, (__, tabOptional) -> {
            if (!tabOptional.isPresent()) {
                Tab tab = new PlayerTab(storage, player);
                tab.sendPacket(tab.playerInfoPacket(EnumWrappers.PlayerInfoAction.ADD_PLAYER));
                return Optional.ofNullable(tab);
            }
            return tabOptional;
        });
    }

    public void hidePlayer(Player player) {
        tabs.compute(player, (__, tabOptional) -> {
            if (tabOptional == null || tabOptional.isPresent()) {
                UtilPacketEvent.sendPacket(TablistManager.playerInfoPacket(player, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER), this, player);
            }
            return Optional.empty();
        });
    }

    public void showAllPlayers() {
        Bukkit.getOnlinePlayers().forEach(this::showPlayer);
    }

    public void hideAllPlayers() {
        storage.playerTablistOrVisibility = Either.right(false);
        Bukkit.getOnlinePlayers().forEach(this::hidePlayer);
    }

    public static class PlayerTab extends Tab {
        Player player;

        public PlayerTab(Tablist.Storage storage, Player player) {
            super(storage, player.getName(), player.getUniqueId(), null, null, null, null);
        }

        @Override
        public PacketContainer playerInfoPacket(EnumWrappers.PlayerInfoAction action) {
            return TablistManager.playerInfoPacket(player, action);
        }

    }
}
