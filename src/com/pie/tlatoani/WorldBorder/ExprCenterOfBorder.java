package com.pie.tlatoani.WorldBorder;

import ch.njol.skript.classes.Changer;
import com.pie.tlatoani.Core.Skript.ChangeablePropertyExpression;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Created by Tlatoani on 8/18/17.
 */
public class ExprCenterOfBorder extends ChangeablePropertyExpression<World, Location> {

    @Override
    public void change(World world, Location location, Changer.ChangeMode changeMode) {
        if (changeMode == Changer.ChangeMode.SET) {
            world.getWorldBorder().setCenter(location);
        }
    }

    @Override
    public Changer.ChangeMode[] getChangeModes() {
        return new Changer.ChangeMode[]{Changer.ChangeMode.SET};
    }

    @Override
    public Location convert(World world) {
        return world.getWorldBorder().getCenter();
    }
}
