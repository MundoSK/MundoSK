package com.pie.tlatoani.Tablist;

import ch.njol.skript.lang.ExpressionType;
import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.Skin.ExprTabName;
import com.pie.tlatoani.Skin.Skin;
import com.pie.tlatoani.Skin.SkinManager;
import com.pie.tlatoani.Tablist.Array.ArrayTablist;
import com.pie.tlatoani.Tablist.Array.EffEnableArrayTablist;
import com.pie.tlatoani.Tablist.Simple.ExprIconOfTab;
import com.pie.tlatoani.Util.Logging;
import com.pie.tlatoani.Util.Registration;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

/**
 * Created by Tlatoani on 8/8/17.
 */
public class TablistMundo {
    public static int SPAWN_REMOVE_TAB_DELAY;
    public static int RESPAWN_REMOVE_TAB_DELAY;
    
    public static void load(int spawnRemoveTabDelay, int respawnRemoveTablDelay) {
        SPAWN_REMOVE_TAB_DELAY = spawnRemoveTabDelay;
        RESPAWN_REMOVE_TAB_DELAY = respawnRemoveTablDelay;
        Registration.registerExpression(ExprTabName.class, String.class, ExpressionType.PROPERTY, "%player%'s [mundo[sk]] tab[list] name", "[mundo[sk]] tab[list] name of %player%");
        Registration.registerType(Tablist.class, "tablist");
        Bukkit.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onJoin(PlayerJoinEvent event) {
                Tablist.onJoin(event.getPlayer());
                SkinManager.onJoin(event.getPlayer());
            }
        }, Mundo.instance);
        Bukkit.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onQuit(PlayerQuitEvent event) {
                Tablist.onQuit(event.getPlayer());
                SkinManager.onQuit(event.getPlayer());
            }
        }, Mundo.instance);
        Bukkit.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onRespawn(PlayerRespawnEvent event) {
                SkinManager.onRespawn(event.getPlayer());
            }
        }, Mundo.instance);
        Registration.registerExpression(ExprTablistContainsPlayers.class, Boolean.class, ExpressionType.PROPERTY, "(%-tablist%|%-player%'s tablist) contains players");
        Registration.registerExpression(ExprNewTablist.class, Tablist.class, ExpressionType.SIMPLE, "new tablist");
        Registration.registerExpression(ExprScoresEnabled.class, Boolean.class, ExpressionType.PROPERTY, "scores enabled in (%-tablist%|%-player%'s tablist)");
        Registration.registerExpression(ExprTablistName.class, String.class, ExpressionType.PROPERTY, "tablist name of %player% (in %-tablist%|for %-player%)", "%player%'s tablist name (in %-tablist%|for %-player%)");
        Registration.registerExpression(ExprTablistScore.class, Number.class, ExpressionType.PROPERTY, "tablist score of %player% (in %-tablist%|for %-player%)", "%player%'s tablist score (in %-tablist%|for %-player%)");
        Registration.registerEffect(EffChangePlayerVisibility.class, "(0¦show|1¦hide) %players% in (%-tablist%|tab[list] of %player%)");
        Registration.registerEffect(EffSetTablist.class, "set tablist of %players% to %tablist%", "set %player%'s tablist to %tablist%");
        {
            //Simple
            Registration.registerEffect(com.pie.tlatoani.Tablist.Simple.EffCreateNewTab.class, "create tab id %string% (in %-tablist%|for %-player%) with [display] name %string% [(ping|latency) %-number%] [(head|icon|skull) %-skin%] [score %-number%]");
            Registration.registerEffect(com.pie.tlatoani.Tablist.Simple.EffDeleteTab.class, "delete tab id %string% (in %-tablist%|for %-player%)");
            Registration.registerEffect(com.pie.tlatoani.Tablist.Simple.EffRemoveAllIDTabs.class, "delete all id tabs (in %-tablist%|for %-player%)");
            Registration.registerExpression(com.pie.tlatoani.Tablist.Simple.ExprDisplayNameOfTab.class, String.class, ExpressionType.PROPERTY, "[display] name of tab id %string% (in %-tablist%|for %-player%)");
            Registration.registerExpression(com.pie.tlatoani.Tablist.Simple.ExprLatencyOfTab.class, Number.class, ExpressionType.PROPERTY, "(latency|ping) of tab id %string% (in %-tablist%|for %-player%)");
            Registration.registerExpression(ExprIconOfTab.class, Skin.class, ExpressionType.PROPERTY, "(head|icon|skull) of tab id %string% (in %-tablist%|for %-player%)");
            Registration.registerExpression(com.pie.tlatoani.Tablist.Simple.ExprScoreOfTab.class, Number.class, ExpressionType.PROPERTY, "score of tab id %string% (in %-tablist%|for %-player%)");
        } {
            //Array
            Logging.debug(TablistMundo.class, "ArrayTablist.class = " + ArrayTablist.class);
            Registration.registerEffect(EffEnableArrayTablist.class, "(disable|deactivate) array tablist for %player%", "(enable|activate) array tablist for %player% [with [%-number% columns] [%-number% rows] [initial (head|icon|skull) %-skin%]]");
            Registration.registerExpression(com.pie.tlatoani.Tablist.Array.ExprDisplayNameOfTab.class, String.class, ExpressionType.PROPERTY, "[display] name of tab %number%, %number% (in %-tablist%|for %-player%)");
            Registration.registerExpression(com.pie.tlatoani.Tablist.Array.ExprLatencyOfTab.class, Number.class, ExpressionType.PROPERTY, "(latency|ping) of tab %number%, %number% (in %-tablist%|for %-player%)");
            Registration.registerExpression(com.pie.tlatoani.Tablist.Array.ExprIconOfTab.class, Skin.class, ExpressionType.PROPERTY, "(head|icon|skull) of tab %number%, %number% (in %-tablist%|for %-player%)", "initial icon of (%-tablist%|%player%'s [array] tablist)");
            Registration.registerExpression(com.pie.tlatoani.Tablist.Array.ExprScoreOfTab.class, Number.class, ExpressionType.PROPERTY, "score of tab %number%, %number% (in %-tablist%|for %-player%)");
            Registration.registerExpression(com.pie.tlatoani.Tablist.Array.ExprSizeOfTabList.class, Number.class, ExpressionType.PROPERTY, "amount of (0¦column|1¦row)s in (%-tablist%|%-player%'s [array] tablist)");
        }
    }
}
