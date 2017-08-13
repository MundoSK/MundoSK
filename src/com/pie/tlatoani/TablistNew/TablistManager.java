package com.pie.tlatoani.TablistNew;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.ProtocolLib.PacketManager;
import com.pie.tlatoani.Skin.Skin;
import com.pie.tlatoani.Util.Scheduling;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.*;

import static com.comphenix.protocol.PacketType.Play.Server.PLAYER_INFO;

/**
 * Created by Tlatoani on 4/16/17.
 */
public class TablistManager {
    private static final HashMap<Player, Tablist> tablistMap = new HashMap<>();
    private static final ArrayList<Player> playersRespawning = new ArrayList<>();

    static {
        if (Mundo.implementPacketStuff) {
            PacketManager.onPacketEvent(PacketType.Play.Server.PLAYER_INFO, event -> {
                Player player = event.getPlayer();
                if (event.isCancelled() || player == null) {
                    return;
                }
                Tablist tablist = getTablistOfPlayer(player);
                List<PlayerInfoData> oldPIDs = event.getPacket().getPlayerInfoDataLists().readSafely(0);
                List<PlayerInfoData> newPIDs = new ArrayList<>();
                for (PlayerInfoData oldPlayerInfoData : oldPIDs) {
                    Player objPlayer = Bukkit.getPlayer(oldPlayerInfoData.getProfile().getUUID());
                    if (objPlayer == null) {
                        newPIDs.add(oldPlayerInfoData);
                    } else {
                        newPIDs.add(tablist.onPlayerInfoPacket(oldPlayerInfoData, objPlayer));
                    }
                }
                event.getPacket().getPlayerInfoDataLists().writeSafely(0, newPIDs);

            });

            PacketManager.onPacketEvent(PacketType.Play.Server.NAMED_ENTITY_SPAWN, event -> {
                Player player = event.getPlayer();
                Player objPlayer = Bukkit.getPlayer(event.getPacket().getUUIDs().read(0));
                if (event.isCancelled() || player == null || objPlayer == null) {
                    return;
                }
                boolean tabVisible = getTablistOfPlayer(player).isPlayerVisible(objPlayer);
                if (!tabVisible) {
                    PacketManager.sendPacket(playerInfoPacket(objPlayer, EnumWrappers.PlayerInfoAction.ADD_PLAYER), TablistManager.class, player);
                    Scheduling.syncDelay(TablistMundo.SPAWN_REMOVE_TAB_DELAY, () -> {
                        PacketManager.sendPacket(playerInfoPacket(objPlayer, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER), TablistManager.class, player);
                    });
                }
            });

            PacketManager.onPacketEvent(PacketType.Play.Server.RESPAWN, event -> {
                Player player = event.getPlayer();
                if (event.isCancelled() || player == null || playersRespawning.contains(player)) {
                    return;
                }
                boolean tabVisible = getTablistOfPlayer(player).isPlayerVisible(player);
                if (!tabVisible) {
                    playersRespawning.add(player);
                    PacketManager.sendPacket(playerInfoPacket(player, EnumWrappers.PlayerInfoAction.ADD_PLAYER), TablistManager.class, player);
                    Scheduling.syncDelay(TablistMundo.RESPAWN_REMOVE_TAB_DELAY, () -> {
                        playersRespawning.remove(player);
                        PacketManager.sendPacket(playerInfoPacket(player, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER), TablistManager.class, player);
                    });
                }
            });
        }
    }

    private TablistManager() {}

    public static Tablist getTablistOfPlayer(Player player) {
        return tablistMap.computeIfAbsent(player, __ -> new Tablist(player));
    }

    public static void onJoin(Player player) {
        tablistMap.forEach((__, tablist) -> tablist.onJoin(player));
    }

    public static void onQuit(Player player) {
        tablistMap.remove(player);
        tablistMap.forEach((__, tablist) -> tablist.onQuit(player));
    }

    //Packet Utility Methods

    public static PacketContainer playerInfoPacket(
            String displayName,
            Integer latency,
            GameMode gameMode,
            String name,
            UUID uuid,
            Skin skin,
            EnumWrappers.PlayerInfoAction action
    ) {
        PacketContainer result = new PacketContainer(PLAYER_INFO);
        WrappedGameProfile profile = new WrappedGameProfile(uuid, name);
        if (action == EnumWrappers.PlayerInfoAction.ADD_PLAYER) {
            if (skin == null) {
                skin = Tablist.DEFAULT_SKIN_TEXTURE;
            }
            profile.getProperties().put(Skin.MULTIMAP_KEY, skin.toWrappedSignedProperty());
        }
        PlayerInfoData playerInfoData = new PlayerInfoData(
                profile,
                latency == null ? 5 : latency,
                gameMode == null ? EnumWrappers.NativeGameMode.NOT_SET : EnumWrappers.NativeGameMode.fromBukkit(gameMode),
                WrappedChatComponent.fromText(displayName == null ? "" : displayName)
        );
        result.getPlayerInfoDataLists().writeSafely(0, Collections.singletonList(playerInfoData));
        result.getPlayerInfoAction().writeSafely(0, action);
        return result;
    }

    public static PacketContainer playerInfoPacket(Player player, EnumWrappers.PlayerInfoAction action) {
        PacketContainer result = new PacketContainer(PLAYER_INFO);
        PlayerInfoData playerInfoData = new PlayerInfoData(
                WrappedGameProfile.fromPlayer(player),
                5,
                EnumWrappers.NativeGameMode.fromBukkit(player.getGameMode()),
                WrappedChatComponent.fromText(player.getPlayerListName())
        );
        result.getPlayerInfoDataLists().writeSafely(0, Collections.singletonList(playerInfoData));
        result.getPlayerInfoAction().writeSafely(0, action);
        return result;
    }

    public static PacketContainer scorePacket(String scoreName, String objectiveName, Integer score, EnumWrappers.ScoreboardAction action) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_SCORE);
        packet.getStrings().writeSafely(0, scoreName);
        packet.getStrings().writeSafely(1, objectiveName);
        packet.getIntegers().writeSafely(0, Optional.of(score).orElse(0));
        packet.getScoreboardActions().writeSafely(0, action);
        return packet;
    }

}
