package com.pie.tlatoani.Tablist;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.ExpressionType;
import com.pie.tlatoani.Core.Registration.Registration;
import com.pie.tlatoani.Skin.Skin;
import com.pie.tlatoani.Tablist.Array.*;
import com.pie.tlatoani.Tablist.General.*;
import com.pie.tlatoani.Tablist.Group.*;
import com.pie.tlatoani.Tablist.Player.*;
import com.pie.tlatoani.Tablist.Simple.*;
import org.bukkit.entity.Player;

/**
 * Created by Tlatoani on 3/30/18.
 */
public class TablistMundo {

    public static final String TABLIST_OWNER = "(%-players%|group %-string%)";
    public static final String FOR_TABLIST_OWNER = "[for " + TABLIST_OWNER + "]";
    public static final String OF_TABLIST_OWNER = "[of " + TABLIST_OWNER + "]";
    public static final String FOR_OF_TABLIST_OWNER = "[(for|of) " + TABLIST_OWNER + "]";
    public static final String TABLIST_OWNER_POSSESSIVE = "[" + TABLIST_OWNER + "'[s]]";
    
    public static final String SIMPLE_TAB = "([simple] tab [with] id|simple tab) %string%";

    /**
     * Registers tablist syntaxes and calls {@link TablistManager#load()}
     */
    public static void load() {
        Registration.registerEffect(EffEnableDisableScores.class,
                "(0¦enable|1¦disable) scores in [the] tablist[s] " + OF_TABLIST_OWNER,
                "(0¦enable|1¦disable) scores in " + TABLIST_OWNER_POSSESSIVE + " tablist[s]");
        Registration.registerExpression(ExprDefaultIcon.class, Skin.class, ExpressionType.PROPERTY,
                "[the] (initial|default) (head|icon|skull) (in|of) [the] tablist[s] " + OF_TABLIST_OWNER,
                "[the] (intial|default) (head|icon|skull) (in|of) " + TABLIST_OWNER_POSSESSIVE + " [array] tablist[s]");
        Registration.registerExpression(ExprHeaderFooter.class, String.class, ExpressionType.PROPERTY,
                "[the] tablist (0¦header|1¦footer)[s] " + FOR_OF_TABLIST_OWNER,
                TABLIST_OWNER_POSSESSIVE + " tablist (0¦header|1¦footer)[s]")
                .document("Tablist Header or Footer", "1.8",
                        "An expression for the header or footer of the tablist(s) of the specified player(s). "
                        + "This is a list expression as the header and footer can have multiple lines of text.");
        Registration.registerExpression(ExprLineOfHeaderFooter.class, String.class, ExpressionType.COMBINED,
                "(line %-number%|[the] last line) of [the] tablist (0¦header|1¦footer)[s] " + FOR_OF_TABLIST_OWNER,
                "(line %-number%|[the] last line) of " + TABLIST_OWNER_POSSESSIVE + " tablist (0¦header|1¦footer)[s]");
        Registration.registerExpression(ExprHeightOfHeaderFooter.class, Number.class, ExpressionType.PROPERTY,
                "[the] height of [the] tablist (0¦header|1¦footer)[s] " + FOR_OF_TABLIST_OWNER,
                "[the] height of " + TABLIST_OWNER_POSSESSIVE + " tablist (0¦header|1¦footer)[s]");
        Registration.registerExpressionCondition(CondScoresEnabled.class, ExpressionType.PROPERTY,
                "scores [are] (0¦enabled|1¦disabled) in [the] tablist[s] " + OF_TABLIST_OWNER,
                "scores [are] (0¦enabled|1¦disabled) in " + TABLIST_OWNER_POSSESSIVE + " tablist[s]")
                .document("Scores are Enabled", "1.8",
                        "Checks whether the tablist(s) of the specified player(s) have scores enabled. "
                        + "This only applies to enabling scores using MundoSK's tablist syntaxes.");
        loadGroup();
        loadPlayer();
        loadSimple();
        loadArray();
        TablistManager.load();
    }

    private static void loadGroup() {
        Registration.registerEffect(EffAddToTablistGroup.class, "add %players% to tablist group %string%");
        Registration.registerEffect(EffRemoveFromTablistGroup.class, "remove %players% from tablist group %string%");
        Registration.registerEffect(EffEmptyGroup.class, "empty tablist group %string%");
        Registration.registerEffect(EffDeleteGroup.class, "delete tablist group %string%");
        Registration.registerExpression(ExprTablistGroup.class, Player.class, ExpressionType.PROPERTY,
                "[the] members of tablist group %string%", "tablist group %string%'s members");
    }

    /**
     * Registers the tablist syntaxes related to {@link PlayerTablist}
     */
    private static void loadPlayer() {
        Registration.registerEffect(EffShowHidePlayerTab.class,
                "(0¦show|1¦hide) [the] [player] tab[s] of %players% " + FOR_TABLIST_OWNER,
                "(0¦show|1¦hide) %players%'[s] [player] tab[s] " + FOR_TABLIST_OWNER,
                "(0¦show|1¦hide) %players% in " + TABLIST_OWNER_POSSESSIVE + " tablist[s]",
                "(0¦show|1¦hide) %players% in [the] tablist[s] " + OF_TABLIST_OWNER,
                "(0¦show|1¦hide) %players% " + FOR_TABLIST_OWNER + " in [the] tablist[s]")
                .document("Show or Hide in Tablist", "1.8",
                        "Shows or hides certain players for other certain players in their tablist(s). "
                        + "Note: if the Players Are Visible condition/expression is set to false for a specific player "
                        + "and you show a player for them using this effect, "
                        + "then the condition/expression will become true but only that player will become unhidden.");
        Registration.registerEffect(EffClearPlayerModifications.class,
                "(clear|reset) [all] player tab modifications " + FOR_TABLIST_OWNER)
                .document("Clear Player Tab Modifications", "1.8",
                        "Resets all of the tabs representing players for the specified player(s) to normal. "
                        + "This will make all players visible in the tablist and reset any display name, latency, and score changes.");
        Registration.registerExpression(ExprDisplayNameOfPlayerTab.class, String.class, ExpressionType.PROPERTY,
                "[the] [display] name of [the] [player] tab of %player% " + FOR_TABLIST_OWNER,
                "[the] [display] name of %player%'s [player] tab " + FOR_TABLIST_OWNER,
                "[the] tablist name of %player% " + FOR_TABLIST_OWNER,
                "%player%'s tablist name " + FOR_TABLIST_OWNER)
                .document("Display Name of Player Tab", "1.8",
                        "An expression for the display name of the specified player tab "
                        + "in the tablist of the specified player(s). "
                        + "This will not be set if the player tab's display name has not been changed (or was reset), "
                        + "or the player tab is hidden.");
        Registration.registerExpression(ExprLatencyBarsOfPlayerTab.class, Number.class, ExpressionType.PROPERTY,
                "[the] (latency|ping) [bars] of [the] [player] tab of %player% " + FOR_TABLIST_OWNER,
                "(latency|ping) [bars] of %player%'s [player] tab " + FOR_TABLIST_OWNER,
                "[the] tablist (latency|ping) [bars] of %player% " + FOR_TABLIST_OWNER,
                "%player%'s tablist (latency|ping) [bars] " + FOR_TABLIST_OWNER)
                .document("Latency Bars of Player Tab", "1.8.3",
                        "An expression for the amount of latency bars of the specified player tab "
                        + "in the tablist of the specified player(s). "
                        + "When set, this is always between 0 and 5. This will not be set if the player tab is hidden.")
                .changer(Changer.ChangeMode.RESET, "1.8.3",
                        "Resets any modification of the latency bars to match the player's actual latency. "
                        + "Initially, the latency bars will appear to be 5, but will change within 30 seconds "
                        + "if the player's actual latency requires a different amount of bars.");
        Registration.registerExpression(ExprScoreOfPlayerTab.class, Number.class, ExpressionType.PROPERTY,
                "[the] score of [the] [player] tab of %player% " + FOR_TABLIST_OWNER,
                "[the] score of %player%'s [player] tab " + FOR_TABLIST_OWNER,
                "[the] tablist score of %player% " + FOR_TABLIST_OWNER,
                "%player%'s tablist score " + FOR_TABLIST_OWNER)
                .document("Score of Player Tab", "1.8",
                        "An expression for the score of the specified player tab "
                        + "in the tablist of the specified player(s). "
                        + "This will not be set if the player tab is hidden.");
        Registration.registerExpressionCondition(CondPlayerTabIsVisible.class, ExpressionType.COMBINED,
                "[the] [player] tab of %player% is (0¦visible|1¦hidden) " + FOR_TABLIST_OWNER,
                "%player%'s [player] tab is (0¦visible|1¦hidden) " + FOR_TABLIST_OWNER,
                "%player% is (0¦visible|1¦hidden) in " + TABLIST_OWNER_POSSESSIVE + " tablist[s]",
                "%player% is (0¦visible|1¦hidden) in [the] tablist[s] " + FOR_OF_TABLIST_OWNER)
                .document("Player Tab is Visible", "1.8",
                        "Checks whether the first player's tab is visible for the second specified player(s).");
        Registration.registerExpressionCondition(CondPlayerTabsAreVisible.class, ExpressionType.PROPERTY,
                "player tabs (0¦are|1¦aren't|1¦are not) visible " + FOR_TABLIST_OWNER,
                TABLIST_OWNER_POSSESSIVE + " tablist[s] (contains|(0¦does|0¦do|1¦doesn't|1¦does not|1¦don't|1¦do not) contain) players",
                "[the] tablist[s] " + OF_TABLIST_OWNER + " (contains|(0¦does|0¦do|1¦doesn't|1¦does not|1¦don't|1¦do not) contain) players",
                "players are (0¦visible|1¦hidden) in [the] tablist[s] " + FOR_OF_TABLIST_OWNER,
                "players are (0¦visible|1¦hidden) in " + TABLIST_OWNER_POSSESSIVE + " tablist[s]")
                .document("Player Tabs Are Visible", "1.8",
                        "Checks whether the tablist(s) of the specified player(s) allow player tabs to be visible. "
                        + "Setting this to false prevents any player tabs from being seen in the tablist for the specified player(s) "
                        + "(players who join will be automatically hidden). "
                        + "Setting this to true immediately makes all player tabs visible in the tablist for the specified player(s). "
                        + "Use the Show or Hide in Tablist effect if you would like to set this condition to be true "
                        + "without immediately showing all players. "
                        + "Note that it is possible for this condition/expression to be true yet no player tabs are visible "
                        + "if they are hidden manually using the Show or Hide in Tablist effect. "
                        + "In this case, players who join will still be visible in the tablist unless manually hidden using the effect.");
    }

    /**
     * Registers the tablist syntaxes related to {@link SimpleTablist}
     */
    private static void loadSimple() {
        Registration.registerExpression(ExprPriorityOfSimpleTab.class, String.class, ExpressionType.PROPERTY,
                "[the] priority of [the] " + SIMPLE_TAB + " " + FOR_TABLIST_OWNER);

        Registration.registerEffect(EffCreateSimpleTab.class,
                "create [a] " + SIMPLE_TAB + " " + FOR_TABLIST_OWNER + " [with [priority %-string%] "
                        + "[[display] name %-string%] [(ping|latency) [bars] %-number%] [(head|icon|skull) %-skin%] [score %-number%]]")
                .document("Create Simple Tab", "1.8",
                        "Creates a simple tab for the specified player(s) with the specified id and properties. "
                        + "If a specified player already has a simple tab with the specified id in their tablist, "
                        + "that tab will be deleted before creating the new tab. "
                        + "This effect will not work for a specified player if they have the array tablist enabled.");
        Registration.registerEffect(EffDeleteSimpleTab.class, 
                "delete [the] " + SIMPLE_TAB + " " + FOR_TABLIST_OWNER)
                .document("Delete Simple Tab", "1.8",
                        "Removes the simple tab "
                        + "with the specified id from the tablist(s) of the specified player(s).");
        Registration.registerEffect(EffDeleteAllSimpleTabs.class, 
                "delete all (id|simple) tabs " + FOR_TABLIST_OWNER)
                .document("Delete All Simple Tabs", "1.8",
                        "Removes all simple tabs from the tablist(s) of the specified players(s).");
        Registration.registerExpression(ExprDisplayNameOfSimpleTab.class, String.class, ExpressionType.PROPERTY, 
                "[the] [display] name of [the] " + SIMPLE_TAB + " " + FOR_TABLIST_OWNER)
                .document("Display Name of Simple Tab", "1.8",
                        "An expression for the display name of the simple tab "
                        + "with the specified id in the tablist(s) of the specified player(s).");
        Registration.registerExpression(ExprLatencyBarsOfSimpleTab.class, Number.class, ExpressionType.PROPERTY, 
                "[the] (latency|ping) [bars] of [the] " + SIMPLE_TAB + " " + FOR_TABLIST_OWNER)
                .document("Latency Bars of Simple Tab", "1.8",
                        "An expression for the amount of latency bars of the simple tab "
                        + "with the specified id in the tablist(s) of the specified player(s). "
                        + "This is always between 0 and 5.");
        Registration.registerExpression(ExprIconOfSimpleTab.class, Skin.class, ExpressionType.PROPERTY, 
                "[the] (head|icon|skull) of [the] " + SIMPLE_TAB + " " + FOR_TABLIST_OWNER)
                .document("Icon of Simple Tab", "1.8",
                        "An expression for the icon of the simple tab "
                        + "with the specified id in the tablist(s) of the specified player(s).");
        Registration.registerExpression(ExprScoreOfSimpleTab.class, Number.class, ExpressionType.PROPERTY, 
                "[the] score of [the] " + SIMPLE_TAB + " " + FOR_TABLIST_OWNER)
                .document("Score of Simple Tab", "1.8",
                        "An expression for the score of the simple tab "
                        + "with the specified id in the tablist(s) of the specified player(s).");
    }

    /**
     * Registers the tablist syntaxes related to {@link ArrayTablist}
     */
    private static void loadArray() {
        Registration.registerEffect(EffEnableDisableArrayTablist.class,
                "(enable|activate) [the] array tablist " + FOR_TABLIST_OWNER
                        + " [with [%-number% columns] [%-number% rows] [(default|initial) (head|icon|skull) %-skin%]]",
                "(disable|deactivate) [the] array tablist " + FOR_TABLIST_OWNER)
                .document("Enable or Disable Array Tablist", "1.8",
                        "Enables or disables the array tablist for the specified player(s). "
                        + "The array tablist creates a grid of tabs that allows you to use grid coordinates to easily modify individual tabs. "
                        + "When enabling, you can specify the amount of columns (defaults to 4), the amount of rows (defaults to 20), "
                        + "and the initial icon (defaults to a white texture). "
                        + "See the Size of Array Tablist and Icon of Array Tab expressions for more info. "
                        + "If the array tablist is already enabled, it will be replaced with a blank one matching the new specifications. "
                        + "Note that when enabling the array tablist, all simple tabs will be removed and all player tabs will be hidden. "
                        + "When disabling the array tablist, all player tabs will be visible, "
                        + "and there will be no simple tabs.")
                .example("command /example_tablist:"
                        , "\ttrigger:"
                        , "\t\tenable array tablist for player "
                        + "#This creates a 4 x 20 grid of tabs in the player's tablist, there is further syntax for other amounts of rows/columns"
                        , "\t\tset display name of tab 1, 1 for player to \"Hello!\" #This sets the first tab as \"Hello!\""
                        , "\t\tset icon of tab 4, 20 for player to alex #This sets the icon of the last tab as the alex skin"
                        , "\t\tset display name of tab 4, 20 for player to \"ALEX\" #This sets the last tab as \"ALEX!\""
                        , "\t\tloop 20 times:"
                        , "\t\t\tset display name of tab 2, loop-number for player to \"Column 2, Row %loop-number%\" "
                        + "#This sets all of the tabs in the second column to display their column and row"
                        , "\t\t\tset icon of tab 2, loop-number for player to steve "
                        + "#This sets all of the tabs in the second column to have a steve skin as their icon");
        Registration.registerEffect(EffAddRemoveArrayTabs.class,
                "add ([a] (0¦column|1¦row)|%-number% (0¦column|1¦row)s) [with icon %-skin%] to [the] [array] tablist[s] " + OF_TABLIST_OWNER,
                "add ([a] (0¦column|1¦row)|%-number% (0¦column|1¦row)s) [with icon %-skin%] to " + TABLIST_OWNER_POSSESSIVE + " [array] tablist[s]",
                "remove ([a] (0¦column|1¦row)|%-number% (0¦column|1¦row)s) from [the] [array] tablist[s] " + OF_TABLIST_OWNER,
                "remove ([a] (0¦column|1¦row)|%-number% (0¦column|1¦row)s) from " + TABLIST_OWNER_POSSESSIVE + " [array] tablist[s]");
        Registration.registerExpression(ExprDisplayNameOfArrayTab.class, String.class, ExpressionType.PROPERTY, 
                "[the] [display] name of [the] [array] tab %number%, %number% " + FOR_TABLIST_OWNER)
                .document("Display Name of Array Tab", "1.8",
                        "An expression for the display name of the specified array tab for the specified player(s).");
        Registration.registerExpression(ExprLatencyBarsOfArrayTab.class, Number.class, ExpressionType.PROPERTY,
                "[the] (latency|ping) [bars] of [the] [array] tab %number%, %number% " + FOR_TABLIST_OWNER)
                .document("Latency Bars of Array Tab", "1.8",
                        "An expression for the amount of latency bars of the specified array tab for the specified player(s). "
                        + "This is always an integer between 0 and 5 (inclusive).");
        Registration.registerExpression(ExprIconOfArrayTab.class, Skin.class, ExpressionType.PROPERTY,
                "[the] (head|icon|skull) of [the] [array] tab %number%, %number% " + FOR_TABLIST_OWNER)
                .document("Icon of Array Tab", "1.8",
                        "An expression for either the icon of the specified array tab for the specified player(s), "
                        + "or the initial icon of the array tablist(s) of the specified player(s). "
                        + "The initial icon is the default icon that array tabs will have when enabling an array tablist or when increasing its size. "
                        + "By default this is a completely white texture.");
        Registration.registerExpression(ExprScoreOfArrayTab.class, Number.class, ExpressionType.PROPERTY,
                "[the] score of [the] [array] tab %number%, %number% " + FOR_TABLIST_OWNER)
                .document("Score of Array Tab", "1.8",
                        "An expression for the score of the specified array tab for the specified player(s).");
        Registration.registerExpression(ExprDimensionOfArrayTablist.class, Number.class, ExpressionType.PROPERTY,
                "[the] amount of (0¦column|1¦row)s in " + TABLIST_OWNER_POSSESSIVE + " [array] tablist[s]")
                .document("Size of Array Tablist", "1.8",
                        "An expression for the amount of rows or columns in the array tablist. "
                        + "There can be 1 to 4 columns."
                        + "For each amount of columns, there is a specified range for the amount of rows:"
                        , "1 Column: 1 to 20 rows"
                        , "2 Columns: 11 to 20 rows"
                        , "3 Columns: 14 to 20 rows"
                        , "4 Columns: 16 to 20 rows"
                        , "This is due to the fact that Minecraft allows 1 to 80 total tabs, and for each amount, "
                        + "there is only one way the tablist can appear. "
                        + "Minecraft only allows a maximum of 20 tabs in one column, "
                        + "so the tabs will try to fill as few columns as possible will adhering to this rule. "
                        + "For example, if there are 40 tabs, this is satisfied by a 2x20 tablist, but for 41 and 42 you need 3x14.");
        Registration.registerExpressionCondition(CondArrayTablistEnabled.class, ExpressionType.PROPERTY,
                "[the] array tablist is (0¦enabled|1¦disabled) " + FOR_TABLIST_OWNER)
                .document("Array Tablist is Enabled", "1.8.2",
                        "Checks whether the array tablist is enabled or disabled for the specified players. "
                        + "See the Enable or Disable Array Tablist effect for more info.");
    }
}
