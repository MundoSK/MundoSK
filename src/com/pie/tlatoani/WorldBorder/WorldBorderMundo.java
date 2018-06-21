package com.pie.tlatoani.WorldBorder;

import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.util.Timespan;
import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.Core.Static.Logging;
import com.pie.tlatoani.Core.Static.Reflection;
import com.pie.tlatoani.Core.Registration.Registration;
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
    public final static String DIAMETER_SYNTAX = "(0¦diameter|0¦side length|8¦size)";
    
    public static void load() {
        Registration.registerEffect(EffResetBorder.class, "reset %world%")
                .document("Reset World Border", "Before 1.2", "Resets the border properties of the specified world to their default values. "
                    + "These values can be found at https://minecraft.gamepedia.com/World_border#Commands");
        Registration.registerEffect(EffChangeBorderDiameter.class,
                "set " + DIAMETER_SYNTAX + " of %world% to %number% over %timespan%",
                "set %world%'s " + DIAMETER_SYNTAX + " to %number% over %timespan%",
                "set " + DIAMETER_SYNTAX + " of %world% over %timespan% to %number%",
                "set %world%'s " + DIAMETER_SYNTAX + " over %timespan% to %number%",
                "add %number% to " + DIAMETER_SYNTAX + " of %world% over %timespan%",
                "add %number% to %world%'s " + DIAMETER_SYNTAX + " over %timespan%",
                "(remove|subtract) %number% from " + DIAMETER_SYNTAX + " of %world% over %timespan%",
                "(remove|subtract) %number% from %world%'s " + DIAMETER_SYNTAX + " over %timespan%")
                .document("Change Border Diameter", "1.8", "Changes the diameter, or size, of the specified world's border over the specified timespan.");

        Registration.registerExpression(ExprPropertyOfBorder.class, Number.class, ExpressionType.PROPERTY,
                "(0¦" + DIAMETER_SYNTAX + "|1¦damage amount|2¦damage buffer|3¦warning distance|4¦warning time) of %world%",
                "%world%'s (0¦" + DIAMETER_SYNTAX + "|1¦damage amount|2¦damage buffer|3¦warning distance|4¦warning time)")
                .document("Property of Border", "1.8", "An expression for a certain property of the specified world's border:"
                        , "diameter: The diameter/size/side length (Note: you should use 'diameter' instead of 'size' to refer to this property, as 'size' conflicts with Skript's size expression)"
                        , "damage amount: The amount of damage a player will take per second if they are outside both the border and the damage buffer"
                        , "damage buffer: The distance (in blocks) outside of the border a player can be before taking damage"
                        , "warning distance: The distance a player has to be within the border to see the red warning effect"
                        + "\nwarning time: The amount of time the border should be within of reaching a player to show that player the red warning effect.");
        Registration.registerPropertyExpression(ExprCenterOfBorder.class, Location.class, "world", "center")
                .document("Center of Border", "Before 1.2", "The center of the specified world's border. This isn't necessarily the same as the spawn.");
        Registration.registerExpressionCondition(CondBeyondBorder.class, ExpressionType.PROPERTY,"%locations% (is|are) (0¦within|1¦beyond) border")
                .document("Is Beyond Border", "1.4.9", "Checks whether a location/entity is beyond or within the border in its world.");

        loadBorderEvent();
    }

    private static void loadBorderEvent() {
        Bukkit.getWorlds().forEach(WorldBorderMundo::replaceBorderForWorld);
        Bukkit.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onWorldLoad(WorldLoadEvent event) {
                replaceBorderForWorld(event.getWorld());
            }
        }, Mundo.get());

        Registration.registerEvent("Border Stabilize", EvtBorderStabilize.class, BorderStabilizeEvent.class, "border stabilize [in %-worlds%]")
                .document("Border Stabilize", "1.4.6", "Called when a border (optionally only of the specified world(s)) stops moving.");
        Registration.registerExpression(ExprBorderMovingValue.class, Number.class, ExpressionType.PROPERTY,
                "(0¦original " + DIAMETER_SYNTAX + "|1¦(eventual|final) " + DIAMETER_SYNTAX + "|2¦remaining distance until [the] border stabilize[s]) of %world%",
                "%world%'s (0¦original " + DIAMETER_SYNTAX + "|1¦(eventual|final) " + DIAMETER_SYNTAX + "|2¦remaining distance until [the] border stabilize[s])")
                .document("Moving Border Diameter", "1.8", "An expression for a certain property of the moving border of the specified world:"
                        + "original diameter: The diameter of the border when it was last stable"
                        + "final diameter: The diameter that the border will be when it stabilizes"
                        + "remaining distance: The distance the border still has to go before it stabilizes");
        Registration.registerExpression(ExprTimeRemainingUntilBorderStabilize.class, Timespan.class, ExpressionType.PROPERTY,
                "(time remaining|remaining time) until [the] border stabilize[s] (of|in) %world%",
                "%world%'s (time remaining|remaining time) until [the] border stabilize[s]")
                .document("Time Remaining Until Border Stabilize", "1.4.6", "An expression for the timespan remaining until the border of the specified world stops moving.");
        Registration.registerExpressionCondition(CondBorderMoving.class, ExpressionType.PROPERTY, "border of %world% is (0¦moving|1¦stable)", "%world%'s border is (0¦moving|1¦stable)")
                .document("Border is Moving", "1.8", "Checks whether the border of the specified world is moving or stable (not moving).");
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
