package com.pie.tlatoani.Miscellaneous.MiscBukkit;

import com.pie.tlatoani.Core.Skript.MundoPropertyExpression;
import org.bukkit.Bukkit;
import org.bukkit.World;

/**
 * Created by Tlatoani on 8/18/17.
 */
public class ExprWorldByName extends MundoPropertyExpression<String, World> {

    @Override
    public World convert(String s) {
        return Bukkit.getWorld(s);
    }
}
