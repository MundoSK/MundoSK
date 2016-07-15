package com.pie.tlatoani.Tablist;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.*;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.ProtocolLib.UtilPacketEvent;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Tlatoani on 7/13/16.
 */
public class TabListManager implements Listener {
    private static final HashMap<UUID, TabListManager> tabLists = new HashMap<>();
    private static final PacketType packetType = PacketType.Play.Server.PLAYER_INFO;
    private static final Charset utf8 = Charset.forName("UTF-8");
    private final Player player;
    private final HashMap<String, String> displayNames = new HashMap<>();
    private final HashMap<String, Integer> latencies = new HashMap<>();
    private final HashMap<String, UUID> heads = new HashMap<>();

    static {
        UtilPacketEvent.protocolManager.addPacketListener(new PacketAdapter(Mundo.instance, packetType) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (isActivated(event.getPlayer()) && event.getPacket().getPlayerInfoDataLists().readSafely(0).get(0).getGameMode() != EnumWrappers.NativeGameMode.NOT_SET) {
                    event.setCancelled(true);
                }
                Mundo.debug(TabListManager.class, "Is NOT_SET: " + (event.getPacket().getPlayerInfoDataLists().readSafely(0).get(0).getGameMode() != EnumWrappers.NativeGameMode.NOT_SET));
            }
        });
    }

    public static class FriendlyPacketContainer extends PacketContainer {

        public FriendlyPacketContainer(PacketType packetType) {
            super(packetType);
        }

    }

    private TabListManager(Player player) {
        this.player = player;
    }

    public static Boolean isActivated(Player player) {
        return tabLists.containsKey(player.getUniqueId());
    }

    public static void setActivated(Player player, boolean activated) {
        if (!activated) {
            tabLists.remove(player.getUniqueId());
        } else if (!isActivated(player)) {
            tabLists.put(player.getUniqueId(), new TabListManager(player));
        }
    }

    public static TabListManager getForPlayer(Player player) {
        return tabLists.get(player.getUniqueId());
    }

    //

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

    //Non Static Stuff

    private void sendPacket(String id, PlayerInfoAction action) {
        int ping = latencies.get(id);
        String displayName = displayNames.get(id);
        WrappedChatComponent chatComponent = WrappedChatComponent.fromJson(colorStringToJson(displayName));
        UUID uuid = UUID.nameUUIDFromBytes(("MundoSKTabList::" + id).getBytes(utf8));
        UUID head = heads.get(id);
        WrappedGameProfile gameProfile = new WrappedGameProfile(uuid, displayName);
        if (head != null) {
            WrappedGameProfile headProfile = WrappedGameProfile.fromPlayer(Bukkit.getPlayer(head));
            gameProfile.getProperties().putAll(headProfile.getProperties());
        } else {
            //WrappedSignedProperty property = new WrappedSignedProperty("textures", "", "");
            //gameProfile.getProperties().put("textures", property);
            //String url;
            //String formattedProperty = String.format("{textures:{SKIN:{url:\"%s\"}}}", url);
            //byte[] encodedData = Base64.encodeBase64(formattedProperty.getBytes());
        }
        PlayerInfoData playerInfoData = new PlayerInfoData(gameProfile, ping, EnumWrappers.NativeGameMode.NOT_SET, chatComponent);
        List<PlayerInfoData> playerInfoDatas = Arrays.asList(playerInfoData);
        PacketContainer packetContainer = new FriendlyPacketContainer(packetType);
        packetContainer.getPlayerInfoDataLists().writeSafely(0, playerInfoDatas);
        packetContainer.getPlayerInfoAction().writeSafely(0, action);
        try {
            UtilPacketEvent.protocolManager.sendServerPacket(player, packetContainer);
        } catch (InvocationTargetException e) {
            Mundo.reportException(this, e);
        }
    }

    public boolean tabExists(String id) {
        return displayNames.containsKey(id);
    }

    public void createTab(String id, String displayName, Integer ping, UUID head) {
        if (!tabExists(id)) {
            ping = Math.max(ping, 0);
            ping = Math.min(ping, 5);
            latencies.put(id, ping);
            displayNames.put(id, displayName);
            heads.put(id, head);
            sendPacket(id, PlayerInfoAction.ADD_PLAYER);
        }
    }

    public void deleteTab(String id) {
        if (tabExists(id)) {
            sendPacket(id, PlayerInfoAction.REMOVE_PLAYER);
            displayNames.remove(id);
            latencies.remove(id);
            heads.remove(id);
        }
    }

    public String getDisplayName(String id) {
        return displayNames.get(id);
    }

    public Integer getLatency(String id) {
        return latencies.get(id);
    }

    public UUID getHead(String id) {
        return heads.get(id);
    }

    public void setDisplayName(String id, String displayName) {
        if (tabExists(id)) {
            displayNames.put(id, displayName);
            sendPacket(id, PlayerInfoAction.UPDATE_DISPLAY_NAME);
        }
    }

    public void setLatency(String id, Integer ping) {
        if (tabExists(id)) {
            latencies.put(id, ping);
            sendPacket(id, PlayerInfoAction.UPDATE_LATENCY);
        }
    }

    public void setHead(String id, UUID head) {
        if (tabExists(id)) {
            sendPacket(id, PlayerInfoAction.REMOVE_PLAYER);
            heads.put(id, head);
            sendPacket(id, PlayerInfoAction.ADD_PLAYER);
        }
    }

}
