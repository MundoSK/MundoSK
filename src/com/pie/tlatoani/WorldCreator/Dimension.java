package com.pie.tlatoani.WorldCreator;

import org.bukkit.World;

/**
 * Created by Tlatoani on 8/18/17.
 */
public enum Dimension {
    NORMAL,
    NETHER,
    THE_END;

    public static Dimension fromEnvironment(World.Environment environment) {
        return valueOf(environment.name());
    }

    public World.Environment toEnvironment() {
        return World.Environment.valueOf(name());
    }
}
