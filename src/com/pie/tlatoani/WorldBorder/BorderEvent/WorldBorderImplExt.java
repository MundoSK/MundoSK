package com.pie.tlatoani.WorldBorder.BorderEvent;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;

/**
 * Created by Tlatoani on 8/16/17.
 */
public class WorldBorderImplExt extends WorldBorderImpl {

    public WorldBorderImplExt(World world) {
        super(world);
    }

    public boolean isInside(Location location) {
        return border.isInside(location);
    }
}
