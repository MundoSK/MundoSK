package com.pie.tlatoani.Tablist;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.ExpressionType;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.ProtocolLib.PacketManager;
import com.pie.tlatoani.ProtocolLib.PacketUtil;
import com.pie.tlatoani.Skin.Skin;
import com.pie.tlatoani.Tablist.Array.CondArrayEnabled;
import com.pie.tlatoani.Tablist.Array.EffEnableArrayTablist;
import com.pie.tlatoani.Tablist.Player.*;
import com.pie.tlatoani.Tablist.Simple.ExprIconOfTab;
import com.pie.tlatoani.Util.Config;
import com.pie.tlatoani.Registration.Registration;
import com.pie.tlatoani.Util.Scheduling;
import mundosk_libraries.packetwrapper.WrapperPlayServerNamedEntitySpawn;
import mundosk_libraries.packetwrapper.WrapperPlayServerPlayerInfo;
import mundosk_libraries.packetwrapper.WrapperPlayServerScoreboardTeam;
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

    public static Tablist getTablistOfPlayer(Player player) {
        if (player == null || !player.isOnline()) {
            throw new IllegalArgumentException("The player parameter in getTablistOfPlayer(Player player) must be non-null and online, player: " + player);
        }
        return tablistMap.computeIfAbsent(player, Tablist::new);
    }

    private static void onJoin(Player player) {
        tablistMap.forEach((__, tablist) -> tablist.onJoin(player));
    }

    private static void onQuit(Player player) {
        tablistMap.remove(player);
        tablistMap.forEach((__, tablist) -> tablist.onQuit(player));
    }

    public static void load() {
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

        Registration.registerExpression(ExprHeaderFooter.class, String.class, ExpressionType.PROPERTY, "tablist (0¦header|1¦footer) (for|of) %players%", "%players%'s tablist (0¦header|1¦footer)")
                .document("Tablist Header or Footer", "1.8", "An expression for the header or footer of the tablist(s) of the specified player(s). "
                        + "This is a list expression as the header and footer can have multiple lines of text.");
        Registration.registerExpressionCondition(CondScoresEnabled.class, ExpressionType.PROPERTY, "scores [are] (0¦enabled|1¦disabled) in tablist of %players%", "scores [are] (0¦enabled|1¦disabled) in %players%'s tablist")
                .document("Scores are Enabled", "1.8", "Checks whether the tablist(s) of the specified player(s) have scores enabled. "
                        + "This only applies to enabling scores using MundoSK's tablist syntaxes.");
        loadPlayer();
        loadSimple();
        loadArray();
        loadPacketEventListeners();
    }

    private static void loadPlayer() {
        Registration.registerEffect(EffChangePlayerVisibility.class,
                "(0¦show|1¦hide) [player] tab[s] of %players% for %players%",
                "(0¦show|1¦hide) %players%'s [player] tab[s] for %players%",
                "(0¦show|1¦hide) %players% for %players% in tablist",
                "(0¦show|1¦hide) %players% in %players%'s tablist",
                "(0¦show|1¦hide) %players% in [the] tablist of %players%")
                .document("Show or Hide in Tablist", "1.8", "Shows or hides certain players for other certain players in their tablist(s). "
                        + "Note: if the Players Are Visible condition/expression is set to false for a specific player and you show a player for them using this effect, "
                        + "then the condition/expression will become true but only that player will become unhidden.");
        Registration.registerEffect(EffClearPlayerModifications.class, "(clear|reset) [all] player tab modifications for %players%")
                .document("Clear Player Tab Modifications", "1.8", "Resets all of the tabs representing players for the specified player(s) to normal. "
                        + "This will make all players visible in the tablist and reset any display name, latency, and score changes.");
        Registration.registerExpression(ExprTablistName.class, String.class, ExpressionType.PROPERTY,
                "[display] name of [player] tab of %player% for %players%",
                "[display] name of %player%'s [player] tab for %players%",
                "tablist name of %player% for %players%",
                "%player%'s tablist name for %players%")
                .document("Display Name of Player Tab", "1.8", "An expression for the display name of the specified player tab in the tablist of the specified player(s). "
                        + "This will not be set if the player tab's display name has not been changed (or was reset), or the player tab is hidden.");
        Registration.registerExpression(ExprTablistLatency.class, Number.class, ExpressionType.PROPERTY,
                "(latency|ping) of [player] tab of %player% for %players%",
                "(latency|ping) of %player%'s [player] tab for %players%",
                "tablist (latency|ping) of %player% for %players%",
                "%player%'s tablist (latency|ping) for %players%")
                .document("Latency of Player Tab", "1.8.3", "An expression for the amount of latency bars of the specified player tab in the tablist of the specified player(s). "
                        + "When set, this is always between 0 and 5. This will not be set if the player tab is hidden.")
                .changer(Changer.ChangeMode.RESET, "1.8.3", "Resets any modification of the latency bars to match the player's actual latency. "
                        + "Initially, the latency bars will appear to be 5, but will change within 30 seconds if the player's actual latency requires a different amount of bars.");
        Registration.registerExpression(ExprTablistScore.class, Number.class, ExpressionType.PROPERTY,
                "score of [player] tab of %player% for %players%",
                "score of %player%'s [player] tab for %players%",
                "tablist score of %player% for %players%",
                "%player%'s tablist score for %players%")
                .document("Score of Player Tab", "1.8", "An expression for the score of the specified player tab in the tablist of the specified player(s). "
                        + "This will not be set if the player tab is hidden.");
        Registration.registerExpressionCondition(CondPlayerIsVisible.class, ExpressionType.COMBINED,
                "[player] tab of %player% is (0¦visible|1¦hidden) for %players%",
                "%player%'s [player] tab is (0¦visible|1¦hidden) for %players%",
                "%player% is (0¦visible|1¦hidden) in %players%'s tablist",
                "%player% is (0¦visible|1¦hidden) in tablist (of|for) %players%")
                .document("Player Tab is Visible", "1.8", "Checks whether the first player's tab is visible for the second specified player(s).");
        Registration.registerExpressionCondition(CondPlayersAreVisible.class, ExpressionType.PROPERTY,
                "player tabs (0¦are|1¦aren't|1¦are not) visible for %players%",
                "%players%'s tablist (contains|(0¦does|0¦do|1¦doesn't|1¦does not|1¦don't|1¦do not) contain) players",
                "tablist of %players% (contains|(0¦does|0¦do|1¦doesn't|1¦does not|1¦don't|1¦do not) contain) players",
                "players are (0¦visible|1¦hidden) in [the] tablist (of|for) %players%",
                "players are (0¦visible|1¦hidden) in %players%'s tablist")
                .document("Player Tabs Are Visible", "1.8", "Checks whether the tablist(s) of the specified player(s) allow player tabs to be visible. "
                        + "Setting this to false prevents any player tabs from being seen in the tablist for the specified player(s) (players who join will be automatically hidden). "
                        + "Setting this to true immediately makes all player tabs visible in the tablist for the specified player(s). "
                        + "Use the Show or Hide in Tablist effect if you would like to set this condition to be true without immediately showing all players. "
                        + "Note that it is possible for this condition/expression to be true yet no player tabs are visible if they are hidden manually using the Show or Hide in Tablist effect. "
                        + "In this case, players who join will still be visible in the tablist unless manually hidden using the effect.");
    }

    private static void loadSimple() {
        Registration.registerEffect(com.pie.tlatoani.Tablist.Simple.EffCreateNewTab.class,
                "create ([simple] tab [with] id|simple tab) %string% for %players% with [display] name %string% [(ping|latency) %-number%] [(head|icon|skull) %-skin%] [score %-number%]")
                .document("Create Simple Tab", "1.8", "Creates a simple tab for the specified player(s) with the specified id and properties. "
                        + "If a specified player already has a simple tab with the specified id in their tablist, that tab will not be modified and no new tab will be created. "
                        + "This effect will not work for a specified player if they have the array tablist enabled.");
        Registration.registerEffect(com.pie.tlatoani.Tablist.Simple.EffDeleteTab.class, "delete ([simple] tab [with] id|simple tab) %string% for %players%")
                .document("Delete Simple Tab", "1.8", "Removes the simple tab with the specified id from the tablist(s) of the specified player(s).");
        Registration.registerEffect(com.pie.tlatoani.Tablist.Simple.EffRemoveAllIDTabs.class, "delete all (id|simple) tabs for %players%")
                .document("Delete All Simple Tabs", "1.8", "Removes all simple tabs from the tablist(s) of the specified players(s).");
        Registration.registerExpression(com.pie.tlatoani.Tablist.Simple.ExprDisplayNameOfTab.class, String.class, ExpressionType.PROPERTY, "[display] name of ([simple] tab [with] id|simple tab) %string% for %players%")
                .document("Display Name of Simple Tab", "1.8", "An expression for the display name of the simple tab with the specified id in the tablist(s) of the specified player(s).");
        Registration.registerExpression(com.pie.tlatoani.Tablist.Simple.ExprLatencyOfTab.class, Number.class, ExpressionType.PROPERTY, "(latency|ping) of ([simple] tab [with] id|simple tab) %string% for %players%")
                .document("Latency of Simple Tab", "1.8", "An expression for the amount of latency bars of the simple tab with the specified id in the tablist(s) of the specified player(s). This is always between 0 and 5.");
        Registration.registerExpression(ExprIconOfTab.class, Skin.class, ExpressionType.PROPERTY, "(head|icon|skull) of ([simple] tab [with] id|simple tab) %string% for %players%")
                .document("Icon of Simple Tab", "1.8", "An expression for the icon of the simple tab with the specified id in the tablist(s) of the specified player(s).");
        Registration.registerExpression(com.pie.tlatoani.Tablist.Simple.ExprScoreOfTab.class, Number.class, ExpressionType.PROPERTY, "score of ([simple] tab [with] id|simple tab) %string% for %players%")
                .document("Score of Simple Tab", "1.8", "An expression for the score of the simple tab with the specified id in the tablist(s) of the specified player(s).");
    }

    private static void loadArray() {
        Registration.registerEffect(EffEnableArrayTablist.class,
                "(disable|deactivate) array tablist for %players%",
                "(enable|activate) array tablist for %players% [with [%-number% columns] [%-number% rows] [initial (head|icon|skull) %-skin%]]")
                .document("Enable or Disable Array Tablist", "1.8", "Enables or disables the array tablist for the specified player(s). "
                        + "The array tablist creates a grid of tabs that allows you to use grid coordinates to easily modify individual tabs. "
                        + "When enabling, you can specify the amount of columns (defaults to 4), the amount of rows (defaults to 20), and the initial icon (defaults to a white texture). "
                        + "See the Size of Array Tablist and Icon of Array Tabl expressions for more info. "
                        + "If the array tablist is already enabled, it will be replaced with a blank one matching the new specifications. "
                        + "Note that when enabling the array tablist, all simple tabs will be removed and all player tabs will be hidden. "
                        + "When disabling the array tablist, initially either all player tabs will be hidden, or (if if the array tablist was 4x20) all player tabs will be visible. "
                        + "There will be no simple tabs either way initially after disabling the array tablist.")
                .example("command /example_tablist:"
                        , "\ttrigger:"
                        , "\t\tenable array tablist for player #This creates a 4 x 20 grid of tabs in the player's tablist, there is further syntax for other amounts of rows/columns"
                        , "\t\tset display name of tab 1, 1 for player to \"Hello!\" #This sets the first tab as \"Hello!\""
                        , "\t\tset icon of tab 4, 20 for player to alex #This sets the icon of the last tab as the alex skin"
                        , "\t\tset display name of tab 4, 20 for player to \"ALEX\" #This sets the last tab as \"ALEX!\""
                        , "\t\tloop 20 times:"
                        , "\t\t\tset display name of tab 2, loop-number for player to \"Column 2, Row %loop-number%\" #This sets all of the tabs in the second column to display their column and row"
                        , "\t\t\tset icon of tab 2, loop-number for player to steve #This sets all of the tabs in the second column to have a steve skin as their icon");
        Registration.registerExpression(com.pie.tlatoani.Tablist.Array.ExprDisplayNameOfTab.class, String.class, ExpressionType.PROPERTY, "[display] name of [array] tab %number%, %number% for %players%")
                .document("Display Name of Array Tab", "1.8", "An expression for the display name of the specified array tab for the specified player(s).");
        Registration.registerExpression(com.pie.tlatoani.Tablist.Array.ExprLatencyOfTab.class, Number.class, ExpressionType.PROPERTY, "(latency|ping) of [array] tab %number%, %number% for %players%")
                .document("Latency of Array Tab", "1.8", "An expression for the amount of latency bars of the specified array tab for the specified player(s). This is always between 0 and 5.");
        Registration.registerExpression(com.pie.tlatoani.Tablist.Array.ExprIconOfTab.class, Skin.class, ExpressionType.PROPERTY,
                "(head|icon|skull) of [array] tab %number%, %number% for %players%",
                "initial icon of %players%'s [array] tablist")
                .document("Icon of Array Tab", "1.8", "An expression for either the icon of the specified array tab for the specified player(s), "
                        + "or the initial icon of the array tablist(s) of the specified player(s). "
                        + "The initial icon is the default icon that array tabs will have when enabling an array tablist or when increasing its size. "
                        + "By default this is a completely white texture.");
        Registration.registerExpression(com.pie.tlatoani.Tablist.Array.ExprScoreOfTab.class, Number.class, ExpressionType.PROPERTY, "score of [array] tab %number%, %number% for %players%")
                .document("Score of Array Tab", "1.8", "An expression for the score of the specified array tab for the specified player(s).");
        Registration.registerExpression(com.pie.tlatoani.Tablist.Array.ExprSizeOfTabList.class, Number.class, ExpressionType.PROPERTY, "amount of (0¦column|1¦row)s in %players%'s [array] tablist")
                .document("Size of Array Tablist", "1.8", "An expression for the amount of rows or columns in the array tablist. There can be 1 to 4 columns."
                        + "For each amount of columns, there is a specified range for the amount of rows:"
                        , "1 Column: 1 to 20 rows"
                        , "2 Columns: 11 to 20 rows"
                        , "3 Columns: 14 to 20 rows"
                        , "4 Columns: 16 to 20 rows"
                        , "This is due to the fact that Minecraft allows 1 to 80 total tabs, and for each amount, there is only one way the tablist can appear. "
                        + "Minecraft only allows a maximum of 20 tabs in one column, so the tabs will try to fill as few columns as possible will adhering to this rule. "
                        + "For example, if there are 40 tabs, this is satisfied by a 2x20 tablist, but for 41 and 42 you need 3x14.");
        Registration.registerExpressionCondition(CondArrayEnabled.class, ExpressionType.PROPERTY, "[the] array tablist is (0¦enabled|1¦disabled) for %players%")
                .document("Array Tablist is Enabled", "1.8.2", "Checks whether the array tablist is enabled or disabled for the specified players. See the Enable or Disable Array Tablist effect for more info.");
    }

    private static void loadPacketEventListeners() {
        PacketManager.onPacketEvent(PacketType.Play.Server.PLAYER_INFO, event -> {
            Player player = event.getPlayer();
            if (event.isCancelled() || player == null) {
                return;
            }
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
            if (event.isCancelled() || player == null || objPlayer == null) {
                return;
            }
            boolean tabVisible = getTablistOfPlayer(player).isPlayerVisible(objPlayer);
            if (!tabVisible) {
                PacketManager.sendPacket(PacketUtil.playerInfoPacket(objPlayer, EnumWrappers.PlayerInfoAction.ADD_PLAYER), TablistManager.class, player);
                Scheduling.syncDelay(Config.TABLIST_SPAWN_REMOVE_TAB_DELAY.getCurrentValue(), () -> {
                    PacketManager.sendPacket(PacketUtil.playerInfoPacket(objPlayer, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER), TablistManager.class, player);
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
                PacketManager.sendPacket(PacketUtil.playerInfoPacket(player, EnumWrappers.PlayerInfoAction.ADD_PLAYER), TablistManager.class, player);
                Scheduling.syncDelay(Config.TABLIST_RESPAWN_REMOVE_TAB_DELAY.getCurrentValue(), () -> {
                    playersRespawning.remove(player);
                    PacketManager.sendPacket(PacketUtil.playerInfoPacket(player, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER), TablistManager.class, player);
                });
            }
        });

        PacketManager.onPacketEvent(PacketType.Play.Server.SCOREBOARD_TEAM, event -> {
            Player player = event.getPlayer();
            if (event.isCancelled() || player == null || playersRespawning.contains(player)) {
                return;
            }
            getTablistOfPlayer(player).onScoreboardTeamPacket(new WrapperPlayServerScoreboardTeam(event.getPacket()));
        });
    }

}
