package com.pie.tlatoani;

import ch.njol.skript.Skript;
import com.pie.tlatoani.Achievement.AchievementMundo;
import com.pie.tlatoani.Book.BookMundo;
import com.pie.tlatoani.Chunk.ChunkMundo;
import com.pie.tlatoani.CodeBlock.CodeBlockMundo;
import com.pie.tlatoani.CustomEvent.CustomEventMundo;
import com.pie.tlatoani.EnchantedBook.EnchantedBookMundo;
import com.pie.tlatoani.Generator.SkriptGeneratorManager;
import com.pie.tlatoani.ListUtil.ListUtil;
import com.pie.tlatoani.Miscellaneous.ExprEventSpecificValue;
import com.pie.tlatoani.Miscellaneous.MiscMundo;
import com.pie.tlatoani.Probability.ProbabilityMundo;
import com.pie.tlatoani.ProtocolLib.PacketManager;
import com.pie.tlatoani.Skin.SkinMundo;
import com.pie.tlatoani.Socket.SocketMundo;
import com.pie.tlatoani.Socket.UtilFunctionSocket;
import com.pie.tlatoani.Tablist.TablistMundo;
import com.pie.tlatoani.TerrainControl.TerrainControlMundo;
import com.pie.tlatoani.Throwable.ThrowableMundo;
import com.pie.tlatoani.Util.*;
import com.pie.tlatoani.WebSocket.WebSocketManager;
import com.pie.tlatoani.WorldBorder.WorldBorderMundo;
import com.pie.tlatoani.WorldCreator.WorldCreatorMundo;
import com.pie.tlatoani.WorldManagement.WorldLoader.WorldLoader;
import com.pie.tlatoani.WorldManagement.WorldManagementMundo;
import com.pie.tlatoani.ZExperimental.ZExperimentalMundo;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Mundo extends JavaPlugin {
	public static Mundo INSTANCE;
    public static Boolean implementPacketStuff;

    @Override
	public void onEnable() {
        FileConfiguration config = getConfig();
        config.addDefault("debug", Arrays.asList(new String[0]));
        config.addDefault("enable_custom_skin_and_tablist", true);
        config.addDefault("tablist_remove_tab_delay_spawn", 5);
        config.addDefault("tablist_remove_tab_delay_respawn", 5);
        config.options().copyDefaults(true);
        List<String> debugPackages = config.getStringList("debug");
        implementPacketStuff = config.getBoolean("enable_custom_skin_and_tablist");
        int tablistSpawnRemoveTabDelay = config.getInt("tablist_remove_tab_delay_spawn");
        int tablistRespawnRemoveTabDelay = config.getInt("tablist_remove_tab_delay_respawn");
        saveConfig();
        INSTANCE = this;

        Logging.load(getLogger(), debugPackages);
        Scheduling.load();
        WorldLoader.load();
		Skript.registerAddon(this);
        Logging.info("Pie is awesome :D");
        if (getDescription().getVersion().toUpperCase().contains("BETA")) {
            Logging.info("You are currently running a BETA version of MundoSK");
            Logging.info("You should only run BETA versions of MundoSK on test servers unless Tlatoani or another reliable source has recommended otherwise");
        }
        if (!debugPackages.isEmpty()) {
            Logging.info("You have enabled debug for certain packages in MundoSK config");
            Logging.info("Debug should only be enabled when you are trying to fix a bug or assist someone else with fixing a bug in MundoSK");
            Logging.info("By having debug enabled, you will have tons of random annoying spam in your console");
            Logging.info("If you would like to disable debug, simply go to your 'plugins' folder, go to the 'MundoSK' folder, open 'config.yml', and where it says 'debug', remove all following text");
        }

		BookMundo.load();
        ChunkMundo.load();
        CodeBlockMundo.load();
        EnchantedBookMundo.load();
		SkriptGeneratorManager.load();
        ListUtil.load();
        MiscMundo.load();
        ProbabilityMundo.load();
        SocketMundo.load();
        ThrowableMundo.load();
        WebSocketManager.load();
        WorldBorderMundo.load();
        WorldCreatorMundo.load();
        WorldManagementMundo.load();
		if (MundoUtil.serverHasPlugin("ProtocolLib")) {
		    PacketManager.load();
		    if (implementPacketStuff) {
		        SkinMundo.load();
		        TablistMundo.load(tablistSpawnRemoveTabDelay, tablistRespawnRemoveTabDelay);
            }
		}
		if (MundoUtil.serverHasPlugin("TerrainControl")) {
		    TerrainControlMundo.load();
		}
        if (Bukkit.getVersion().contains("1.8") || Bukkit.getVersion().contains("1.9") || Bukkit.getVersion().contains("1.10") || Bukkit.getVersion().contains("1.11")) {
            AchievementMundo.load();
        }
        //ZExperimental ~ The Z is for mystery (it's so that it appears last in the package list)
        ZExperimentalMundo.load();
        //
        Registration.registerEnumAllExpressions();
        CustomEventMundo.load();
        ExprEventSpecificValue.register();
		Logging.info("Awesome syntaxes have been registered!");
        Scheduling.sync(Metrics::enableMundoSKMetrics);
	}

    @Override
    public void onDisable() {
        UtilFunctionSocket.onDisable();
        Logging.info("Closed all function sockets (if any were open)");
        WebSocketManager.stopAllServers(0);
        Logging.info("Stopped all WebSocket servers (if any were open)");
        try {
            WorldLoader.save();
            Logging.info("Successfully saved all (if any) world loaders");
        } catch (IOException e) {
            Logging.info("A problem occurred while saving world loaders");
            Logging.reportException(this, e);
        }
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String unusedWorldName, String id) {
        return SkriptGeneratorManager.getSkriptGenerator(id);
    }

    public static String getVersion() {
        return INSTANCE.getDescription().getVersion();
    }
	
}
