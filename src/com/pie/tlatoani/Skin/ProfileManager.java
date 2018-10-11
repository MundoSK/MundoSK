package com.pie.tlatoani.Skin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.pie.tlatoani.ProtocolLib.PacketManager;
import com.pie.tlatoani.ProtocolLib.PacketUtil;
import com.pie.tlatoani.Skin.ModifiableProfile.Specific;
import com.pie.tlatoani.Tablist.TablistManager;
import com.pie.tlatoani.Core.Static.Logging;
import com.pie.tlatoani.Core.Static.Reflection;
import com.pie.tlatoani.Core.Static.Scheduling;
import com.pie.tlatoani.Util.WorldLockedLocation;
import mundosk_libraries.packetwrapper.WrapperPlayServerPlayerInfo;
import mundosk_libraries.packetwrapper.WrapperPlayServerScoreboardScore;
import mundosk_libraries.packetwrapper.WrapperPlayServerScoreboardTeam;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.*;

/**
 * Created by Tlatoani on 1/20/18.
 */
public class ProfileManager {
    static final Map<Player, ModifiableProfile> profileMap = new HashMap<>();

    private static final ArrayList<Player> spawnedPlayers = new ArrayList<>();

    private static Reflection.MethodInvoker CRAFT_PLAYER_GET_HANDLE = null;
    private static Reflection.MethodInvoker DEDICATED_PLAYER_LIST_MOVE_TO_WORLD = null;
    private static Reflection.MethodInvoker CRAFT_WORLD_GET_HANDLE = null;
    private static Reflection.FieldAccessor WORLD_SERVER_DIMENSION = null;

    public static void loadReflectionStuff() {
        try {
            CRAFT_PLAYER_GET_HANDLE = Reflection.getTypedMethod(
                    Reflection.getCraftBukkitClass("entity.CraftPlayer"), "getHandle",
                    Reflection.getMinecraftClass("EntityPlayer"));
            if (Bukkit.getVersion().contains("1.13")) {
                DEDICATED_PLAYER_LIST_MOVE_TO_WORLD = Reflection.getMethod(
                        Reflection.getMinecraftClass("DedicatedPlayerList"), "moveToWorld",
                        Reflection.getMinecraftClass("EntityPlayer"),
                        Reflection.getMinecraftClass("DimensionManager"),
                        boolean.class,
                        Location.class,
                        boolean.class
                );
                CRAFT_WORLD_GET_HANDLE = Reflection.getTypedMethod(
                        Reflection.getCraftBukkitClass("CraftWorld"), "getHandle",
                        Reflection.getMinecraftClass("WorldServer"));
                WORLD_SERVER_DIMENSION = Reflection.getField(
                        Reflection.getMinecraftClass("WorldServer"), "dimension",
                        Reflection.getMinecraftClass("DimensionManager")
                );
            } else {
                DEDICATED_PLAYER_LIST_MOVE_TO_WORLD = Reflection.getMethod(
                        Reflection.getMinecraftClass("DedicatedPlayerList"), "moveToWorld",
                        Reflection.getMinecraftClass("EntityPlayer"),
                        int.class,
                        boolean.class,
                        Location.class,
                        boolean.class
                );
            }
        } catch (Exception e) {
            Logging.reportException(ProfileManager.class, e);
        }
    }

    public static void loadPacketEvents() {
        PacketManager.onPacketEvent(PacketType.Play.Server.PLAYER_INFO, event -> {
            if (event.isCancelled() || event.getPlayer() == null || event.isPlayerTemporary() || !event.getPlayer().isOnline()) {
                return;
            }
            Player target = event.getPlayer();
            WrapperPlayServerPlayerInfo packet = new WrapperPlayServerPlayerInfo(event.getPacket());
            if (packet.getAction() == EnumWrappers.PlayerInfoAction.ADD_PLAYER || packet.getAction() == EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME) {
                Logging.debug(ProfileManager.class, "Player Info, target = " + target.getName() + ", action = " + packet.getAction());
                List<PlayerInfoData> oldData = packet.getData();
                List<PlayerInfoData> newData = new ArrayList<>(oldData.size());
                for (PlayerInfoData oldPlayerInfoData : oldData) {
                    Player player = Bukkit.getPlayer(oldPlayerInfoData.getProfile().getUUID());
                    if (player == null) {
                        newData.add(oldPlayerInfoData);
                        continue;
                    }
                    Logging.debug(ProfileManager.class, "Player Info Packet: " + player.getName());
                    if (!spawnedPlayers.contains(player)) {
                        Logging.debug(ProfileManager.class, "New player!");
                        spawnedPlayers.add(player);
                    }
                    Logging.debug(ProfileManager.class, "Old nametag = " + oldPlayerInfoData.getProfile().getName());
                    Specific specificProfile = getProfile(player).getSpecificProfile(target);
                    WrappedChatComponent displayName = oldPlayerInfoData.getDisplayName();
                    Logging.debug(ProfileManager.class, "Old displayName = " + displayName);
                    if (displayName == null) {
                        String rawDisplayName = Optional
                                .ofNullable(target.getScoreboard())
                                .map(scoreboard -> scoreboard.getEntryTeam(player.getName()))
                                .map(team -> team.getPrefix() + player.getName() + team.getSuffix())
                                .orElse(player.getName());
                        displayName = WrappedChatComponent.fromText(rawDisplayName);
                        Logging.debug(ProfileManager.class, "New displayName = " + displayName);
                    }
                    String nametag = specificProfile.getNametag();
                    //Code change to allow NameTagEdit to work theoretically
                    if (nametag.equals(player.getName())) {
                        nametag = oldPlayerInfoData.getProfile().getName();
                    }
                    PlayerInfoData newPlayerInfoData = new PlayerInfoData(
                            oldPlayerInfoData.getProfile().withName(nametag),
                            oldPlayerInfoData.getLatency(),
                            oldPlayerInfoData.getGameMode(),
                            displayName
                    );
                    Logging.debug(ProfileManager.class, "New nametag = " + newPlayerInfoData.getProfile().getName());
                    if (packet.getAction() == EnumWrappers.PlayerInfoAction.ADD_PLAYER) {
                        Skin skin = specificProfile.getDisplayedSkin();
                        Logging.debug(ProfileManager.class, "Skin replacement (may not exist): " + skin);
                        if (skin != null) {
                            newPlayerInfoData.getProfile().getProperties().put(Skin.MULTIMAP_KEY, skin.toWrappedSignedProperty());
                        }
                    }
                    newData.add(newPlayerInfoData);
                }
                packet.setData(newData);
            }
        });

        PacketManager.onPacketEvent(PacketType.Play.Server.SCOREBOARD_TEAM, event -> {
            if (event.isCancelled() || event.getPlayer() == null || event.isPlayerTemporary() || !event.getPlayer().isOnline()) {
                return;
            }
            Player target = event.getPlayer();
            WrapperPlayServerScoreboardTeam packet = new WrapperPlayServerScoreboardTeam(event.getPacket());
            Logging.debug(ProfileManager.class, "Scoreboard Team Packet");
            if (packet.getMode() == WrapperPlayServerScoreboardTeam.Mode.TEAM_UPDATED) {
                Collection<String> modifiedNames = Optional
                        .ofNullable(target.getScoreboard())
                        .map(scoreboard -> scoreboard.getTeam(packet.getName()))
                        .map(Team::getEntries)
                        .orElse(Collections.emptySet());
                for (String name : modifiedNames) {
                    Player player = Bukkit.getPlayerExact(name);
                    if (player != null && player.isOnline()) {
                        Logging.debug(ProfileManager.class, "Player " + name + ", updating");
                        PacketManager.sendPacket(PacketUtil.playerInfoPacket(player, EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME), ProfileManager.class, target);
                    }
                }
            } else if (packet.getMode() == WrapperPlayServerScoreboardTeam.Mode.TEAM_REMOVED) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    PacketManager.sendPacket(PacketUtil.playerInfoPacket(player, EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME), ProfileManager.class, target);
                    Logging.debug(ProfileManager.class, "Player " + player.getName() + ", updating");
                }
            } else {
                Collection<String> oldNames = packet.getPlayers();
                Collection<String> newNames = new HashSet<>(oldNames.size());
                for (String name : oldNames) {
                    newNames.add(name);
                    Player player = Bukkit.getPlayerExact(name);
                    if (player != null && player.isOnline()) {
                        Specific specificProfile = getProfile(player).getSpecificProfile(target);
                        String nameTag = specificProfile.getNametag();
                        if (!name.equals(nameTag)) {
                            newNames.add(nameTag);
                        }
                        Logging.debug(ProfileManager.class, "Player " + name + ", nameTag = " + nameTag);
                        PacketManager.sendPacket(PacketUtil.playerInfoPacket(player, EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME), ProfileManager.class, target);
                    }
                }
                Logging.debug(ProfileManager.class, "oldNames = " + oldNames);
                Logging.debug(ProfileManager.class, "newNames = " + newNames);
                packet.setPlayers(newNames);
            }
        });

        PacketManager.onPacketEvent(PacketType.Play.Server.SCOREBOARD_SCORE, event -> {
            if (event.isCancelled() || event.getPlayer() == null || event.isPlayerTemporary() || !event.getPlayer().isOnline()) {
                return;
            }
            Player target = event.getPlayer();
            WrapperPlayServerScoreboardScore packet = new WrapperPlayServerScoreboardScore(event.getPacket());
            Optional
                    .ofNullable(packet.getScoreName())
                    .map(Bukkit::getPlayerExact)
                    .ifPresent(player -> {
                        packet.setScoreName(getProfile(player).getSpecificProfile(target).getNametag());
                        Logging.debug(ProfileManager.class, "Replacing score for player = " + player);
                    });
        });
    }

    //Join/Leave Events

    static void onQuit(Player player) {
        profileMap.remove(player);
        for (ModifiableProfile generalProfile : profileMap.values()) {
            generalProfile.onQuit(player);
        }
        spawnedPlayers.remove(player);
    }

    //API Stuffs

    public static ModifiableProfile getProfile(Player player) {
        if (player == null || !player.isOnline()) {
            throw new IllegalArgumentException("Player must be non-null and online: " + player);
        }
        return profileMap.computeIfAbsent(player, ModifiableProfile::new);
    }

    //Manipulations stuffs

    static void refreshPlayer(Player player, Player target) {
        if (!spawnedPlayers.contains(player)) {
            return;
        }
        if (player.equals(target)) {
            respawnPlayer(player);
            return;
        }
        target.hidePlayer(player);
        Scheduling.syncDelay(1, () -> target.showPlayer(player));
        //DO NOT REMOVE THE FOLLOWING CODE
        //It ensures that targets who are not currently tracking the player and thus will not receive a spawn packet
        //still have the tab hidden for them if necessary
        Scheduling.syncDelay(2, () -> {
            if (!TablistManager.getTablistOfPlayer(target).isPlayerVisible(player)) {
                PacketManager.sendPacket(PacketUtil.playerInfoPacket(player, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER), ProfileManager.class, target);
            }
        });
    }

    private static void respawnPlayer(Player player) {
        PacketManager.sendPacket(PacketUtil.playerInfoPacket(player, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER), ProfileManager.class, player);
        PacketManager.sendPacket(PacketUtil.playerInfoPacket(player, EnumWrappers.PlayerInfoAction.ADD_PLAYER), ProfileManager.class, player);

        Location playerLoc = new WorldLockedLocation(player.getLocation());
        Logging.debug(ProfileManager.class, "playerLoc = " + playerLoc);
        try {
            Logging.debug(ProfileManager.class, "DEDICATED_PLAYER_LIST_MOVE_TO_WORLD: " + DEDICATED_PLAYER_LIST_MOVE_TO_WORLD);
            Logging.debug(ProfileManager.class, "NMS_SERVER: " + DEDICATED_PLAYER_LIST_MOVE_TO_WORLD);
            Logging.debug(ProfileManager.class, "DEDICATED_PLAYER_LIST_MOVE_TO_WORLD: " + DEDICATED_PLAYER_LIST_MOVE_TO_WORLD);
            DEDICATED_PLAYER_LIST_MOVE_TO_WORLD.invoke(Reflection.NMS_SERVER, CRAFT_PLAYER_GET_HANDLE.invoke(player), convertDimension(player.getWorld()), true, playerLoc, true);
        } catch (Exception e) {
            Logging.debug(ProfileManager.class, "Failed to make player see his skin change: " + player.getName());
            Logging.reportException(ProfileManager.class, e);
        }
    }

    private static Object convertDimension(World world) {
        if (Bukkit.getVersion().contains("1.13")) {
            Object worldHandle = CRAFT_WORLD_GET_HANDLE.invoke(world);
            return WORLD_SERVER_DIMENSION.get(worldHandle);
        } else {
            switch (world.getEnvironment()) {
                case NORMAL:
                    return 0;
                case NETHER:
                    return -1;
                case THE_END:
                    return 1;
                default:
                    throw new IllegalArgumentException("Dimension: " + world.getEnvironment());
            }
        }
    }
}
