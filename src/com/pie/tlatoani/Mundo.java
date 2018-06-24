package com.pie.tlatoani;

import ch.njol.skript.Skript;
import com.pie.tlatoani.Achievement.AchievementMundo;
import com.pie.tlatoani.Book.BookMundo;
import com.pie.tlatoani.Chunk.ChunkMundo;
import com.pie.tlatoani.CodeBlock.CodeBlockMundo;
import com.pie.tlatoani.CustomEvent.CustomEventMundo;
import com.pie.tlatoani.EnchantedBook.EnchantedBookMundo;
import com.pie.tlatoani.Generator.GeneratorManager;
import com.pie.tlatoani.ListUtil.ListUtil;
import com.pie.tlatoani.Miscellaneous.MiscMundo;
import com.pie.tlatoani.Probability.ProbabilityMundo;
import com.pie.tlatoani.ProtocolLib.PacketManager;
import com.pie.tlatoani.Core.Registration.Documentation;
import com.pie.tlatoani.Core.Registration.Registration;
import com.pie.tlatoani.Skin.SkinMundo;
import com.pie.tlatoani.Socket.SocketMundo;
import com.pie.tlatoani.Socket.UtilFunctionSocket;
import com.pie.tlatoani.Tablist.TablistMundo;
import com.pie.tlatoani.TerrainControl.TerrainControlMundo;
import com.pie.tlatoani.Throwable.ThrowableMundo;
import com.pie.tlatoani.Core.Static.*;
import com.pie.tlatoani.WebSocket.WebSocketManager;
import com.pie.tlatoani.WorldBorder.WorldBorderMundo;
import com.pie.tlatoani.WorldCreator.WorldCreatorMundo;
import com.pie.tlatoani.WorldManagement.WorldLoader.WorldLoader;
import com.pie.tlatoani.WorldManagement.WorldManagementMundo;
import com.pie.tlatoani.ZExperimental.ZExperimentalMundo;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class Mundo extends JavaPlugin {
	private static Mundo instance;

    public static Mundo get() {
	    if (instance == null) {
	        throw new IllegalStateException("MundoSK has not been enabled yet!");
        }
	    return instance;
    }

    @Override
	public void onEnable() {
        instance = this;

        Config.reload();
        Logging.load(getLogger());
        Scheduling.load();
        WorldLoader.load();
		Skript.registerAddon(this);

        Logging.info("Pie is awesome :D");
        if (getDescription().getVersion().toUpperCase().contains("BETA")) {
            Logging.info("You are currently running a BETA version of MundoSK");
            Logging.info("You should only run BETA versions of MundoSK on test servers unless Tlatoani or another reliable source has recommended otherwise");
        }
        if (!Config.DEBUG_PACKAGES.getCurrentValue().isEmpty()) {
            Logging.info("You have enabled debug for certain packages in MundoSK config");
            Logging.info("Debug should only be enabled when you are trying to fix a bug or assist someone else with fixing a bug in MundoSK");
            Logging.info("By having debug enabled, you will have tons of random annoying spam in your console");
            Logging.info("If you would like to disable debug, simply go to your 'plugins' folder, go to the 'MundoSK' folder, open 'config.yml', and where it says 'debug', remove all following text");
        }

        Registration.register("Book", BookMundo::load);
        Registration.register("Chunk", ChunkMundo::load);
        Registration.register("CodeBlock", CodeBlockMundo::load);
        Registration.register("EnchantedBook", EnchantedBookMundo::load);
        Registration.register("Generator", GeneratorManager::load);
        Registration.register("ListUtil", ListUtil::load);
        Registration.register("Miscellaneous", MiscMundo::load);
        Registration.register("Probability", ProbabilityMundo::load);
        Registration.register("Socket", SocketMundo::load);
        Registration.register("Throwable", ThrowableMundo::load);
        Registration.register("WebSocket", WebSocketManager::load);
        Registration.register("WorldBorder", WorldBorderMundo::load);
        Registration.register("WorldCreator", WorldCreatorMundo::load);
        Registration.register("WorldManagement", WorldManagementMundo::load);
        if (Utilities.serverHasPlugin("ProtocolLib")) {
            Registration.register("Packet", PacketManager::load, "ProtocolLib");
            if (Config.IMPLEMENT_PACKET_STUFF.getCurrentValue()) {
                Registration.register("Skin", SkinMundo::load, "ProtocolLib");
                Registration.register("Tablist", TablistMundo::load, "ProtocolLib");
            }
        }
        if (Utilities.serverHasPlugin("TerrainControl")) {
            Registration.register("TerrainControl", TerrainControlMundo::load, "TerrainControl");
        }
        if (Bukkit.getVersion().contains("1.8") || Bukkit.getVersion().contains("1.9") || Bukkit.getVersion().contains("1.10") || Bukkit.getVersion().contains("1.11")) {
            Registration.register("Achievement", AchievementMundo::load);
        }

        //ZExperimental ~ The Z is for mystery (it's so that it appears last in the package list)
        Registration.register("ZExperimental", ZExperimentalMundo::load);

        Registration.register("CustomEvent", CustomEventMundo::load);
		Logging.info("Awesome syntaxes have been registered!");
		Scheduling.sync(Documentation::buildDocumentation);
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
        return GeneratorManager.getSkriptGenerator(id);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return MainCommand.onCommand(sender, cmd, label, args);
    }

    public static String getVersion() {
        return instance.getDescription().getVersion();
    }
	
}
