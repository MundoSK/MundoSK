package com.pie.tlatoani.TablistNew;

import ch.njol.skript.lang.ExpressionType;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.ProtocolLib.PacketManager;
import com.pie.tlatoani.Skin.Skin;
import com.pie.tlatoani.TablistNew.Array.EffEnableArrayTablist;
import com.pie.tlatoani.TablistNew.Player.*;
import com.pie.tlatoani.TablistNew.Simple.ExprIconOfTab;
import com.pie.tlatoani.Util.Registration;
import com.pie.tlatoani.Util.Scheduling;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Tlatoani on 4/16/17.
 */
public class TablistManager {
    private static final HashMap<Player, Tablist> tablistMap = new HashMap<>();
    private static final ArrayList<Player> playersRespawning = new ArrayList<>();

    public static int SPAWN_REMOVE_TAB_DELAY;
    public static int RESPAWN_REMOVE_TAB_DELAY;

    public static void load(int spawnRemoveTabDelay, int respawnRemoveTabDelay) {
        SPAWN_REMOVE_TAB_DELAY = spawnRemoveTabDelay;
        RESPAWN_REMOVE_TAB_DELAY = respawnRemoveTabDelay;

        Bukkit.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onJoin(PlayerJoinEvent event) {
                TablistManager.onJoin(event.getPlayer());
            }
        }, Mundo.INSTANCE);
        Bukkit.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onQuit(PlayerQuitEvent event) {
                TablistManager.onQuit(event.getPlayer());
            }
        }, Mundo.INSTANCE);

        Registration.registerExpression(ExprScoresEnabled.class, Boolean.class, ExpressionType.PROPERTY, "scores [are] enabled in tablist of %players%", "scores [are] enabled in %players%'s tablist");
        loadPlayer();
        loadSimple();
        loadArray();
    }

    private static void loadPlayer() {
        Registration.registerEffect(EffChangePlayerVisibility.class, "(0¦show|1¦hide) %players% for %players% in tablist", "(0¦show|1¦hide) %players% in %players%'s tablist");
        Registration.registerExpression(ExprPlayerIsVisible.class, Boolean.class, ExpressionType.COMBINED, "%player% is visible in %players%'s tablist", "%player% is visible in tablist (of|for) %players%");
        Registration.registerExpression(ExprPlayersAreVisible.class, Boolean.class, ExpressionType.PROPERTY, "%players%'s tablist contains players", "tablist of %players% contains players", "players are visible in tablist (of|for) %players%", "players are visible in %players%'s tablist");
        Registration.registerExpression(ExprTablistName.class, String.class, ExpressionType.PROPERTY, "tablist name of %player% for %players%", "%player%'s tablist name for %players%");
        Registration.registerExpression(ExprTablistScore.class, Number.class, ExpressionType.PROPERTY, "tablist score of %player% for %players%", "%player%'s tablist score for %players%");
    }

    private static void loadSimple() {
        Registration.registerEffect(com.pie.tlatoani.TablistNew.Simple.EffCreateNewTab.class, "create ([simple] tab [with] id|simple tab) %string% for %players% with [display] name %string% [(ping|latency) %-number%] [(head|icon|skull) %-skin%] [score %-number%]");
        Registration.registerEffect(com.pie.tlatoani.TablistNew.Simple.EffDeleteTab.class, "delete ([simple] tab [with] id|simple tab) %string% for %players%");
        Registration.registerEffect(com.pie.tlatoani.TablistNew.Simple.EffRemoveAllIDTabs.class, "delete all (id|simple) tabs for %players%");
        Registration.registerExpression(com.pie.tlatoani.TablistNew.Simple.ExprDisplayNameOfTab.class, String.class, ExpressionType.PROPERTY, "[display] name of ([simple] tab [with] id|simple tab) %string% for %players%");
        Registration.registerExpression(com.pie.tlatoani.TablistNew.Simple.ExprLatencyOfTab.class, Number.class, ExpressionType.PROPERTY, "(latency|ping) of ([simple] tab [with] id|simple tab) %string% for %players%");
        Registration.registerExpression(ExprIconOfTab.class, Skin.class, ExpressionType.PROPERTY, "(head|icon|skull) of ([simple] tab [with] id|simple tab) %string% for %players%");
        Registration.registerExpression(com.pie.tlatoani.TablistNew.Simple.ExprScoreOfTab.class, Number.class, ExpressionType.PROPERTY, "score of ([simple] tab [with] id|simple tab) %string% for %players%");
    }

    private static void loadArray() {
        Registration.registerEffect(EffEnableArrayTablist.class, "(disable|deactivate) array tablist for %players%", "(enable|activate) array tablist for %players% [with [%-number% columns] [%-number% rows] [initial (head|icon|skull) %-skin%]]");
        Registration.registerExpression(com.pie.tlatoani.TablistNew.Array.ExprDisplayNameOfTab.class, String.class, ExpressionType.PROPERTY, "[display] name of [array] tab %number%, %number% for %players%");
        Registration.registerExpression(com.pie.tlatoani.TablistNew.Array.ExprLatencyOfTab.class, Number.class, ExpressionType.PROPERTY, "(latency|ping) of [array] tab %number%, %number% for %players%");
        Registration.registerExpression(com.pie.tlatoani.TablistNew.Array.ExprIconOfTab.class, Skin.class, ExpressionType.PROPERTY, "(head|icon|skull) of [array] tab %number%, %number% for %players%", "initial icon of %players%'s [array] tablist");
        Registration.registerExpression(com.pie.tlatoani.TablistNew.Array.ExprScoreOfTab.class, Number.class, ExpressionType.PROPERTY, "score of [array] tab %number%, %number% for %players%");
        Registration.registerExpression(com.pie.tlatoani.TablistNew.Array.ExprSizeOfTabList.class, Number.class, ExpressionType.PROPERTY, "amount of (0¦column|1¦row)s in %players%'s [array] tablist");
    }


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
                    PacketManager.sendPacket(TablistUtil.playerInfoPacket(objPlayer, EnumWrappers.PlayerInfoAction.ADD_PLAYER), TablistManager.class, player);
                    Scheduling.syncDelay(TablistManager.SPAWN_REMOVE_TAB_DELAY, () -> {
                        PacketManager.sendPacket(TablistUtil.playerInfoPacket(objPlayer, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER), TablistManager.class, player);
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
                    PacketManager.sendPacket(TablistUtil.playerInfoPacket(player, EnumWrappers.PlayerInfoAction.ADD_PLAYER), TablistManager.class, player);
                    Scheduling.syncDelay(TablistManager.RESPAWN_REMOVE_TAB_DELAY, () -> {
                        playersRespawning.remove(player);
                        PacketManager.sendPacket(TablistUtil.playerInfoPacket(player, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER), TablistManager.class, player);
                    });
                }
            });
        }
    }

    public static Tablist getTablistOfPlayer(Player player) {
        return tablistMap.computeIfAbsent(player, __ -> new Tablist(player));
    }

    private static void onJoin(Player player) {
        tablistMap.forEach((__, tablist) -> tablist.onJoin(player));
    }

    private static void onQuit(Player player) {
        tablistMap.remove(player);
        tablistMap.forEach((__, tablist) -> tablist.onQuit(player));
    }

}
