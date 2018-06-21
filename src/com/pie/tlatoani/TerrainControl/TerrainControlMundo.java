package com.pie.tlatoani.TerrainControl;

import ch.njol.skript.lang.ExpressionType;
import com.pie.tlatoani.Core.Static.Logging;
import com.pie.tlatoani.Core.Registration.Registration;

/**
 * Created by Tlatoani on 8/8/17.
 */
public class TerrainControlMundo {
    
    public static void load() {
        Logging.info("You uncovered the secret TerrainControl syntaxes!");
        Registration.registerEffect(EffSpawnObject.class, "(tc|terrain control) spawn %string% at %location% with rotation %string%")
                .document("Spawn Custom Object", "1.4.4 or earlier", "Spawns the specified custom object at the specified location with the specified rotation, rotation can be \"north\", \"south\", \"east\", or \"west\".");
        Registration.registerExpression(ExprBiomeAt.class, String.class, ExpressionType.PROPERTY,"(tc|terrain control) biome at %location%")
                .document("Biome at Location", "1.4.4 or earlier", "An expression for the TerrainControl biome at the specified location.");
        Registration.registerExpressionCondition(CondTCEnabled.class, ExpressionType.PROPERTY,"(tc|terrain control) is enabled for %world%")
                .document("TerrainControl Is Enabled", "1.4.9", "Checks whether TerrainControl is enabled for the specified world.");
    }
}
