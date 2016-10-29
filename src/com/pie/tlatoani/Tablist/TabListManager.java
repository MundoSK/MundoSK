package com.pie.tlatoani.Tablist;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.Tablist.Array.ArrayTabList;
import com.pie.tlatoani.Tablist.Simple.SimpleTabList;
import com.pie.tlatoani.SkinTexture.SkinTexture;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Tlatoani on 7/13/16.
 */
public class TabListManager implements Listener {
    private static final HashMap<UUID, SimpleTabList> simpleTabLists = new HashMap<>();
    private static final HashMap<UUID, ArrayTabList> arrayTabLists = new HashMap<>();
    private static final HashMap<UUID, ArrayList<UUID>> hiddenPlayerLists = new HashMap<>();
    private static final ArrayList<UUID> playerFreeTablists = new ArrayList<>();
    public static final PacketType packetType = PacketType.Play.Server.PLAYER_INFO;
    public static final Charset utf8 = Charset.forName("UTF-8");
    public static final SkinTexture DEFAULT_SKIN_TEXTURE = new SkinTexture.Simple(
            "eyJ0aW1lc3RhbXAiOjE0NzAwMjgwNDU3MzUsInByb2ZpbGVJZCI6IjQzYTgzNzNkNjQyOTQ1MTBhOWFhYjMwZjViM2NlYmIzIiwicHJvZmlsZU5hbWUiOiJTa3VsbENsaWVudFNraW42Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9iNTg3OTM1YzdmYmVjYzJmYWMxMDY0OWZjZGZiODM1YjQ2NTA3MzZiOWJmMWQ0NGVhZjc2ZDNiOWVmN2UwIn19fQ==",
            "eTy8+/waBl22GpAyTHx+QY40J3DY57F2FSkVupjJxAuuUfstvX/DxmJANKtIcYCYP9LUHh9DkP1T2bXUobHcx8GAICi8S/uEWXx96PHHjSr7wQ9uBC4NMCkV7dHHMKdVqEJ9jDpMvSax9vs1tOc2NWaeMbzc/345K95JaYVD+AV4W1+IuppXlMgDmCatUCgGDbzTuQKO8An9zFPciCRq1VSGaOPCj4PoIDQyMhSPqb1cPML/wH26Wtl4DEjnyVIyemk7oDBK29DXxtBLmzX6Ni1C8VM3UmG2StDC7dSwxJNLBHQ/aqXwupK4j0bZghiRbiaq4kAlPcpMeL+TTHac7oYFGihj/s/OVWaL0Fo2KgFZgKuZ26kDepCLEEOOoj2Zq8ohtxufPdTDqw032AyA/HbldnBIsCnQCDiq3XXdZHz0R+pvuf73BSHc7CiG2pwjSdSQ8XetlP70A9SddJu+iFuKGwzh/cvQ2H+sqoUYmIYIXcl2xJTy+Y/shxJDZZVxGCSHmj+4SYzJCg+nsNlEJ9HBG//LfeY+WhacbC9pPPy8wKnDqvIx0QX2YakyBFy659DEBEhSSNRQjOm78Zd9K7pP1QOrS2RDwsDSIXaR0gxT69Bv+Z/r+w8GJY6tHvT8aqTNQHpmv+kwMVdGOWMj3wMErW2aqjH9ffc1nuWht/E="
    );

    private static final Map<String, UUID> uuidSaver = new HashMap<>();
    private static final String uuidbeginning = "62960000-6296-3000-8000-6296";
    private static int uuidKeyCounter = 0;

    public static final HashMap<UUID, String> tabNames = new HashMap<>();

    public static class PacketSender implements Runnable {
        private PacketContainer packet;
        private Player[] players;

        public PacketSender(PacketContainer packet, Player... players) {
            this.packet = packet;
            this.players = players;
        }

        @Override
        public void run() {
            for (Player player : players) {
                try {
                    ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static {


        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Mundo.instance, PacketType.Play.Server.NAMED_ENTITY_SPAWN) {
            @Override
            public void onPacketSending(PacketEvent event) {
                Player player = Bukkit.getPlayer(event.getPacket().getUUIDs().read(0));
                if (event.getPlayer() != null && hiddenPlayerLists.get(event.getPlayer().getUniqueId()).contains(player.getUniqueId()) && !event.isCancelled()) {
                    Mundo.debug(TabListManager.class, "Player is hidden");
                    PlayerInfoData playerInfoData = new PlayerInfoData(WrappedGameProfile.fromPlayer(player), 5, EnumWrappers.NativeGameMode.fromBukkit(player.getGameMode()), WrappedChatComponent.fromJson(colorStringToJson(player.getPlayerListName())));
                    PacketContainer packet = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
                    packet.getPlayerInfoDataLists().writeSafely(0, Arrays.asList(playerInfoData));
                    packet.getPlayerInfoAction().writeSafely(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
                    try {
                        ProtocolLibrary.getProtocolManager().sendServerPacket(event.getPlayer(), packet);
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    PacketContainer removePacket = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
                    removePacket.getPlayerInfoDataLists().writeSafely(0, Arrays.asList(playerInfoData));
                    removePacket.getPlayerInfoAction().writeSafely(0, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
                    Mundo.scheduler.runTask(Mundo.instance, new PacketSender(removePacket, event.getPlayer()));
                }
            }
        });
    }

    /*Too attached to delete it
    public static class FriendlyPacketContainer extends PacketContainer {

        public FriendlyPacketContainer(PacketType packetType) {
            super(packetType);
        }

    }*/

    public static void onJoin(Player player) {
        simpleTabLists.put(player.getUniqueId(), new SimpleTabList(player));
        hiddenPlayerLists.put(player.getUniqueId(), new ArrayList<>());
        if (!playerFreeTablists.isEmpty())
            for (UUID targetUUID : playerFreeTablists.toArray(new UUID[0])) {
                hidePlayer(player, Bukkit.getPlayer(targetUUID));
            }

    }

    public static void onQuit(Player player) {
        getSimpleTabListForPlayer(player).clear();
        simpleTabLists.remove(player.getUniqueId());
        hiddenPlayerLists.remove(player.getUniqueId());
        hiddenPlayerLists.forEach(new BiConsumer<UUID, ArrayList<UUID>>() {
            @Override
            public void accept(UUID uuid, ArrayList<UUID> uuids) {
                uuids.remove(player.getUniqueId());
            }
        });
        playerFreeTablists.remove(player.getUniqueId());
        deactivateArrayTabList(player);
    }

    public static void activateArrayTabList(Player player, int columns, int rows, SkinTexture initialIcon) {
        Mundo.debug(TabListManager.class, "setARrayTagList");
        deactivateArrayTabList(player);
        arrayTabLists.put(player.getUniqueId(), new ArrayTabList(player, columns, rows, initialIcon));
    }

    public static void deactivateArrayTabList(Player player) {
        if (arrayTabLists.containsKey(player.getUniqueId())) {
            getArrayTabListForPlayer(player).clear();
            arrayTabLists.remove(player.getUniqueId());
        }
    }

    public static boolean tablistContainsPlayers(Player player) {
        return !playerFreeTablists.contains(player.getUniqueId());
    }

    public static void setTablistContainsPlayers(Player player, boolean whether) {
        if (whether != tablistContainsPlayers(player)) {
            ArrayList<UUID> hiddenPlayers = hiddenPlayerLists.get(player.getUniqueId());
            if (whether) {
                for (Player playerItem : Bukkit.getOnlinePlayers().toArray(new Player[0])) {
                    showPlayer(playerItem, player);
                }
                playerFreeTablists.remove(player.getUniqueId());
            } else {
                for (Player playerItem : Bukkit.getOnlinePlayers().toArray(new Player[0])) {
                    hidePlayer(playerItem, player);
                }
                playerFreeTablists.add(player.getUniqueId());
            }
        }
    }
    
    public static void showPlayer(Player player, Player to) {
        playerFreeTablists.remove(to.getUniqueId());
        ArrayList<UUID> hiddenPlayers = hiddenPlayerLists.get(to.getUniqueId());
        if (hiddenPlayers.contains(player.getUniqueId())) {
            hiddenPlayers.remove(player.getUniqueId());
            PlayerInfoData playerInfoData = new PlayerInfoData(WrappedGameProfile.fromPlayer(player), 5, EnumWrappers.NativeGameMode.fromBukkit(player.getGameMode()), WrappedChatComponent.fromJson(colorStringToJson(player.getPlayerListName())));
            PacketContainer packet = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
            packet.getPlayerInfoDataLists().writeSafely(0, Arrays.asList(playerInfoData));
            packet.getPlayerInfoAction().writeSafely(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(to, packet);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public static void hidePlayer(Player player, Player from) {
        ArrayList<UUID> hiddenPlayers = hiddenPlayerLists.get(from.getUniqueId());
        if (!hiddenPlayers.contains(player.getUniqueId())) {
            hiddenPlayers.add(player.getUniqueId());
            PlayerInfoData playerInfoData = new PlayerInfoData(WrappedGameProfile.fromPlayer(player), 5, EnumWrappers.NativeGameMode.fromBukkit(player.getGameMode()), WrappedChatComponent.fromJson(colorStringToJson(player.getPlayerListName())));
            PacketContainer packet = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
            packet.getPlayerInfoDataLists().writeSafely(0, Arrays.asList(playerInfoData));
            packet.getPlayerInfoAction().writeSafely(0, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(from, packet);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public static SimpleTabList getSimpleTabListForPlayer(Player player) {
        return simpleTabLists.get(player.getUniqueId());
    }

    public static ArrayTabList getArrayTabListForPlayer(Player player) {
        return arrayTabLists.get(player.getUniqueId());
    }

    //Util

    /*
    This method was coded by werter318 on Bukkit.org
     */
    public static String colorStringToJson(String original) {
        char colorChar = ChatColor.COLOR_CHAR;

        String template = "{text:\"TEXT\",color:COLOR,bold:BOLD,underlined:UNDERLINED,italic:ITALIC,strikethrough:STRIKETHROUGH,obfuscated:OBFUSCATED,extra:[EXTRA]}";
        String json = "";

        List<String> parts = new ArrayList<>();

        int first = 0;
        int last = 0;

        while ((first = original.indexOf(colorChar, last)) != -1) {
            int offset = 2;
            while ((last = original.indexOf(colorChar, first + offset)) - 2 == first) {
                offset += 2;
            }

            if (last == -1) {
                parts.add(original.substring(first));
                break;
            } else {
                parts.add(original.substring(first, last));
            }
        }

        if (parts.isEmpty()) {
            parts.add(original);
        }

        Pattern colorFinder = Pattern.compile("(" + colorChar + "([a-f0-9]))");
        for (String part : parts) {
            json = (json.isEmpty() ? template : json.replace("EXTRA", template));

            Matcher matcher = colorFinder.matcher(part);
            ChatColor color = (matcher.find() ? ChatColor.getByChar(matcher.group().charAt(1)) : ChatColor.WHITE);

            json = json.replace("COLOR", color.name().toLowerCase());
            json = json.replace("BOLD", String.valueOf(part.contains(ChatColor.BOLD.toString())));
            json = json.replace("ITALIC", String.valueOf(part.contains(ChatColor.ITALIC.toString())));
            json = json.replace("UNDERLINED", String.valueOf(part.contains(ChatColor.UNDERLINE.toString())));
            json = json.replace("STRIKETHROUGH", String.valueOf(part.contains(ChatColor.STRIKETHROUGH.toString())));
            json = json.replace("OBFUSCATED", String.valueOf(part.contains(ChatColor.MAGIC.toString())));

            json = json.replace("TEXT", part.replaceAll("(" + colorChar + "([a-z0-9]))", ""));
        }

        json = json.replace(",extra:[EXTRA]", "");

        return json;
    }

}
