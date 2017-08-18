package com.pie.tlatoani.WorldManagement;

import ch.njol.skript.classes.Converter;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.registrations.Converters;
import com.pie.tlatoani.Util.Registration;
import com.pie.tlatoani.WorldCreator.WorldCreatorData;
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
        Registration.registerEffect(EffCreateWorld.class, "create [new] world named %string%[( with|,)] [(dim[ension]|env[ironment]) %-dimension%][,] [seed %-string%][,] [type %-worldtype%][,] [gen[erator] %-string%][,] [gen[erator] settings %-string%][,] [struct[ures] %-boolean%]");
        Registration.registerEffect(EffCreateWorldCreator.class, "create [new] world using %creator%");
        Registration.registerEffect(EffUnloadWorld.class, "unload %world% [save %-boolean%]");
        Registration.registerEffect(EffDeleteWorld.class, "delete %world%");
        Registration.registerEffect(EffDuplicateWorld.class, "duplicate %world% using name %string%");
        Registration.registerExpression(ExprCurrentWorlds.class,World.class, ExpressionType.SIMPLE,"[all] current worlds");

        loadWorldLoader();
    }
    
    private static void loadWorldLoader() {
        Registration.registerEffect(EffLoadWorldAutomatically.class, "[(1¦don't|1¦do not)] load %world% automatically");
        Registration.registerExpression(ExprAllAutomaticCreators.class, WorldCreatorData.class, ExpressionType.SIMPLE, "[all] automatic creators");
        Registration.registerPropertyExpression(ExprAutomaticCreator.class, WorldCreatorData.class, "string", "automatic creator %", "automatic creator for world %", "automatic creator for world named %");
    }
}
