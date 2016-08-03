package com.pie.tlatoani.Tablist.Array;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;
import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.ProtocolLib.UtilPacketEvent;
import com.pie.tlatoani.Tablist.SkinTexture.SkinTexture;
import com.pie.tlatoani.Tablist.TabListManager;
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
    private final SkinTexture[][] heads = new SkinTexture[4][20];
    private final static String uuidbeginning = "10001000-1000-3000-8000-10001000";
    private int columns;
    private int rows;
    public SkinTexture initialIcon;

    public ArrayTabList(Player player, int columns, int rows, SkinTexture initialIcon) {
        Mundo.debug(this, "constructor " + columns + " " + rows);
        this.player = player;
        this.columns = Mundo.limitToRange(1, columns, 4);
        this.rows = 0;
        rows = columns == 1 ? Mundo.limitToRange(1,  rows, 20) :
               columns == 2 ? Mundo.limitToRange(11, rows, 20) :
               columns == 3 ? Mundo.limitToRange(14, rows, 20) :
                              Mundo.limitToRange(16, rows, 20);
        this.initialIcon = initialIcon;
        setRows(rows);
    }

    private void sendPacket(int column, int row, EnumWrappers.PlayerInfoAction action) {
        int ping = latencies[column - 1][row - 1];
        String displayName = displayNames[column - 1][row - 1];
        SkinTexture icon = heads[column - 1][row - 1];
        WrappedChatComponent chatComponent = WrappedChatComponent.fromJson(TabListManager.colorStringToJson(displayName));
        int identifier = (((column - 1) * 20) + row);
        if (identifier % 2 == 0) identifier += 79;
        /*if (icon.type == TabListIcon.IconType.STEVE) identifier--;*/
        UUID uuid = UUID.fromString(uuidbeginning + "10" + Mundo.toHexDigit(Mundo.divideNoRemainder(identifier, 10)) + (identifier % 10));
        WrappedGameProfile gameProfile = new WrappedGameProfile(uuid, "");
        if (action == EnumWrappers.PlayerInfoAction.ADD_PLAYER) {
            /*if (icon.type == TabListIcon.IconType.PLAYER) {
                Multimap<String, WrappedSignedProperty> propertyMultimap = gameProfile.getProperties();
                UtilSkinStorage.getProperties(icon.playerUUID).forEach(new Consumer<UtilSignedProperty>() {
                    @Override
                    public void accept(UtilSignedProperty utilSignedProperty) {
                        Mundo.debug(this, "BEfore: " + utilSignedProperty.value);
                        String changed = TabListManager.switchPlayerOfTexture(utilSignedProperty.value, player);
                        Mundo.debug(this, "AFter: " + changed);
                        propertyMultimap.put("textures", new WrappedSignedProperty(utilSignedProperty.name, utilSignedProperty.value, utilSignedProperty.signature));
                    }
                });
            } else if (icon.type == TabListIcon.IconType.URL) {
                //WrappedSignedProperty property = new WrappedSignedProperty("textures", Base64Coder.encodeString("{textures:{SKIN:{url:\"" + (icon.url) + "\"}}}"), "");
                Mundo.debug(this, "URL: " + icon.url);
                WrappedSignedProperty property = new WrappedSignedProperty("textures", TabListManager.getTextureValue(icon.url, player), null);
                Mundo.debug(this, "PROPERTY: " + property);
                gameProfile.getProperties().put("textures", property);
            } else if (icon.type == TabListIcon.IconType.SKINTEXTURE) {
                Mundo.debug(this, "SKINTEXTURE: " + icon.skinTexture);
                icon.skinTexture.retrieveSkinTextures(gameProfile.getProperties());
            }*/
            icon.retrieveSkinTextures(gameProfile.getProperties());
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
                    displayNames[column - 1][row - 1] = "";
                    latencies[column - 1][row - 1] = 5;
                    heads[column - 1][row - 1] = initialIcon;
                    sendPacket(column, row, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
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
                    heads[column - 1][row - 1] = initialIcon;
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
        for (int column = 1; column <= columns; column++)
            for (int row = 1; row <= rows; row++) {
                sendPacket(column, row, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
            }
    }

    public String getDisplayName(int column, int row) {
        return Mundo.isInRange(1, column, columns) && Mundo.isInRange(1, row, rows) ? displayNames[column - 1][row - 1] : null;
    }

    public Integer getLatency(int column, int row) {
        return Mundo.isInRange(1, column, columns) && Mundo.isInRange(1, row, rows) ? latencies[column - 1][row - 1] : null;
    }

    public SkinTexture getHead(int column, int row) {
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

    public void setHead(int column, int row, SkinTexture head) {
        if (Mundo.isInRange(1, column, columns) && Mundo.isInRange(1, row, rows)) {
            sendPacket(column, row, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
            heads[column - 1][row - 1] = head;
            sendPacket(column, row, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
        }
    }
}
