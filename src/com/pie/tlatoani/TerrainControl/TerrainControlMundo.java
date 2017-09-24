package com.pie.tlatoani.TerrainControl;

import ch.njol.skript.lang.ExpressionType;
import com.pie.tlatoani.Util.Logging;
import com.pie.tlatoani.Registration.Registration;

/**
 * Created by Tlatoani on 8/8/17.
 */
public class TerrainControlMundo {
    
    public static void load() {
        Logging.info("You uncovered the secret TerrainControl syntaxes!");
        Registration.registerEffect(EffSpawnObject.class, "(tc|terrain control) spawn %string% at %location% with rotation %string%");
        Registration.registerExpression(ExprBiomeAt.class,String.class, ExpressionType.PROPERTY,"(tc|terrain control) biome at %location%");
        Registration.registerExpression(ExprTCEnabled.class,Boolean.class,ExpressionType.PROPERTY,"(tc|terrain control) is enabled for %world%");
    }
}
