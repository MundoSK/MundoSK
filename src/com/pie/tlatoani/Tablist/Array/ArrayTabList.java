package com.pie.tlatoani.Tablist.Array;

import com.comphenix.protocol.events.PacketContainer;
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
public class ArrayTabList {
    private final Player player;
    private final String[][] displayNames = new String[4][20];
    private final Integer[][] latencies = new Integer[4][20];
    private final UUID[][] heads = new UUID[4][20];
    private final static String uuidbeginning = "10001000-1000-3000-8000-10001000";
    private int columns;
    private int rows;

    public ArrayTabList(Player player, int columns, int rows) {
        Mundo.debug(this, "constructor " + columns + " " + rows);
        this.player = player;
        this.columns = Mundo.limitToRange(1, columns, 4);
        this.rows = 0;
        rows = columns == 1 ? Mundo.limitToRange(1,  rows, 20) :
               columns == 2 ? Mundo.limitToRange(11, rows, 20) :
               columns == 3 ? Mundo.limitToRange(14, rows, 20) :
                              Mundo.limitToRange(16, rows, 20);
        setRows(rows);
    }

    private void sendPacket(int column, int row, EnumWrappers.PlayerInfoAction action) {
        int ping = latencies[column - 1][row - 1];
        String displayName = displayNames[column - 1][row - 1];
        WrappedChatComponent chatComponent = WrappedChatComponent.fromJson(TabListManager.colorStringToJson(displayName));
        int ending = (((column - 1) * 20) + row);
        if (ending % 2 == 0) ending += 79;
        UUID uuid = UUID.fromString(uuidbeginning + "10" + Mundo.toHexDigit(Mundo.divideNoRemainder(ending, 10)) + (ending % 10));
        Mundo.debug(this, "UUID " + column + ", " + row + ": " + uuid);
        UUID head = heads[column - 1][row - 1];
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

    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

    public void setColumns(int columns) {
        columns = Mundo.limitToRange(1, columns, 4);
        if (columns > this.columns) {
            for (int column = this.columns + 1; column <= columns; column++)
                for (int row = 1; row <= this.rows; row++) {
                    displayNames[column - 1][row - 1] = column + "," + (row < 10 ? "0" + row : row);
                    latencies[column - 1][row - 1] = 5;
                    sendPacket(column, row, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
                    displayNames[column - 1][row - 1] = "";
                    sendPacket(column, row, EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME);
                }
        } else if (columns < this.columns) {
            for (int column = columns + 1; column <= this.columns; column++)
                for (int row = 1; row <= this.rows; row++) {
                    sendPacket(column, row, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
                    displayNames[column - 1][row - 1] = null;
                    latencies[column - 1][row - 1] = null;
                    heads[column - 1][row - 1] = null;
                }
        }
        this.columns = columns;
    }

    public void setRows(int rows) {
        Mundo.debug(this, "Got here, this.columns " + this.columns + ", this.rows " + this.rows + ", rows " + rows);
        rows = columns == 1 ? Mundo.limitToRange(1,  rows, 20) :
               columns == 2 ? Mundo.limitToRange(11, rows, 20) :
               columns == 3 ? Mundo.limitToRange(14, rows, 20) :
                              Mundo.limitToRange(16, rows, 20);
        if (rows > this.rows) {
            for (int column = 1; column <= this.columns; column++)
                for (int row = this.rows + 1; row <= rows; row++) {
                    displayNames[column - 1][row - 1] = "";
                    latencies[column - 1][row - 1] = 5;
                    sendPacket(column, row, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
                }
        } else if (rows < this.rows) {
            for (int column = 1; column <= this.columns; column++)
                for (int row = rows + 1; row <= this.rows; row++) {
                    sendPacket(column, row, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
                    displayNames[column - 1][row - 1] = null;
                    latencies[column - 1][row - 1] = null;
                    heads[column - 1][row - 1] = null;
                }
        }
        this.rows = rows;
    }

    public void clear() {
        ArrayList<PlayerInfoData> arrayList = new ArrayList<>();
        for (int column = 1; column <= columns; column++)
            for (int row = 1; row <= rows; row++) {
                UUID uuid = UUID.nameUUIDFromBytes(("MundoSKTabList::" + column + "," + (row < 10 ? "0" + row : row)).getBytes(TabListManager.utf8));
                WrappedGameProfile gameProfile = new WrappedGameProfile(uuid, "");
                PlayerInfoData playerInfoData = new PlayerInfoData(gameProfile, 5, EnumWrappers.NativeGameMode.NOT_SET, WrappedChatComponent.fromText(""));
                arrayList.add(playerInfoData);
            }
        PacketContainer packetContainer = new PacketContainer(TabListManager.packetType);
        packetContainer.getPlayerInfoDataLists().writeSafely(0, arrayList);
        packetContainer.getPlayerInfoAction().writeSafely(0, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
        try {
            UtilPacketEvent.protocolManager.sendServerPacket(player, packetContainer);
        } catch (InvocationTargetException e) {
            Mundo.reportException(this, e);
        }
    }

    public String getDisplayName(int column, int row) {
        return Mundo.isInRange(1, column, columns) && Mundo.isInRange(1, row, rows) ? displayNames[column - 1][row - 1] : null;
    }

    public Integer getLatency(int column, int row) {
        return Mundo.isInRange(1, column, columns) && Mundo.isInRange(1, row, rows) ? latencies[column - 1][row - 1] : null;
    }

    public UUID getHead(int column, int row) {
        return Mundo.isInRange(1, column, columns) && Mundo.isInRange(1, row, rows) ? heads[column - 1][row - 1] : null;
    }

    public void setDisplayName(int column, int row, String displayName) {
        if (Mundo.isInRange(1, column, columns) && Mundo.isInRange(1, row, rows)) {
            displayNames[column - 1][row - 1] = displayName;
            sendPacket(column, row, EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME);
        }
    }

    public void setLatency(int column, int row, Integer ping) {
        if (Mundo.isInRange(1, column, columns) && Mundo.isInRange(1, row, rows)) {
            latencies[column - 1][row - 1] = ping;
            sendPacket(column, row, EnumWrappers.PlayerInfoAction.UPDATE_LATENCY);
        }
    }

    public void setHead(int column, int row, UUID head) {
        if (Mundo.isInRange(1, column, columns) && Mundo.isInRange(1, row, rows)) {
            sendPacket(column, row, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
            heads[column - 1][row - 1] = head;
            sendPacket(column, row, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
        }
    }
}
