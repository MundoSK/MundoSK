package com.pie.tlatoani.Tablist;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.ProtocolLib.UtilPacketEvent;
import com.pie.tlatoani.Skin.Skin;
import com.pie.tlatoani.Tablist.Player.PlayerTablist;
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
    private static final LinkedList<Tablist> activeTablists = new LinkedList<>();

    private static final ArrayList<Player> playersRespawning = new ArrayList<>();

    public static final Tablist GLOBAL_TABLIST = new Tablist();

    static {
        if (Mundo.implementPacketStuff) {
            activeTablists.add(GLOBAL_TABLIST);

            UtilPacketEvent.onPacketSend(PacketType.Play.Server.PLAYER_INFO, event -> {
                Player player = event.getPlayer();
                if (event.isCancelled() || player == null) {
                    return;
                }
                List<PlayerInfoData> oldPIDs = event.getPacket().getPlayerInfoDataLists().readSafely(0);
                List<PlayerInfoData> newPIDs = new ArrayList<>();
                for (PlayerInfoData oldPlayerInfoData : oldPIDs) {
                    Player objPlayer = Bukkit.getPlayer(oldPlayerInfoData.getProfile().getUUID());
                    if (objPlayer == null) {
                        newPIDs.add(oldPlayerInfoData);
                        continue;
                    }
                    Tablist tablist = getTablistOfPlayer(player);
                    PlayerInfoData newPlayerInfoData = tablist.getPlayerTablistOrVisibility().map(playerTablist -> {
                        Tab tab = playerTablist.getTab(objPlayer);
                        if (tab == null) {
                            return oldPlayerInfoData;
                        }
                        if (tab instanceof PlayerTablist.PlayerPersonalizable) {
                            Optional<Tab.Personal> personalOptional = ((PlayerTablist.PlayerPersonalizable) tab).forPlayer(player);
                            if (personalOptional != null) {
                                if (personalOptional.isPresent()) {
                                    tab = personalOptional.get();
                                } else {
                                    return oldPlayerInfoData;
                                }
                            }
                        }
                        return new PlayerInfoData(
                                oldPlayerInfoData.getProfile(),
                                tab.getLatency() == null ? oldPlayerInfoData.getLatency() : tab.getLatency(),
                                oldPlayerInfoData.getGameMode(),
                                tab.getDisplayName() == null ? oldPlayerInfoData.getDisplayName() : WrappedChatComponent.fromText(tab.getDisplayName())
                        );
                    }, visibility -> oldPlayerInfoData);
                    newPIDs.add(newPlayerInfoData);
                }
                event.getPacket().getPlayerInfoDataLists().writeSafely(0, newPIDs);
            });

            UtilPacketEvent.onPacketSend(PacketType.Play.Server.NAMED_ENTITY_SPAWN, event -> {
                Player player = event.getPlayer();
                Player objPlayer = Bukkit.getPlayer(event.getPacket().getUUIDs().read(0));
                if (event.isCancelled() || player == null || objPlayer == null) {
                    return;
                }
                boolean tabVisible = getTablistOfPlayer(player).getPlayerTablistOrVisibility().map(
                        playerTablist -> playerTablist.isPlayerVisible(objPlayer, player),
                        visibility -> visibility
                );
                if (!tabVisible) {
                    UtilPacketEvent.sendPacket(playerInfoPacket(objPlayer, EnumWrappers.PlayerInfoAction.ADD_PLAYER), TablistManager.class, player);
                    Mundo.sync(Mundo.tablistRemoveTabDelaySpawn, () -> {
                        UtilPacketEvent.sendPacket(playerInfoPacket(objPlayer, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER), TablistManager.class, event.getPlayer());
                    });
                }
            });

            UtilPacketEvent.onPacketSend(PacketType.Play.Server.RESPAWN, event -> {
                Player player = event.getPlayer();
                if (event.isCancelled() || player == null || playersRespawning.contains(player)) {
                    return;
                }
                boolean tabVisible = getTablistOfPlayer(player).getPlayerTablistOrVisibility().map(
                        playerTablist -> playerTablist.isPlayerVisible(player, player),
                        visibility -> visibility
                );
                if (!tabVisible) {
                    playersRespawning.add(player);
                    UtilPacketEvent.sendPacket(playerInfoPacket(player, EnumWrappers.PlayerInfoAction.ADD_PLAYER), TablistManager.class, player);
                    Mundo.sync(Mundo.tablistRemoveTabDelayRespawn, () -> {
                        playersRespawning.remove(player);
                        UtilPacketEvent.sendPacket(playerInfoPacket(player, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER), TablistManager.class, event.getPlayer());
                    });
                }
            });
        }
    }


    private TablistManager() {}

    public static Tablist getTablistOfPlayer(Player player) {
        return tablistMap.get(player);
    }

    public static void setTablistOfPlayer(Player player, Tablist tablist) {
        Tablist prevTablist = getTablistOfPlayer(player);
        tablistMap.put(player, tablist);
        if (prevTablist != null) {
            prevTablist.removePlayer(player);
            if (!prevTablist.containsPlayers() && prevTablist != GLOBAL_TABLIST) {
                activeTablists.remove(prevTablist);
            }
        }
        if (!tablist.containsPlayers() && tablist != GLOBAL_TABLIST) {
            activeTablists.add(tablist);
        }
        tablist.addPlayer(player);
    }

    public static void onJoin(Player player) {
        if (getTablistOfPlayer(player) == null) {
            setTablistOfPlayer(player, GLOBAL_TABLIST);
        }
    }

    public static void onQuit(Player player) {
        Tablist tablist = getTablistOfPlayer(player);
        tablist.removePlayer(player);
        if (!tablist.containsPlayers() && tablist != GLOBAL_TABLIST) {
            activeTablists.remove(tablist);
        }
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
            skin.retrieveSkinTextures(profile.getProperties());
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
        packet.getIntegers().writeSafely(0, Mundo.firstNotNull(score, 0));
        packet.getScoreboardActions().writeSafely(0, action);
        return packet;
    }

}
