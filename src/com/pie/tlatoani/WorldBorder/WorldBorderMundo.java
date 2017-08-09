package com.pie.tlatoani.WorldBorder;

import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.util.Timespan;
import com.pie.tlatoani.Mundo;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Created by Tlatoani on 8/8/17.
 */
public class WorldBorderMundo {
    
    public static void load() {
        Mundo.registerEffect(EffResetBorder.class, "reset %world%");
        Mundo.registerEvent("Border Stabilize", EvtBorderStabilize.class, UtilBorderStabilizeEvent.class, "border stabilize [in %-world%]");
        Mundo.registerEventValue(UtilBorderStabilizeEvent.class, World.class, UtilBorderStabilizeEvent::getWorld);
        Mundo.registerExpression(ExprSizeOfBorder.class,Double.class, ExpressionType.PROPERTY,"(size|diameter) of %world% [over %-timespan%]");
        Mundo.registerExpression(ExprCenterOfBorder.class,Location.class,ExpressionType.PROPERTY,"center of %world%");
        Mundo.registerExpression(ExprDamageAmountOfBorder.class,Double.class,ExpressionType.PROPERTY,"damage amount of %world%");
        Mundo.registerExpression(ExprDamageBufferOfBorder.class,Double.class,ExpressionType.PROPERTY,"damage buffer of %world%");
        Mundo.registerExpression(ExprWarningDistanceOfBorder.class,Integer.class,ExpressionType.PROPERTY,"warning distance of %world%");
        Mundo.registerExpression(ExprWarningTimeOfBorder.class,Integer.class,ExpressionType.PROPERTY,"warning time of %world%");
        Mundo.registerExpression(ExprFinalSizeOfBorder.class,Double.class,ExpressionType.PROPERTY,"final size of %world%");
        Mundo.registerExpression(ExprTimeRemainingUntilBorderStabilize.class,Timespan.class,ExpressionType.PROPERTY,"time remaining until border stabilize in %world%");
        Mundo.registerExpression(CondBeyondBorder.class,Boolean.class,ExpressionType.PROPERTY,"%locations% (is|are) (0¦within|1¦beyond) border");
    }
}
