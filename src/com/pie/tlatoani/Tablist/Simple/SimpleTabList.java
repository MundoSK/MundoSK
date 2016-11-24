package com.pie.tlatoani.Tablist.Simple;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;
import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.ProtocolLib.UtilPacketEvent;
import com.pie.tlatoani.Skin.Skin;
import com.pie.tlatoani.Tablist.TabListManager;
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
    private final HashMap<String, Skin> heads = new HashMap<>();

    public SimpleTabList(Player player) {
        this.player = player;
    }

    private void sendPacket(String id, EnumWrappers.PlayerInfoAction action) {
        int ping = latencies.get(id);
        String displayName = displayNames.get(id);
        WrappedChatComponent chatComponent = WrappedChatComponent.fromJson(TabListManager.colorStringToJson(displayName));
        UUID uuid = UUID.nameUUIDFromBytes(("MundoSKTabList::" + id).getBytes(TabListManager.utf8));
        Skin icon = heads.get(id);
        WrappedGameProfile gameProfile = new WrappedGameProfile(uuid, "");
        if (action == EnumWrappers.PlayerInfoAction.ADD_PLAYER) {
            if (icon == null) icon = TabListManager.DEFAULT_SKIN_TEXTURE;
            icon.retrieveSkinTextures(gameProfile.getProperties());
        }
        PlayerInfoData playerInfoData = new PlayerInfoData(gameProfile, ping, EnumWrappers.NativeGameMode.NOT_SET, chatComponent);
        List<PlayerInfoData> playerInfoDatas = Arrays.asList(playerInfoData);
        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
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

    public void createTab(String id, String displayName, Integer ping, Skin head) {
        TabListManager.deactivateArrayTabList(player);
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

    public Skin getHead(String id) {
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

    public void setHead(String id, Skin icon) {
        if (tabExists(id)) {
            sendPacket(id, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
            heads.put(id, icon);
            sendPacket(id, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
        }
    }

}
