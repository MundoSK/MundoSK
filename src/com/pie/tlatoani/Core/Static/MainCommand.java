package com.pie.tlatoani.Core.Static;

import com.pie.tlatoani.Core.Registration.DocumentationCommand;
import com.pie.tlatoani.Mundo;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class MainCommand {

    public static final ChatColor PRIMARY_CHAT_COLOR = ChatColor.DARK_GREEN;
    public static final ChatColor ALT_CHAT_COLOR = ChatColor.GREEN;
    public static final ChatColor TRI_CHAT_COLOR = ChatColor.DARK_AQUA;

    public static boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equals("mundosk")) {
            if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
                sender.sendMessage(PRIMARY_CHAT_COLOR + "MundoSK Command Help");
                sender.sendMessage(formatCommandDescription("[help]", "Prints this list of commands"));
                sender.sendMessage(formatCommandDescription("desc[ription]", "Prints a description of MundoSK"));
                sender.sendMessage(formatCommandDescription("ver[sion]", "Prints the version of MundoSK running on this server"));
                sender.sendMessage(formatCommandDescription("config", "Prints the current config options"));
                sender.sendMessage(formatCommandDescription("config reload", "Reloads MundoSK's config"));
                sender.sendMessage(formatCommandDescription("doc[s]", "Accesses MundoSK's documentation"));
                sender.sendMessage(formatCommandDescription("update", "Prints the latest update information (actually it doesn't do anything)"));
                sender.sendMessage(formatCommandDescription("update <version>", "Downloads the given MundoSK version to be installed on server restart"));
            } else if (args[0].equalsIgnoreCase("desc") || args[0].equalsIgnoreCase("description")) {
                sender.sendMessage(PRIMARY_CHAT_COLOR + "MundoSK is a Skript Addon that has features including Packets, World Borders, World Management, Custom World Generation, Tablist, Skin Modification, Sockets, and more!");
                sender.sendMessage(formatMundoSKInfo("Your MundoSK Version", Mundo.getVersion()));
                sender.sendMessage(formatMundoSKInfo("skUnity Forums Page", "https://forums.skunity.com/resources/mundosk.69/"));
                sender.sendMessage(formatMundoSKInfo("Skript Hub Documentation", "http://skripthub.net/docs/?addon=MundoSK"));
                sender.sendMessage(formatMundoSKInfo("GitHub", "https://github.com/MundoSK/MundoSK"));
                sender.sendMessage(formatMundoSKInfo("Skript Chat Discord Invite", "https://discord.gg/vb9dGbu"));
            } else if (args[0].equalsIgnoreCase("ver") || args[0].equalsIgnoreCase("version")) {
                sender.sendMessage(formatMundoSKInfo("Your MundoSK Version", Mundo.getVersion()));
            } else if (args[0].equalsIgnoreCase("config")) {
                if (args.length >= 2 && args[1].equalsIgnoreCase("reload")) {
                    Config.reload();
                    sender.sendMessage(PRIMARY_CHAT_COLOR + "Reloaded MundoSK's Config!");
                }
                sender.sendMessage(PRIMARY_CHAT_COLOR + "MundoSK Config");
                Config.displayConfig(sender);
            } else if (args[0].equalsIgnoreCase("doc") || args[0].equalsIgnoreCase("docs")) {
                DocumentationCommand.accessDocumentation(sender, args);
            } else if (args[0].equalsIgnoreCase("update")) {
                if (args.length == 1) {

                }
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
}
