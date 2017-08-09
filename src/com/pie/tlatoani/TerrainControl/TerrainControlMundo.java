package com.pie.tlatoani.TerrainControl;

import ch.njol.skript.lang.ExpressionType;
import com.pie.tlatoani.Mundo;

/**
 * Created by Tlatoani on 8/8/17.
 */
public class TerrainControlMundo {
    
    public static void load() {
        Mundo.info("You uncovered the secret TerrainControl syntaxes!");
        Mundo.registerEffect(EffSpawnObject.class, "(tc|terrain control) spawn %string% at %location% with rotation %string%");
        Mundo.registerExpression(ExprBiomeAt.class,String.class, ExpressionType.PROPERTY,"(tc|terrain control) biome at %location%");
        Mundo.registerExpression(ExprTCEnabled.class,Boolean.class,ExpressionType.PROPERTY,"(tc|terrain control) is enabled for %world%");
    }
}
