package com.pie.tlatoani.WorldCreator;

import com.pie.tlatoani.Core.Skript.MundoPropertyExpression;
import org.bukkit.World;

/**
 * Created by Tlatoani on 8/18/17.
 */
public class ExprCreatorOf extends MundoPropertyExpression<World, WorldCreatorData> {

    @Override
    public WorldCreatorData convert(World world) {
        return WorldCreatorData.fromWorld(world);
    }
}
