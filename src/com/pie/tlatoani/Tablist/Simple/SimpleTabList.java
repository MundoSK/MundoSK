package com.pie.tlatoani.Tablist.Simple;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.ProtocolLib.UtilPacketEvent;
import com.pie.tlatoani.Tablist.TabListManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Created by Tlatoani on 7/15/16.
 */
public class SimpleTabList {
    private final Player player;
    private final HashMap<String, String> displayNames = new HashMap<>();
    private final HashMap<String, Integer> latencies = new HashMap<>();
    private final HashMap<String, UUID> heads = new HashMap<>();

    public SimpleTabList(Player player) {
        this.player = player;
    }

    private void sendPacket(String id, EnumWrappers.PlayerInfoAction action) {
        int ping = latencies.get(id);
        String displayName = displayNames.get(id);
        WrappedChatComponent chatComponent = WrappedChatComponent.fromJson(TabListManager.colorStringToJson(displayName));
        UUID uuid = UUID.nameUUIDFromBytes(("MundoSKTabList::" + id).getBytes(TabListManager.utf8));
        UUID head = heads.get(id);
        WrappedGameProfile gameProfile = new WrappedGameProfile(uuid, "");
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
        PacketContainer packetContainer = new PacketContainer(TabListManager.packetType);
        packetContainer.getPlayerInfoDataLists().writeSafely(0, playerInfoDatas);
        packetContainer.getPlayerInfoAction().writeSafely(0, action);
        try {
            UtilPacketEvent.protocolManager.sendServerPacket(player, packetContainer);
        } catch (InvocationTargetException e) {
            Mundo.reportException(this, e);
        }
    }

    public void clear() {
        String[] ids = displayNames.keySet().toArray(new String[0]);
        for (int i = 0; i < ids.length; i++) {
            deleteTab(ids[i]);
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
            sendPacket(id, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
        }
    }

    public void deleteTab(String id) {
        if (tabExists(id)) {
            sendPacket(id, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
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
            sendPacket(id, EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME);
        }
    }

    public void setLatency(String id, Integer ping) {
        if (tabExists(id)) {
            latencies.put(id, ping);
            sendPacket(id, EnumWrappers.PlayerInfoAction.UPDATE_LATENCY);
        }
    }

    public void setHead(String id, UUID head) {
        if (tabExists(id)) {
            sendPacket(id, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
            heads.put(id, head);
            sendPacket(id, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
        }
    }

}
