package com.pie.tlatoani.WorldManagement;

import ch.njol.skript.classes.Converter;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.registrations.Converters;
import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.WorldManagement.WorldLoader.*;
import org.bukkit.World;
import org.bukkit.WorldCreator;

/**
 * Created by Tlatoani on 8/8/17.
 */
public class WorldManagementMundo {
    
    public static void load() {
        Converters.registerConverter(World.class, WorldCreator.class, new Converter<World, WorldCreator>() {
            @Override
            public WorldCreator convert(World world) {
                WorldCreator worldCreator = new WorldCreator(world.getName());
                worldCreator.copy(world);
                worldCreator.type(world.getWorldType());
                worldCreator.generateStructures(world.canGenerateStructures());
                worldCreator.generatorSettings("");
                return worldCreator;
            }
        });
        Mundo.registerEffect(EffCreateWorld.class, "create [new] world named %string%[( with|,)][ (environment|env[ironment]) %-environment%][,] [seed %-string%][,] [type %-worldtype%][,] [gen[erator] %-string%][,] [gen[erator] settings %-string%][,] [struct[ures] %-boolean%]");
        Mundo.registerEffect(EffCreateWorldCreator.class, "create world using %creator%");
        Mundo.registerEffect(EffUnloadWorld.class, "unload %world% [save %-boolean%]");
        Mundo.registerEffect(EffDeleteWorld.class, "delete %world%");
        Mundo.registerEffect(EffDuplicateWorld.class, "duplicate %world% using name %string%");
        Mundo.registerExpression(ExprCurrentWorlds.class,World.class, ExpressionType.SIMPLE,"[all] current worlds");

        loadWorldLoader();
        loadWorldLoaderDeprecated();
    }
    
    private static void loadWorldLoader() {
        Mundo.registerEffect(EffLoadWorldAutomatically.class, "[(1¦don't|1¦do not)] load %world% automatically");
        Mundo.registerExpression(ExprAllAutomaticCreators.class, WorldCreator.class, ExpressionType.SIMPLE, "[all] automatic creators");
        Mundo.registerExpression(ExprAutomaticCreator.class, WorldCreator.class, ExpressionType.SIMPLE, "automatic creator [for world [named]] %string%");
    }

    private static void loadWorldLoaderDeprecated() {
        Mundo.registerEffect(EffRunCreatorOnStart.class, "run %creator% on start"); //Will be removed in a future version
        Mundo.registerEffect(EffDoNotLoadWorldOnStart.class, "don't load world %string% on start"); //Will be removed in a future version
    }
}
