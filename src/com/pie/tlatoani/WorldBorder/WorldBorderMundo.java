package com.pie.tlatoani.WorldBorder;

import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.util.Timespan;
import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.Util.Logging;
import com.pie.tlatoani.Util.Reflection;
import com.pie.tlatoani.Util.Registration;
import com.pie.tlatoani.WorldBorder.BorderEvent.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

import java.util.function.Function;

/**
 * Created by Tlatoani on 8/8/17.
 */
public class WorldBorderMundo {
    public static final String DIAMETER_SYNTAX = "(diameter|size|length)";
    
    public static void load() {
        Registration.registerEffect(EffResetBorder.class, "reset %world%");
        Registration.registerEffect(EffChangeBorderSize.class,
                "set (diameter|size|length) of %world% to %number% over %timespan%",
                "set %world%'s (diameter|size|length) to %number% over %timespan%",
                "set (diameter|size|length) of %world% over %timespan% to %number%",
                "set %world%'s (diameter|size|length) over %timespan% to %number%",
                "add %number% to (diameter|size|length) of %world% over %timespan%",
                "add %number% to %world%'s (diameter|size|length) over %timespan%",
                "(remove|subtract) %number% from (diameter|size|length) of %world% over %timespan%",
                "(remove|subtract) %number% from %world%'s (diameter|size|length) over %timespan%");

        Registration.registerExpression(ExprPropertyOfBorder.class, Number.class, ExpressionType.PROPERTY,
                "(0¦" + DIAMETER_SYNTAX + "|0¦length|1¦damage amount|2¦damage buffer|3¦warning distance|4¦warning time) of %world%",
                "%world%'s (0¦" + DIAMETER_SYNTAX + "|0¦length|1¦damage amount|2¦damage buffer|3¦warning distance|4¦warning time)");
        Registration.registerExpression(ExprCenterOfBorder.class,Location.class,ExpressionType.PROPERTY,"center of %world%", "%world%'s center");
        Registration.registerExpression(CondBeyondBorder.class,Boolean.class,ExpressionType.PROPERTY,"%locations% (is|are) (0¦within|1¦beyond) border");

        loadBorderEvent();
    }

    private static void loadBorderEvent() {
        Bukkit.getWorlds().forEach(WorldBorderMundo::replaceBorderForWorld);
        Bukkit.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onWorldLoad(WorldLoadEvent event) {
                replaceBorderForWorld(event.getWorld());
            }
        }, Mundo.INSTANCE);

        Registration.registerEvent("Border Stabilize", EvtBorderStabilize.class, BorderStabilizeEvent.class, "border stabilize [in %-worlds%]");
        Registration.registerExpression(ExprBorderMovingValue.class, Number.class, ExpressionType.PROPERTY,
                "(0¦original " + DIAMETER_SYNTAX + "|1¦(eventual|final) " + DIAMETER_SYNTAX + "|2¦remaining distance until border stabilize) of %world%",
                "%world%'s (0¦original " + DIAMETER_SYNTAX + "|1¦(eventual|final) " + DIAMETER_SYNTAX + "|2¦remaining distance until border stabilize)");
        Registration.registerExpression(CondBorderMoving.class, Boolean.class, ExpressionType.PROPERTY, "border of %world% is (0¦moving|1¦stable)", "%world%'s border is (0¦moving|1¦stable)");
        Registration.registerExpression(ExprTimeRemainingUntilBorderStabilize.class, Timespan.class, ExpressionType.PROPERTY, "(time remaining|remaining time) until border stabilize (of|in) %world%");
    }

    private static Function<World, WorldBorder> BORDER_REPLACER = null;
    private static Reflection.FieldAccessor<WorldBorder> CRAFT_WORLD_WORLD_BORDER = null;

    private static WorldBorder getBorderReplacement(World world) {
        if (BORDER_REPLACER == null) {
            if (Reflection.methodExists(WorldBorder.class, "isInside", Location.class)) {
                BORDER_REPLACER = WorldBorderImplExt::new;
            } else {
                BORDER_REPLACER = WorldBorderImpl::new;
            }
        }
        return BORDER_REPLACER.apply(world);
    }

    private static void setWorldBorderField(World world, WorldBorder value) {
        if (CRAFT_WORLD_WORLD_BORDER == null) {
            CRAFT_WORLD_WORLD_BORDER = Reflection.getField(Reflection.getCraftBukkitClass("CraftWorld"), "worldBorder", WorldBorder.class);
        }
        CRAFT_WORLD_WORLD_BORDER.set(world, value);
    }

    public static void replaceBorderForWorld(World world) {
        if (world.getWorldBorder() instanceof WorldBorderImpl) {
            return;
        }
        setWorldBorderField(world, getBorderReplacement(world));
        Logging.debug(WorldBorderMundo.class, "Replaced the border for world: " + world);
    }


}
