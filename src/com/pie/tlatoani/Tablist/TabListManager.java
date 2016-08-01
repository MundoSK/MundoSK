package com.pie.tlatoani.Tablist;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.*;
import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.ProtocolLib.UtilPacketEvent;
import com.pie.tlatoani.Tablist.Array.ArrayTabList;
import com.pie.tlatoani.Tablist.Simple.SimpleTabList;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.json.simple.JSONObject;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Tlatoani on 7/13/16.
 */
public class TabListManager implements Listener {
    private static final HashMap<UUID, SimpleTabList> simpleTabLists = new HashMap<>();
    private static final HashMap<UUID, ArrayTabList> arrayTabLists = new HashMap<>();
    public static final PacketType packetType = PacketType.Play.Server.PLAYER_INFO;
    public static final Charset utf8 = Charset.forName("UTF-8");
    static {
        UtilPacketEvent.protocolManager.addPacketListener(new PacketAdapter(Mundo.instance, packetType) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (hasCustomTabList(event.getPlayer()) && event.getPacket().getPlayerInfoDataLists().readSafely(0).get(0).getGameMode() != EnumWrappers.NativeGameMode.NOT_SET) {
                    event.setCancelled(true);
                }
            }
        });
    }

    /* Too attached to delete it
    public static class FriendlyPacketContainer extends PacketContainer {

        public FriendlyPacketContainer(PacketType packetType) {
            super(packetType);
        }

    }
    */

    public static boolean hasCustomTabList(Player player) {
        return simpleTabLists.containsKey(player.getUniqueId()) || arrayTabLists.containsKey(player.getUniqueId());
    }

    public static void setSimpleTabList(Player player) {
        clearTabList(player);
        simpleTabLists.put(player.getUniqueId(), new SimpleTabList(player));
    }

    public static void setArrayTabList(Player player, int columns, int rows, TabListIcon initialIcon) {
        Mundo.debug(TabListManager.class, "setARrayTagList");
        clearTabList(player);
        arrayTabLists.put(player.getUniqueId(), new ArrayTabList(player, columns, rows, initialIcon));
    }

    public static void clearTabList(Player player) {
        if (arrayTabLists.containsKey(player.getUniqueId())) {
            getArrayTabListForPlayer(player).clear();
            arrayTabLists.remove(player.getUniqueId());
        } else if (simpleTabLists.containsKey(player.getUniqueId())) {
            getSimpleTabListForPlayer(player).clear();
            simpleTabLists.remove(player.getUniqueId());
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

    public static String getTextureValue(String url, Player onlinePlayer) {
        JSONObject returnValue = new JSONObject();
        returnValue.put("timestamp", 0);
        returnValue.put("profileId", onlinePlayer.getUniqueId().toString().replace("-", ""));
        returnValue.put("profileName", onlinePlayer.getDisplayName());
        JSONObject texturesValue = new JSONObject();
        JSONObject SKINValue = new JSONObject();
        Mundo.debug(TabListManager.class, "URL: " + url);
        Mundo.debug(TabListManager.class, "SKINValue:: " + SKINValue);
        SKINValue.put("url", url);
        texturesValue.put("SKIN", SKINValue);
        returnValue.put("textures", texturesValue);
        return Base64Coder.encodeString(returnValue.toJSONString().replace("\\/", "/"));
    }

}
