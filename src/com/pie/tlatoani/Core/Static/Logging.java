package com.pie.tlatoani.Core.Static;

import ch.njol.skript.Skript;
import com.pie.tlatoani.Mundo;
import org.bukkit.Bukkit;

import java.util.logging.Logger;

/**
 * Created by Tlatoani on 8/10/17.
 */
public final class Logging {
    private static Logger logger;

    public static void load(Logger logger) {
        Logging.logger = logger;
    }

    public static boolean classDebugs(Class c) {
        return Config.DEBUG_PACKAGES.getCurrentValue().contains(Utilities.getMundoCategory(c));
    }

    public static void info(String s) {
        logger.info(s);
    }

    public static void reportException(Object obj, Exception e) {
        info("An exception has occured within MundoSK");
        info("Please report this to the MundoSK page on forums.skunity.com");
        info("Bukkit/Spigot version: " + Bukkit.getVersion());
        info("Skript version: " + Skript.getVersion());
        info("MundoSK version: " + Mundo.getVersion());
        info("Exception at " + (obj instanceof Class ? (Class) obj : obj.getClass()).getSimpleName());
        e.printStackTrace();
    }

    public static void debug(Object obj, String msg) {
        Class debugClass = obj instanceof Class ? (Class) obj : obj.getClass();
        if (classDebugs(debugClass)) {
            info("DEBUG " + debugClass.getSimpleName() + ": " + msg);
        }
    }

    public static void debug(Object obj, Exception e) {
        Class debugClass = obj instanceof Class ? (Class) obj : obj.getClass();
        if (classDebugs(debugClass)) {
            info("DEBUG");
            info("An exception was reported for debugging while debug_mode was activated in the config");
            info("If you were told to activate debug_mode to help fix bugs in MundoSK on forums.skunity.com, then please copy and paste this message along with the full stack trace of the following error to hastebin.com and give the hastebin link to whoever is helping you fix this bug");
            info("If you are trying to fix a problem in MundoSK yourself, good luck :)");
            info("Otherwise, if you do not know why you are seeing this error here, go to the MundoSK config, set debug_mode to false, and restart your server");
            info("For help, go to the MundoSK page on forums.skunity.com");
            info("Bukkit/Spigot version: " + Bukkit.getVersion());
            info("Skript version: " + Skript.getVersion());
            info("MundoSK version: " + Mundo.getVersion());
            info("Exception debugged at " + debugClass.getSimpleName());
            e.printStackTrace();
        }
    }
}
