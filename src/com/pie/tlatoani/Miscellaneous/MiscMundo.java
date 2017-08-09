package com.pie.tlatoani.Miscellaneous;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.util.Slot;
import ch.njol.skript.util.Timespan;
import com.pie.tlatoani.Miscellaneous.ArmorStand.*;
import com.pie.tlatoani.Miscellaneous.Hanging.*;
import com.pie.tlatoani.Miscellaneous.Matcher.*;
import com.pie.tlatoani.Miscellaneous.MiscBukkit.*;
import com.pie.tlatoani.Miscellaneous.Random.ExprNewRandom;
import com.pie.tlatoani.Miscellaneous.Random.ExprRandomValue;
import com.pie.tlatoani.Miscellaneous.ServerListPing.*;
import com.pie.tlatoani.Miscellaneous.TabCompletion.*;
import com.pie.tlatoani.Miscellaneous.Thread.*;
import com.pie.tlatoani.Mundo;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.hanging.*;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

/**
 * Created by Tlatoani on 8/8/17.
 */
public class MiscMundo {
    
    public static void load() {
        Mundo.registerExpression(ExprReturnTypeOfFunction.class,ClassInfo.class,ExpressionType.PROPERTY,"return type of function %string%");
        Mundo.registerExpression(ExprLoadedScripts.class,String.class,ExpressionType.SIMPLE, "loaded script[ name]s");
        Mundo.registerExpression(ExprAllTypes.class, ClassInfo.class, ExpressionType.SIMPLE, "all types");
        Mundo.registerExpression(ExprThatAre.class, Object.class, ExpressionType.COMBINED, "%objects% that are %object%");
        Mundo.registerExpression(ExprNumber.class, Number.class, ExpressionType.PROPERTY, "%*number%[ ](0¦b|1¦d|2¦f|3¦s|4¦l)");
        Mundo.registerScope(ScopeWhen.class, "when %boolean%");
        Mundo.registerExpression(ExprLoopWhile.class,Object.class,ExpressionType.PROPERTY,"%objects% (0¦while|1¦until|2¦if|3¦unless) %boolean%");
        Mundo.registerExpression(ExprTreeOfListVariable.class, Object.class, ExpressionType.PROPERTY, "tree of %objects%");
        Mundo.registerExpression(ExprIndexesOfListVariable.class, String.class, ExpressionType.PROPERTY, "[all [of]] [the] indexes (of|in) [value] %objects%");
        Mundo.registerExpression(ExprBranch.class, String.class, ExpressionType.PROPERTY, "branch");

        loadArmorStand();
        loadHanging();
        loadMatcher();
        loadMiscBukkit();
        loadRandom();
        loadServerListPing();
        loadTabCompletion();
        loadThread();
    }
    
    private static void loadArmorStand() {
        Mundo.registerEvent("Armor Stand Interact Event", SimpleEvent.class, PlayerArmorStandManipulateEvent.class, "armor stand (manipulate|interact)");
        Mundo.registerEventValue(PlayerArmorStandManipulateEvent.class, ItemStack.class, PlayerArmorStandManipulateEvent::getArmorStandItem);
        Mundo.registerEventValue(PlayerArmorStandManipulateEvent.class, Slot.class, e ->
                new ArmorStandEquipmentSlot(e.getRightClicked(), ArmorStandEquipmentSlot.EquipSlot.getByEquipmentSlot(e.getSlot())));
        Mundo.registerEvent("Armor Stand Place Event", EvtArmorStandPlace.class, EntitySpawnEvent.class, "armor stand place");
    }

    private static void loadHanging() {
        Mundo.registerEvent("Hang Event", SimpleEvent.class, HangingPlaceEvent.class, "hang");
        Mundo.registerEventValue(HangingPlaceEvent.class, Block.class, HangingPlaceEvent::getBlock);
        Mundo.registerEvent("Unhang Event", EvtUnhang.class, HangingBreakEvent.class, "unhang [due to %-hangingremovecauses%]");
        Mundo.registerEventValue(HangingBreakByEntityEvent.class, Entity.class, HangingBreakByEntityEvent::getRemover);
        Mundo.registerEventValue(HangingBreakEvent.class, HangingBreakEvent.RemoveCause.class, HangingBreakEvent::getCause);
        Mundo.registerExpression(ExprHangedEntity.class,Entity.class, ExpressionType.SIMPLE,"hanged entity");
    }
    
    private static void loadMatcher() {
        Mundo.registerScope(ScopeMatcher.class, "(switch|match) %object%");
        Mundo.registerScope(ScopeMatches.class, "(case|matches) %object%");
    }
    
    private static void loadMiscBukkit() {
        Mundo.registerEnum(Difficulty.class, "difficulty", Difficulty.values());
        Mundo.registerEnum(PlayerLoginEvent.Result.class, "playerloginresult", PlayerLoginEvent.Result.values ());
        Mundo.registerEnum(HangingBreakEvent.RemoveCause.class, "hangingremovecause", HangingBreakEvent.RemoveCause.values());
        Mundo.registerEffect(EffWait.class, "[(2¦async)] wait (0¦until|1¦while) %boolean% [for %-timespan%]");
        if (Mundo.methodExists(Entity.class, "addPassenger", Entity.class)) {
            Mundo.registerEffect(EffMountVehicle.class, "mount %entities% on %entity%");
        }
        Mundo.registerExpression(ExprWorldString.class,World.class,ExpressionType.PROPERTY,"world %string%");
        Mundo.registerExpression(ExprHighestSolidBlock.class,Block.class,ExpressionType.PROPERTY,"highest [(solid|non-air)] block at %location%");
        Mundo.registerExpression(ExprDifficulty.class,Difficulty.class,ExpressionType.PROPERTY,"difficulty of %world%");
        Mundo.registerExpression(ExprGameRule.class,String.class,ExpressionType.PROPERTY,"value of [game]rule %string% in %world%");
        Mundo.registerExpression(ExprRemainingAir.class,Timespan.class,ExpressionType.PROPERTY,"breath of %livingentity%", "%livingentity%'s breath", "max breath of %livingentity%", "%livingentity%'s max breath");
        Mundo.registerExpression(ExprLoginResult.class, PlayerLoginEvent.Result.class, ExpressionType.SIMPLE, "(login|connect[ion]) result");
        Mundo.registerExpression(ExprServerIP.class, String.class, ExpressionType.SIMPLE, "[mundo[sk]] [the] ip of server", "[mundo[sk]] [the] server's ip");
        Mundo.registerExpression(ExprServerPort.class, Number.class, ExpressionType.SIMPLE, "[mundo[sk]] [the] port of server", "[mundo[sk]] [the] server's port");
        Mundo.registerExpression(ExprEntityCanCollide.class, Boolean.class, ExpressionType.PROPERTY, "%livingentity% is collidable");
        Mundo.registerExpression(ExprTreeAtLoc.class, Block.class, ExpressionType.PROPERTY, "tree at %location%");
        Mundo.registerExpression(ExprRespawnLocation.class, Location.class, ExpressionType.SIMPLE, "respawn location");
        Mundo.registerExpression(ExprDestination.class, Location.class, ExpressionType.SIMPLE, "destination");
        Mundo.registerExpression(ExprNewPortal.class, Location.class, ExpressionType.PROPERTY, "new nether portal within [[a] radius of] %number% (block|meter)s of %location%");
        Mundo.registerExpression(ExprFlying.class, Boolean.class, ExpressionType.PROPERTY, "[%player% is] flying");
    }
    
    private static void loadRandom() {
        Mundo.registerExpression(ExprNewRandom.class, Random.class, ExpressionType.PROPERTY, "new random [from seed %number%]");
        Mundo.registerExpression(ExprRandomValue.class, Object.class, ExpressionType.PROPERTY, "random (0¦int|1¦long|2¦float|3¦double|4¦gaussian|5¦int less than %-number%|6¦boolean) [from [random] %random%]");
    }
    
    private static void loadServerListPing() {
        Mundo.registerEvent("Server List Ping", SimpleEvent.class, ServerListPingEvent.class, "[[(server|player)] list] ping");
        Mundo.registerExpression(ExprAmountOfPlayers.class, Number.class, ExpressionType.SIMPLE, "(shown|sent) (0¦amount of|1¦max [amount of]) players");
        Mundo.registerExpression(ExprMotd.class, String.class, ExpressionType.SIMPLE, "(shown|sent) motd");
        Mundo.registerExpression(ExprIP.class, String.class, ExpressionType.SIMPLE, "pinger's ip");
    }
    
    private static void loadTabCompletion() {
        Mundo.registerEvent("Chat Tab Complete Event", SimpleEvent.class, PlayerChatTabCompleteEvent.class, "chat tab complete");
        Mundo.registerEventValue(PlayerChatTabCompleteEvent.class, String.class, PlayerChatTabCompleteEvent::getChatMessage);
        if (Mundo.classExists("org.bukkit.event.server.TabCompleteEvent")) {
            Mundo.registerEvent("Tab Complete Event", SimpleEvent.class, TabCompleteEvent.class, "tab complete");
            Mundo.registerEventValue(TabCompleteEvent.class, String.class, TabCompleteEvent::getBuffer);
            Mundo.registerExpression(ExprCompletions.class,String.class,ExpressionType.SIMPLE,"completions");
            Mundo.registerExpression(ExprLastToken.class, String.class, ExpressionType.SIMPLE, "last token");
        } else {
            Mundo.registerExpression(ExprCompletionsOld.class,String.class,ExpressionType.SIMPLE,"completions");
            Mundo.registerExpression(ExprLastTokenOld.class, String.class, ExpressionType.SIMPLE, "last token");
        }
    }
    
    private static void loadThread() {
        Mundo.registerEffect(EffWaitAsync.class, "async wait %timespan%");
        Mundo.registerScope(ScopeAsync.class, "async [in %-timespan%]");
        Mundo.registerScope(ScopeSync.class, "(sync|in %-timespan%)");
    }

}
