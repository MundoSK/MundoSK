package com.pie.tlatoani.Util;

import org.bukkit.Location;
import org.bukkit.World;

/**
 * Created by Tlatoani on 8/9/17.
 * A Location whose setWorld() method does not do anything
 */
public class WorldLockedLocation extends Location {

    public WorldLockedLocation(Location location) {
        super(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    public WorldLockedLocation(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    public WorldLockedLocation(World world, double x, double y, double z, float yaw, float pitch) {
        super(world, x, y, z, yaw, pitch);
    }

    @Override
    public void setWorld(World world) {
        //do nothing
    }
}
