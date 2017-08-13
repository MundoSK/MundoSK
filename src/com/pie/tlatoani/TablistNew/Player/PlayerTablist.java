package com.pie.tlatoani.TablistNew.Player;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.pie.tlatoani.ProtocolLib.PacketManager;
import com.pie.tlatoani.TablistNew.Tab;
import com.pie.tlatoani.TablistNew.Tablist;
import com.pie.tlatoani.TablistNew.TablistManager;
import com.pie.tlatoani.TablistNew.TablistUtil;
import com.pie.tlatoani.Util.MundoUtil;
import com.pie.tlatoani.Util.Scheduling;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Optional;

/**
 * Created by Tlatoani on 4/14/17.
 */
public class PlayerTablist {
    public final Tablist tablist;
    private Optional<HashMap<Player, Optional<Tab>>> tabs = Optional.of(new HashMap<>());

    public PlayerTablist(Tablist tablist) {
        this.tablist = tablist;
    }

    public Optional<Tab> getTab(Player player) {
        Optional<Tab> tabOptional = tabs.flatMap(map -> map.get(player));
        if (tabOptional == null) {
            return Optional.empty();
        } else {
            return tabOptional;
        }
    }

    public Optional<Tab> forceTab(Player player) {
        Optional<Tab> tabOptional = tabs.flatMap(map -> map.get(player));
        if (tabOptional == null) {
            Tab tab = new PlayerTab(tablist.target, player);
            tabs.ifPresent(map -> map.put(player, Optional.of(tab)));
            return Optional.of(tab);
        } else {
            return tabOptional;
        }
    }

    public boolean isPlayerVisible(Player player) {
        Optional<Tab> tabOptional = tabs.flatMap(map -> map.get(player));
        return tabOptional == null || tabOptional.isPresent();
    }

    public void showPlayer(Player player) {
        if (!tabs.isPresent()) {
            tabs = Optional.of(new HashMap<>());
        }
        tabs.ifPresent(map -> map.computeIfPresent(player, (__, tabOptional) -> {
            if (!tabOptional.isPresent()) {
                Tab tab = new PlayerTab(tablist.target, player);
                tab.sendPacket(tab.playerInfoPacket(EnumWrappers.PlayerInfoAction.ADD_PLAYER));
                return Optional.ofNullable(tab);
            }
            return tabOptional;
        }));
    }

    public void hidePlayer(Player player) {
        tabs.ifPresent(map -> map.compute(player, (__, tabOptional) -> {
            if (tabOptional == null || tabOptional.isPresent()) {
                PacketManager.sendPacket(TablistUtil.playerInfoPacket(player, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER), this, tablist.target);
            }
            return Optional.empty();
        }));
    }

    public boolean arePlayersVisible() {
        return tabs.isPresent();
    }

    public void showAllPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            showPlayer(player);
        }
    }

    public void hideAllPlayers() {
        tabs.ifPresent(map -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                Optional<Tab> playerTabOptional = map.get(player);
                if (playerTabOptional == null || playerTabOptional.isPresent()) {
                    PacketManager.sendPacket(TablistUtil.playerInfoPacket(player, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER), this, tablist.target);
                }
            }
            tabs = Optional.empty();
        });
    }

    public void clearModifications() {
        MundoUtil.consumeOptional(tabs, map -> {
            map.forEach((player, tabOptional) -> {
                MundoUtil.consumeOptional(tabOptional, tab -> {
                    if (tab.getDisplayName() != null) {
                        tab.setDisplayName(null);
                    }
                    if (tab.getLatency() != null) {
                        tab.setLatency(null);
                    }
                    if (tab.getScore() != null) {
                        tab.setScore(null);
                    }
                }, () -> PacketManager.sendPacket(TablistUtil.playerInfoPacket(player, EnumWrappers.PlayerInfoAction.ADD_PLAYER), this, tablist.target));
            });
            map.clear();
        }, this::showAllPlayers);
    }

    public void onJoin(Player player) {
        if (!tabs.isPresent()) {
            Scheduling.syncDelay(TablistManager.SPAWN_REMOVE_TAB_DELAY, () ->
                    PacketManager.sendPacket(TablistUtil.playerInfoPacket(player, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER), this, tablist.target));
        }
    }

    public void onQuit(Player player) {
        if (player.isOnline()) {
            throw new IllegalArgumentException("The player " + player + " is still online, so TablistNew#onQuit(Player player) should not have been called!");
        }
        tabs.ifPresent(map -> map.remove(player));
    }

    public PlayerInfoData onPlayerInfoPacket(PlayerInfoData oldPlayerInfoData, Player objPlayer) {
        return getTab(objPlayer).map(tab -> new PlayerInfoData(
                oldPlayerInfoData.getProfile(),
                tab.getLatency() == null ? oldPlayerInfoData.getLatency() : tab.getLatency(),
                oldPlayerInfoData.getGameMode(),
                tab.getDisplayName() == null ? oldPlayerInfoData.getDisplayName() : WrappedChatComponent.fromText(tab.getDisplayName()))
        ).orElse(oldPlayerInfoData);
    }

    public static class PlayerTab extends Tab {
        Player player;

        public PlayerTab(Player target, Player player) {
            super(target, player.getName(), player.getUniqueId(), null, null, null, null);
        }

        @Override
        public PacketContainer playerInfoPacket(EnumWrappers.PlayerInfoAction action) {
            return TablistUtil.playerInfoPacket(player, action);
        }

    }
}
