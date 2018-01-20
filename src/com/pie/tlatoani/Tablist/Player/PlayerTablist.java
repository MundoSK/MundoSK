package com.pie.tlatoani.Tablist.Player;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.pie.tlatoani.ProtocolLib.PacketManager;
import com.pie.tlatoani.ProtocolLib.PacketUtil;
import com.pie.tlatoani.Skin.Skin;
import com.pie.tlatoani.Tablist.Tab;
import com.pie.tlatoani.Tablist.Tablist;
import com.pie.tlatoani.Tablist.TablistManager;
import com.pie.tlatoani.Util.Config;
import com.pie.tlatoani.Util.MundoUtil;
import com.pie.tlatoani.Util.Scheduling;
import mundosk_libraries.packetwrapper.WrapperPlayServerScoreboardTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Tlatoani on 4/14/17.
 */
public class PlayerTablist {
    public final Tablist tablist;
    private Optional<Map<Player, Optional<Tab>>> tabs = Optional.of(new HashMap<>());

    public PlayerTablist(Tablist tablist) {
        this.tablist = tablist;
    }

    public Optional<Tab> getTab(Player player) {
        return tabs.flatMap(map -> Optional.ofNullable(map.get(player)).orElse(Optional.empty()));
    }

    //Used to force creation of a Tab in cases where no attributes of a player's display in the tablist have been modified
    public Optional<Tab> forceTab(Player player) {
        return tabs.flatMap(map -> map.computeIfAbsent(player, __ -> Optional.of(new PlayerTab(tablist.target, player))));
    }

    public boolean isPlayerVisible(Player player) {
        return tabs.map(
                map -> Optional.ofNullable(map.get(player))
                        .map(Optional::isPresent)
                        .orElse(true)
        ).orElse(false);
    }

    public void showPlayer(Player player) {
        if (!tabs.isPresent()) {
            tabs = Optional.of(new HashMap<>());
            tabs.ifPresent(map -> {
                for (Player player1 : Bukkit.getOnlinePlayers()) {
                    map.put(player1, Optional.empty());
                }
            });
        }
        tabs.ifPresent(map -> map.computeIfPresent(player, (__, tabOptional) -> {
            if (!tabOptional.isPresent()) {
                PacketManager.sendPacket(PacketUtil.playerInfoPacket(player, EnumWrappers.PlayerInfoAction.ADD_PLAYER), PlayerTablist.class, tablist.target);
                return null;
            }
            return tabOptional;
        }));
    }

    public void hidePlayer(Player player) {
        tabs.ifPresent(map -> map.compute(player, (__, tabOptional) -> {
            if (tabOptional == null || tabOptional.isPresent()) {
                PacketManager.sendPacket(PacketUtil.playerInfoPacket(player, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER), this, tablist.target);
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
                    PacketManager.sendPacket(PacketUtil.playerInfoPacket(player, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER), this, tablist.target);
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
                }, () -> PacketManager.sendPacket(PacketUtil.playerInfoPacket(player, EnumWrappers.PlayerInfoAction.ADD_PLAYER), this, tablist.target));
            });
            map.clear();
        }, this::showAllPlayers);
    }

    public void onJoin(Player player) {
        if (!tabs.isPresent()) {
            Scheduling.syncDelay(Config.TABLIST_SPAWN_REMOVE_TAB_DELAY.getCurrentValue(), () ->
                    PacketManager.sendPacket(PacketUtil.playerInfoPacket(player, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER), this, tablist.target));
        }
    }

    public void onQuit(Player player) {
        tabs.ifPresent(map -> map.remove(player));
    }

    public PlayerInfoData onPlayerInfoPacket(PlayerInfoData oldPlayerInfoData, Player objPlayer) {
        return getTab(objPlayer).map(tab -> {
            WrappedChatComponent displayName = Optional
                    .ofNullable(tab.getDisplayName())
                    .map(rawDisplayName -> WrappedChatComponent.fromText(
                            Optional
                                    .ofNullable(tablist.target.getScoreboard())
                                    .map(scoreboard -> scoreboard.getEntryTeam(objPlayer.getName()))
                                    .map(team -> team.getPrefix() + rawDisplayName + team.getSuffix())
                                    .orElse(rawDisplayName)
                    ))
                    .orElse(oldPlayerInfoData.getDisplayName());
            return new PlayerInfoData(
                    oldPlayerInfoData.getProfile(),
                    Optional.ofNullable(tab.getLatency()).orElse(oldPlayerInfoData.getLatency()),
                    oldPlayerInfoData.getGameMode(),
                    displayName);
        }).orElse(oldPlayerInfoData);
    }

    public void onScoreboardTeamPacket(WrapperPlayServerScoreboardTeam packet) {
        for (String playerName : packet.getPlayers()) {
            Player objPlayer = Bukkit.getPlayerExact(playerName);
            if (objPlayer != null) {
                getTab(objPlayer)
                        .filter(tab -> tab.getDisplayName() != null)
                        .ifPresent(tab -> {
                            tab.sendPacket(tab.playerInfoPacket(EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME));
                        });
            }
        }
    }

    public static class PlayerTab extends Tab {
        private final Player objPlayer;

        public PlayerTab(Player target, Player player) {
            super(target, player.getName(), player.getUniqueId(), null, null, null, 0);
            objPlayer = player;
        }

        @Override
        public PacketContainer playerInfoPacket(EnumWrappers.PlayerInfoAction action) {
            return PacketUtil.playerInfoPacket(objPlayer, action);
        }

        @Override
        public void setIcon(Skin value) {
            throw new UnsupportedOperationException("You can't set the icon of a PlayerTab!");
        }

    }
}
