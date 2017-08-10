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
        Registration.registerEvent("Border Stabilize", EvtBorderStabilize.class, UtilBorderStabilizeEvent.class, "border stabilize [in %-world%]");
        Registration.registerEventValue(UtilBorderStabilizeEvent.class, World.class, UtilBorderStabilizeEvent::getWorld);
        Registration.registerExpression(ExprSizeOfBorder.class,Double.class, ExpressionType.PROPERTY,"(size|diameter) of %world% [over %-timespan%]");
        Registration.registerExpression(ExprCenterOfBorder.class,Location.class,ExpressionType.PROPERTY,"center of %world%");
        Registration.registerExpression(ExprDamageAmountOfBorder.class,Double.class,ExpressionType.PROPERTY,"damage amount of %world%");
        Registration.registerExpression(ExprDamageBufferOfBorder.class,Double.class,ExpressionType.PROPERTY,"damage buffer of %world%");
        Registration.registerExpression(ExprWarningDistanceOfBorder.class,Integer.class,ExpressionType.PROPERTY,"warning distance of %world%");
        Registration.registerExpression(ExprWarningTimeOfBorder.class,Integer.class,ExpressionType.PROPERTY,"warning time of %world%");
        Registration.registerExpression(ExprFinalSizeOfBorder.class,Double.class,ExpressionType.PROPERTY,"final size of %world%");
        Registration.registerExpression(ExprTimeRemainingUntilBorderStabilize.class,Timespan.class,ExpressionType.PROPERTY,"time remaining until border stabilize in %world%");
        Registration.registerExpression(CondBeyondBorder.class,Boolean.class,ExpressionType.PROPERTY,"%locations% (is|are) (0¦within|1¦beyond) border");
    }
}
