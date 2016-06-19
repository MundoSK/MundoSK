package com.pie.tlatoani.Miscellaneous;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.StringUtils;
import org.bukkit.event.Event;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tlatoani on 6/15/16.
 */
public class ExprLoadedScripts extends SimpleExpression<String> {
    private final static FileFilter scriptFilter = new FileFilter() {
        @Override
        public boolean accept(final @Nullable File f) {
            return f != null && (f.isDirectory() || StringUtils.endsWithIgnoreCase("" + f.getName(), ".sk")) && !f.getName().startsWith("-");
        }
    };

    @Override
    protected String[] get(Event event) {
        File scriptsFolder = new File(Skript.getInstance().getDataFolder(), Skript.SCRIPTSFOLDER + File.separator);
        if (!scriptsFolder.isDirectory()) {
            return new String[0];
        }
        return getScripts(scriptsFolder).toArray(new String[0]);
    }

    private static List<String> getScripts(File directory) {
        List<String> strings = new ArrayList<String>();
        File[] files = directory.listFiles(scriptFilter);
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                strings.addAll(getScripts(files[i]));
            } else {
                strings.add(files[i].getName());
            }
        }
        return strings;
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "loaded scripts";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        return true;
    }
}
