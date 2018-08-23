package com.pie.tlatoani.Tablist;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.ProtocolLib.PacketManager;
import com.pie.tlatoani.ProtocolLib.PacketUtil;
import com.pie.tlatoani.Tablist.Group.TablistGroup;
import com.pie.tlatoani.Tablist.Player.PlayerTablist;
import com.pie.tlatoani.Core.Static.Config;
import com.pie.tlatoani.Core.Static.Scheduling;
import mundosk_libraries.packetwrapper.WrapperPlayServerNamedEntitySpawn;
import mundosk_libraries.packetwrapper.WrapperPlayServerPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Tlatoani on 4/16/17.
 * Used to manage maps with all of the Tablists and TablistGroups,
 * handle join/leave events with respect to how they affect tablists,
 * and similarly handle packet events
 */
public class TablistManager {
    private static final Map<Player, Tablist> tablistMap = new HashMap<>();
    private static final Map<String, TablistGroup> tablistGroupMap = new HashMap<>();
    private static final ArrayList<Player> playersRespawning = new ArrayList<>();

    public static final TablistGroup GLOBAL_GROUP = new TablistGroup();

    /**
     * Gets the Tablist belonging to {@code player}, creating it if necessary
     * @param player The player whose tablist (as represented by a Tablist object) will be viewed or modified
     * @return The desired Tablist
     * @throws IllegalArgumentException If {@code player} is null or offline
     */
    public static Tablist getTablistOfPlayer(Player player) {
        if (player == null || !player.isOnline()) {
            throw new IllegalArgumentException(
                    "The player parameter in getTablistOfPlayer(Player player) must be non-null and online, player: " + player);
        }
        return tablistMap.computeIfAbsent(player, Tablist::new);
    }

    /**
     * Gets the {@link TablistGroup} identified by {@code name}, creating it if necessary
     * @param name The name identifying the desired {@link TablistGroup}
     * @return The desired {@link TablistGroup}
     * @throws IllegalArgumentException If {@code name} is null
     */
    public static TablistGroup getTablistGroup(String name) {
        if (name == null) {
            throw new IllegalArgumentException(
                    "The name parameter in getTablistGroup(Player player) must be non-null, name: " + name);
        }
        return tablistGroupMap.computeIfAbsent(name, __ -> new TablistGroup());
    }

    /**
     * Deletes (and resets,
     * by calling {@link TablistGroup#reset()}) the {@link TablistGroup} identified by the specified name if it existed.
     * @param name The name identifying the {@link TablistGroup} to be deleted
     * @return {@code true} if there was such a {@link TablistGroup}, {@code false} otherwise
     * @throws IllegalArgumentException If {@code name} is null
     */
    public static boolean deleteTablistGroup(String name) {
        if (name == null) {
            throw new IllegalArgumentException(
                    "The name parameter in getTablistGroup(Player player) must be non-null, name: " + name);
        }
        TablistGroup group = tablistGroupMap.remove(name);
        if (group != null) {
            group.reset();
        }
        return group != null;
    }

    /**
     * Loads the listeners for the join/leave events and packet events
     */
    static void load() {
        Bukkit.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onJoin(PlayerJoinEvent event) {
                TablistManager.onJoin(event.getPlayer());
            }
        }, Mundo.get());
        Bukkit.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onQuit(PlayerQuitEvent event) {
                TablistManager.onQuit(event.getPlayer());
            }
        }, Mundo.get());
        loadPacketEventListeners();
    }

    /**
     * Called by the {@link PlayerJoinEvent} listener registered in {@link #load()}
     * @param player The {@link Player} that joined
     */
    private static void onJoin(Player player) {
        tablistMap.forEach((__, tablist) -> tablist.onJoin(player));
        Scheduling.syncDelay(Config.TABLIST_ADD_TO_DEFAULT_GROUP_DELAY.getCurrentValue(), () -> GLOBAL_GROUP.add(player));
    }

    /**
     * Called by the {@link PlayerQuitEvent} listener registered in {@link #load()}
     * @param player The {@link Player} that quit
     */
    private static void onQuit(Player player) {
        tablistMap.remove(player);
        tablistMap.forEach((__, tablist) -> tablist.onQuit(player));
        GLOBAL_GROUP.remove(player);
        tablistGroupMap.forEach((__, group) -> group.remove(player));
    }

    /**
     * Loads the listeners for the packet events that need to be manipulated in order for tablist stuff to work.
     * <p>
     * For {@link PacketType.Play.Server#PLAYER_INFO}, the outgoing packet is modified, if necessary, to contain
     * the desired tab attributes as specified in the corresponding {@link PlayerTablist}.
     * </p>
     * <p>
     * For {@link PacketType.Play.Server#NAMED_ENTITY_SPAWN},
     * if the player being spawned is hidden in tablist for the player receiving the packet,
     * the player receiving the packet is sent a {@link PacketType.Play.Server#PLAYER_INFO} packet
     * with the {@link EnumWrappers.PlayerInfoAction#ADD_PLAYER} action
     * and then another (delayed) packet of the same type with the {@link EnumWrappers.PlayerInfoAction#REMOVE_PLAYER} action,
     * in order to ensure that the player receiving the packet properly sees the skin of the player being spawned.
     * </p>
     * <p>
     * For {@link PacketType.Play.Server#RESPAWN},
     * if the player being respawned (note that this is the player receiving the packet) is hidden in tablist for themselves,
     * and this procedure hadn't been done recently (see below),
     * do the same process as was done for {@link PacketType.Play.Server#NAMED_ENTITY_SPAWN}.
     * </p>
     * <p>
     * For {@link PacketType.Play.Server#NAMED_ENTITY_SPAWN} and {@link PacketType.Play.Server#RESPAWN},
     * the delays to wait between sending the first and second
     * {@link PacketType.Play.Server#PLAYER_INFO} packets (as well as the time defining 'recently') are specified by
     * {@link Config#TABLIST_SPAWN_REMOVE_TAB_DELAY} and {@link Config#TABLIST_RESPAWN_REMOVE_TAB_DELAY}.
     * </p>
     */
    private static void loadPacketEventListeners() {
        PacketManager.onPacketEvent(PacketType.Play.Server.PLAYER_INFO, event -> {
            if (event.isCancelled() || event.getPlayer() == null || event.isPlayerTemporary() || !event.getPlayer().isOnline()) {
                return;
            }
            Player player = event.getPlayer();
            WrapperPlayServerPlayerInfo packet = new WrapperPlayServerPlayerInfo(event.getPacket());
            Tablist tablist = getTablistOfPlayer(player);
            List<PlayerInfoData> oldData = packet.getData();
            List<PlayerInfoData> newData = new ArrayList<>(oldData.size());
            for (PlayerInfoData oldPlayerInfoData : oldData) {
                Player objPlayer = Bukkit.getPlayer(oldPlayerInfoData.getProfile().getUUID());
                if (objPlayer == null) {
                    newData.add(oldPlayerInfoData);
                } else {
                    newData.add(tablist.onPlayerInfoPacket(oldPlayerInfoData, objPlayer));
                }
            }
            packet.setData(newData);

        });

        PacketManager.onPacketEvent(PacketType.Play.Server.NAMED_ENTITY_SPAWN, event -> {
            Player player = event.getPlayer();
            WrapperPlayServerNamedEntitySpawn packet = new WrapperPlayServerNamedEntitySpawn(event.getPacket());
            Player objPlayer = Bukkit.getPlayer(packet.getPlayerUUID());
            if (event.isCancelled() || player == null || event.isPlayerTemporary() || !player.isOnline() || objPlayer == null) {
                return;
            }
            boolean tabVisible = getTablistOfPlayer(player).isPlayerVisible(objPlayer);
            if (!tabVisible) {
                PacketManager.sendPacket(
                        PacketUtil.playerInfoPacket(objPlayer, EnumWrappers.PlayerInfoAction.ADD_PLAYER),
                        TablistManager.class,
                        player
                );
                Scheduling.syncDelay(Config.TABLIST_SPAWN_REMOVE_TAB_DELAY.getCurrentValue(), () -> {
                    PacketManager.sendPacket(
                            PacketUtil.playerInfoPacket(objPlayer, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER),
                            TablistManager.class,
                            player
                    );
                });
            }
        });

        PacketManager.onPacketEvent(PacketType.Play.Server.RESPAWN, event -> {
            Player player = event.getPlayer();
            if (event.isCancelled() || player == null || event.isPlayerTemporary() || !player.isOnline() || playersRespawning.contains(player)) {
                return;
            }
            boolean tabVisible = getTablistOfPlayer(player).isPlayerVisible(player);
            if (!tabVisible) {
                playersRespawning.add(player);
                PacketManager.sendPacket(
                        PacketUtil.playerInfoPacket(player, EnumWrappers.PlayerInfoAction.ADD_PLAYER),
                        TablistManager.class,
                        player
                );
                Scheduling.syncDelay(Config.TABLIST_RESPAWN_REMOVE_TAB_DELAY.getCurrentValue(), () -> {
                    playersRespawning.remove(player);
                    PacketManager.sendPacket(
                            PacketUtil.playerInfoPacket(player, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER),
                            TablistManager.class,
                            player
                    );
                });
            }
        });
    }

}
