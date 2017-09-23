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
import com.pie.tlatoani.Registration.Registration;
import com.pie.tlatoani.Skin.SkinMundo;
import com.pie.tlatoani.Socket.SocketMundo;
import com.pie.tlatoani.Socket.UtilFunctionSocket;
import com.pie.tlatoani.Tablist.TablistManager;
import com.pie.tlatoani.TerrainControl.TerrainControlMundo;
import com.pie.tlatoani.Throwable.ThrowableMundo;
import com.pie.tlatoani.Util.*;
import com.pie.tlatoani.WebSocket.WebSocketManager;
import com.pie.tlatoani.WorldBorder.WorldBorderMundo;
import com.pie.tlatoani.WorldCreator.WorldCreatorMundo;
import com.pie.tlatoani.WorldManagement.WorldLoader.WorldLoader;
import com.pie.tlatoani.WorldManagement.WorldManagementMundo;
import com.pie.tlatoani.ZExperimental.ZExperimentalMundo;
import mundosk_libraries.java_websocket.WebSocket;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class Mundo extends JavaPlugin {
	public static Mundo INSTANCE;
	public static final ChatColor PRIMARY_CHAT_COLOR = ChatColor.DARK_GREEN;
	public static final ChatColor ALT_CHAT_COLOR = ChatColor.GREEN;
	public static final ChatColor TRI_CHAT_COLOR = ChatColor.DARK_AQUA;

    @Override
	public void onEnable() {
        INSTANCE = this;

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
        Registration.register("Enchanted Book", EnchantedBookMundo::load);
        Registration.register("Generator", GeneratorManager::load);
        Registration.register("ListUtil", ListUtil::load);
        Registration.register("Miscellaneous", MiscMundo::load);
        Registration.register("Probability", ProbabilityMundo::load);
        Registration.register("Socket", SocketMundo::load);
        Registration.register("Throwable", ThrowableMundo::load);
        Registration.register("WebSocket", WebSocketManager::load);
        Registration.register("World Border", WorldBorderMundo::load);
        Registration.register("WorldCreator", WorldCreatorMundo::load);
        Registration.register("World Management", WorldManagementMundo::load);
        if (MundoUtil.serverHasPlugin("ProtocolLib")) {
            Registration.register("Packet", PacketManager::load);
            if (Config.IMPLEMENT_PACKET_STUFF.getCurrentValue()) {
                Registration.register("Skin", SkinMundo::load);
                Registration.register("Tablist", TablistManager::load);
            }
        }
        if (MundoUtil.serverHasPlugin("TerrainControl")) {
            Registration.register("TerrainControl", TerrainControlMundo::load);
        }
        if (Bukkit.getVersion().contains("1.8") || Bukkit.getVersion().contains("1.9") || Bukkit.getVersion().contains("1.10") || Bukkit.getVersion().contains("1.11")) {
            Registration.register("Achievement", AchievementMundo::load);
        }

        //ZExperimental ~ The Z is for mystery (it's so that it appears last in the package list)
        Registration.register("ZExperimental", ZExperimentalMundo::load);

        Registration.register("Custom Event", CustomEventMundo::load);
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
        return GeneratorManager.getSkriptGenerator(id);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equals("mundosk")) {
            if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
                sender.sendMessage(PRIMARY_CHAT_COLOR + "MundoSK Command Help");
                sender.sendMessage(formatCommandDescription("[help]", "Prints this list of commands"));
                sender.sendMessage(formatCommandDescription("desc[ription]", "Prints a description of MundoSK"));
                sender.sendMessage(formatCommandDescription("ver[sion]", "Prints the version of MundoSK running on this server"));
                sender.sendMessage(formatCommandDescription("config", "Prints the current config options"));
                sender.sendMessage(formatCommandDescription("config reload", "Reloads MundoSK's config"));
            } else if (args[0].equalsIgnoreCase("desc") || args[0].equalsIgnoreCase("description")) {
                sender.sendMessage(PRIMARY_CHAT_COLOR + "MundoSK is a Skript Addon that has features including Packets, World Borders, World Management, Custom World Generation, Tablist, Skin Modification, Sockets, and more!");
                sender.sendMessage(formatMundoSKInfo("Your MundoSK Version", getVersion()));
                sender.sendMessage(formatMundoSKInfo("skUnity Forums Page", "https://forums.skunity.com/resources/mundosk.69/"));
                sender.sendMessage(formatMundoSKInfo("GitHub", "https://github.com/MundoSK/MundoSK"));
                sender.sendMessage(formatMundoSKInfo("Skript Addon Discord Invite", "https://discord.gg/vb9dGbu"));
            } else if (args[0].equalsIgnoreCase("ver") || args[0].equalsIgnoreCase("version")) {
                sender.sendMessage(formatMundoSKInfo("Your MundoSK Version", getVersion()));
            } else if (args[0].equalsIgnoreCase("config")) {
                if (args.length >= 2 && args[1].equalsIgnoreCase("reload")) {
                    Config.reload();
                    sender.sendMessage(PRIMARY_CHAT_COLOR + "Reloaded MundoSK's Config!");
                }
                sender.sendMessage(PRIMARY_CHAT_COLOR + "MundoSK Config");
                Config.displayConfig(sender);
            } else {
                sender.sendMessage(PRIMARY_CHAT_COLOR + "MundoSK didn't understand this command argument: " + ALT_CHAT_COLOR + args[0]);
                sender.sendMessage(PRIMARY_CHAT_COLOR + "Do " + ALT_CHAT_COLOR + "/mundosk " + PRIMARY_CHAT_COLOR + "to show a list of MundoSK commands");

            }
            return true;
        }
        return false;
    }

    public static String formatCommandDescription(String args, String desc) {
        return ALT_CHAT_COLOR + "/mundosk " + args + " " + PRIMARY_CHAT_COLOR + desc;
    }

    public static String formatMundoSKInfo(String name, String info) {
        return PRIMARY_CHAT_COLOR + name + " " + ALT_CHAT_COLOR + info;
    }

    public static String getVersion() {
        return INSTANCE.getDescription().getVersion();
    }
	
}
