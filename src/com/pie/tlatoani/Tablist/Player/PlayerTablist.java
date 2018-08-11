package com.pie.tlatoani.Tablist.Player;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.pie.tlatoani.Core.Static.Logging;
import com.pie.tlatoani.ProtocolLib.PacketUtil;
import com.pie.tlatoani.Tablist.Tab;
import com.pie.tlatoani.Tablist.Tablist;
import com.pie.tlatoani.Tablist.TablistManager;
import com.pie.tlatoani.Core.Static.Config;
import com.pie.tlatoani.Core.Static.OptionalUtil;
import com.pie.tlatoani.Core.Static.Scheduling;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Tlatoani on 4/14/17.
 * Used to manage the player-connected tabs of the tablist.
 */
public class PlayerTablist {
    public final Tablist tablist;
    private Optional<Map<Player, Optional<Tab>>> tabs = Optional.of(new HashMap<>());

    /**
     * Should only be called by the constructor {@link Tablist}, specifically, constructing {@code tablist}.
     * Initializes a PlayerTablist to be contained within {@code tablist}.
     */
    public PlayerTablist(Tablist tablist) {
        this.tablist = tablist;
    }

    /**
     * Returns the {@link Tab} corresponding to {@code player} to be used as a view of the tab's attributes.
     * If {@code player}'s tab is hidden or does not contain any nonempty attributes, {@link Optional#empty()} is returned.
     * @param player The player whose tab is desired
     * @return An {@link Optional} containing the corresponding {@link Tab}, or {@link Optional#empty()} as specified above
     */
    public Optional<Tab> getTabIfModified(Player player) {
        return tabs.flatMap(map -> Optional.ofNullable(map.computeIfPresent(player, (__, tabOptional) -> {
            if (tabOptional.isPresent() && tabOptional.get().isDefault()) {
                return null;
            } else {
                return tabOptional;
            }
        })).orElse(Optional.empty()));
    }

    //Used to force creation of a Tab in cases where no attributes of a player's display in the tablist have been modified

    /**
     * Returns the {@link Tab} corresponding to {@code player} to be used to mainpulate the tab's attributes.
     * If {@code player}'s tab is hidden, {@link Optional#empty()} is returned.
     * @param player The player whose tab is desired
     * @return An {@link Optional} containing the corresponding {@link Tab}, or {@link Optional#empty()} as specified above
     */
    public Optional<Tab> getTab(Player player) {
        return tabs.flatMap(map -> map.computeIfAbsent(player, __ -> Optional.of(new PlayerTab(this, player))));
    }

    /**
     * @return {@code true} if {@code player} is visible in the target's tablist, {@code false} otherwise
     */
    public boolean isPlayerVisible(Player player) {
        return tabs.map(
                map -> Optional.ofNullable(map.get(player))
                        .map(Optional::isPresent)
                        .orElse(true)
        ).orElse(false);
    }

    /**
     * Makes {@code player} visible in the target's tablist if they are not already so.
     */
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
                tablist.sendPacket(PacketUtil.playerInfoPacket(player, EnumWrappers.PlayerInfoAction.ADD_PLAYER), this);
                if (tablist.areScoresEnabled()) {
                    tablist.sendPacket(
                            PacketUtil.scorePacket(
                                    player.getName(),
                                    Tablist.OBJECTIVE_NAME,
                                    0,
                                    EnumWrappers.ScoreboardAction.CHANGE
                            ),
                            this
                    );
                }
                return null;
            }
            return tabOptional;
        }));
    }

    /**
     * Makes {@code player} hidden in the target's tablist if they are not already so.
     */
    public void hidePlayer(Player player) {
        tabs.ifPresent(map -> map.compute(player, (__, tabOptional) -> {
            if (tabOptional == null || tabOptional.isPresent()) {
                tablist.sendPacket(PacketUtil.playerInfoPacket(player, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER), this);
            }
            return Optional.empty();
        }));
    }

    /**
     * Returns {@code true} if it is possible for players to be visible in the target's tablist
     * (i. e. if a player joins, they won't be automatically hidden by MundoSK itself)
     * @return {@code true} if players might be visible in the target's tablist, {@code false} otherwise
     */
    public boolean arePlayersVisible() {
        return tabs.isPresent();
    }

    /**
     * Calls {@link #showPlayer(Player)} on all online players.
     */
    public void showAllPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            showPlayer(player);
        }
    }

    /**
     * Makes it so that no player can be seen in the tablist.
     * If a player joins, they will be automatically hidden.
     */
    public void hideAllPlayers() {
        tabs.ifPresent(map -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                Optional<Tab> playerTabOptional = map.get(player);
                if (playerTabOptional == null || playerTabOptional.isPresent()) {
                    tablist.sendPacket(PacketUtil.playerInfoPacket(player, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER), this);
                }
            }
            tabs = Optional.empty();
        });
    }

    /**
     * Returns the tablist to its natural state with respect to player-connected tabs.
     */
    public void clearModifications() {
        OptionalUtil.consume(tabs, this::showAllPlayers, map -> {
            tabs = Optional.of(new HashMap<>());
            Logging.debug(PlayerTablist.class, "#clearModifications(): map = " + map);
            map.forEach((player, tabOptional) -> {
                Logging.debug(PlayerTablist.class, "#clearModifications(): player = " + player + ", tabOpt = " + tabOptional);
                OptionalUtil.consume(
                        tabOptional,
                        () -> tablist.sendPacket(
                                PacketUtil.playerInfoPacket(player, EnumWrappers.PlayerInfoAction.ADD_PLAYER),
                                this
                        ), tab -> {
                    tab.setDisplayName(null);
                    tab.setLatencyBars(null);
                    tab.setScore(null);
                });
                Logging.debug(PlayerTablist.class, "#clearModifications(): through player = " + player);
            });
            Logging.debug(PlayerTablist.class, "#clearModifications(): clearing map");
            map.clear();
            Logging.debug(PlayerTablist.class, "#clearModifications(): map cleared");
        });
    }

    /**
     * Called only by {@link TablistManager#onJoin(Player)}.
     * Currently only serves to hide {@code player} if {@link #arePlayersVisible()} is {@code false}.
     * @param player The player who joined
     */
    public void onJoin(Player player) {
        if (!tabs.isPresent()) {
            Scheduling.syncDelay(Config.TABLIST_SPAWN_REMOVE_TAB_DELAY.getCurrentValue(), () ->
                    tablist.sendPacket(PacketUtil.playerInfoPacket(player, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER), this));
        }
    }

    /**
     * Called only by {@link TablistManager#onQuit(Player)}.
     * Removes all information relating to {@code player}
     * (no packets need to be sent since Minecraft/Bukkit will remove the player from tablist on their own
     * and no other modification is necessary).
     * @param player The player who quit
     */
    public void onQuit(Player player) {
        tabs.ifPresent(map -> map.remove(player));
    }

    /**
     * Applies all changes made to player-connected tabs in this tablist to {@code playerTablist}.
     * This method is called by {@link Tablist#applyChanges(Tablist)}.
     * @param playerTablist
     */
    public void applyChanges(PlayerTablist playerTablist) {
        OptionalUtil.consume(tabs, playerTablist::hideAllPlayers, tabMap ->
                tabMap.forEach((player, tabOptional) ->
                        OptionalUtil.consume(tabOptional, () -> playerTablist.hidePlayer(player), tab -> {
                            playerTablist.showPlayer(player);
                            tab.applyChanges(playerTablist.getTab(player).get());
                        }))
        );
    }
}
