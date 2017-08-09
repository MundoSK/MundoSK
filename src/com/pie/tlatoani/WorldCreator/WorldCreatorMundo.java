package com.pie.tlatoani.WorldCreator;

import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.ParseContext;
import ch.njol.util.Pair;
import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.WorldManagement.WorldLoader.UtilWorldLoader;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.json.simple.JSONObject;

/**
 * Created by Tlatoani on 8/8/17.
 */
public class WorldCreatorMundo {
    
    public static void load() {
        Mundo.registerType(WorldCreator.class, "creator").parser(new Mundo.SimpleParser<WorldCreator>() {
            @Override
            public WorldCreator parse(String s, ParseContext parseContext) {
                return null;
            }

            @Override
            public String toString(WorldCreator creator, int flags) {
                JSONObject jsonObject = UtilWorldLoader.getCreatorJSON(creator);
                jsonObject.put("worldname", creator.name());
                return jsonObject.toString();
            }

        });
        if (!Mundo.serverHasPlugin("RandomSK")) {
            Mundo.registerEnum(World.Environment.class, "environment", World.Environment.values(), new Pair<String, World.Environment>("END", World.Environment.THE_END));
        }
        Mundo.registerEnum(WorldType.class, "worldtype", WorldType.values(), new Pair<String, WorldType>("SUPERFLAT", WorldType.FLAT), new Pair<String, WorldType>("LARGE BIOMES", WorldType.LARGE_BIOMES), new Pair<String, WorldType>("VERSION 1.1", WorldType.VERSION_1_1));
        Mundo.registerExpression(ExprCreatorNamed.class,WorldCreator.class, ExpressionType.PROPERTY,"creator (with name|named) %string%");
        Mundo.registerExpression(ExprCreatorWith.class,WorldCreator.class,ExpressionType.PROPERTY,"%creator%[ modified],[ name %-string%][,][ (environment|env[ironment]) %-environment%][,][ seed %-string%][,][ type %-worldtype%][,][ gen[erator] %-string%][,][ gen[erator] settings %-string%][,][ struct[ures] %-boolean%]");
        Mundo.registerExpression(ExprCreatorOf.class,WorldCreator.class,ExpressionType.PROPERTY,"creator of %world%");
        Mundo.registerExpression(ExprNameOfCreator.class,String.class,ExpressionType.PROPERTY,"worldname of %creator%");
        Mundo.registerExpression(ExprEnvOfCreator.class,World.Environment.class,ExpressionType.PROPERTY,"env[ironment] of %creator%");
        Mundo.registerExpression(ExprSeedOfCreator.class,String.class,ExpressionType.PROPERTY,"seed of %creator%");
        Mundo.registerExpression(ExprGenOfCreator.class,String.class,ExpressionType.PROPERTY,"gen[erator] of %creator%");
        Mundo.registerExpression(ExprGenSettingsOfCreator.class,String.class,ExpressionType.PROPERTY,"gen[erator] setSafely[tings] of %creator%");
        Mundo.registerExpression(ExprTypeOfCreator.class,WorldType.class,ExpressionType.PROPERTY,"worldtype of %creator%");
        Mundo.registerExpression(ExprStructOfCreator.class,Boolean.class,ExpressionType.PROPERTY,"struct[ure(s| settings)] of %creator%");
    }
}
