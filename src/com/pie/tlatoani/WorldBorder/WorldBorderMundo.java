package com.pie.tlatoani.WorldBorder;

import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.util.Timespan;
import com.pie.tlatoani.Util.Registration;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Created by Tlatoani on 8/8/17.
 */
public class WorldBorderMundo {
    
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

        Registration.registerEvent("Border Stabilize", EvtBorderStabilize.class, BorderStabilizeEvent.class, "border stabilize [in %-world%]");
        Registration.registerEventValue(BorderStabilizeEvent.class, World.class, BorderStabilizeEvent::getWorld);

        Registration.registerExpression(ExprPropertyOfBorder.class, Number.class, ExpressionType.PROPERTY,
                "(0¦diameter|0¦size|0¦length|1¦damage amount|2¦damage buffer|3¦warning distance|4¦warning time) of %world%",
                "%world%'s (0¦diameter|0¦size|0¦length|1¦damage amount|2¦damage buffer|3¦warning distance|4¦warning time)");
        Registration.registerExpression(ExprCenterOfBorder.class,Location.class,ExpressionType.PROPERTY,"center of %world%", "%world%'s center");
        Registration.registerExpression(ExprFinalSizeOfBorder.class,Double.class,ExpressionType.PROPERTY,"final size of %world%");
        Registration.registerExpression(ExprTimeRemainingUntilBorderStabilize.class,Timespan.class,ExpressionType.PROPERTY,"time remaining until border stabilize in %world%");
        Registration.registerExpression(CondBeyondBorder.class,Boolean.class,ExpressionType.PROPERTY,"%locations% (is|are) (0¦within|1¦beyond) border");
    }
}
