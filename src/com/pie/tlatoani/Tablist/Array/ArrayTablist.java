package com.pie.tlatoani.Tablist.Array;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.pie.tlatoani.ProtocolLib.PacketManager;
import com.pie.tlatoani.Skin.Skin;
import com.pie.tlatoani.Tablist.Tablist;
import com.pie.tlatoani.Util.Logging;
import com.pie.tlatoani.Util.MathUtil;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

/**
 * Created by Tlatoani on 7/15/16.
 */
public class ArrayTablist {
    private final Tablist tablist;
    private final String[][] displayNames = new String[4][20];
    private final int[][] latencies = new int[4][20];
    private final Skin[][] heads = new Skin[4][20];
    private final int[][] scores = new int[4][20];
    private final static String uuidbeginning = "10001000-1000-3000-8000-10001000";
    private int columns = 0;
    private int rows = 0;
    public Skin initialIcon = Tablist.DEFAULT_SKIN_TEXTURE;

    public ArrayTablist(Tablist tablist) {
        this.tablist = tablist;
    }

    public static int getViableRowAmount(int columns, int rows) {
        return columns == 1 ? MathUtil.limitToRange(1,  rows, 20) :
               columns == 2 ? MathUtil.limitToRange(11, rows, 20) :
               columns == 3 ? MathUtil.limitToRange(14, rows, 20) :
               columns == 4 ? MathUtil.limitToRange(16, rows, 20) :
                          0;
    }

    private void sendPacketToAll(int column, int row, EnumWrappers.PlayerInfoAction action) {
        sendPacket(column, row, action, tablist.players);
    }

    private void sendPacket(int column, int row, EnumWrappers.PlayerInfoAction action, Collection<Player> players) {
        Logging.debug(this, "SENDING PACKET col = " + column + ", row = " + row + " action = " + action + "players = " + players);
        int ping = latencies[column - 1][row - 1];
        String displayName = displayNames[column - 1][row - 1];
        Skin icon = heads[column - 1][row - 1];
        Logging.debug(this, "SP 1");
        WrappedChatComponent chatComponent = WrappedChatComponent.fromJson(Tablist.colorStringToJson(displayName));
        int identifier = (((column - 1) * 20) + row);
        UUID uuid = UUID.fromString(uuidbeginning + "10" + MathUtil.toHexDigit(MathUtil.divideNoRemainder(identifier, 10)) + (identifier % 10));
        WrappedGameProfile gameProfile = new WrappedGameProfile(uuid, "MundoSK::" + (identifier < 10 ? "0" : "") + identifier);
        Logging.debug(this, "SP 2");
        if (action == EnumWrappers.PlayerInfoAction.ADD_PLAYER) {
            if (icon == null) icon = Tablist.DEFAULT_SKIN_TEXTURE;
            gameProfile.getProperties().put(Skin.MULTIMAP_KEY, icon.toWrappedSignedProperty());
        }
        Logging.debug(this, "SP 3");
        PlayerInfoData playerInfoData = new PlayerInfoData(gameProfile, ping, EnumWrappers.NativeGameMode.NOT_SET, chatComponent);
        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
        packetContainer.getPlayerInfoDataLists().writeSafely(0, Collections.singletonList(playerInfoData));
        packetContainer.getPlayerInfoAction().writeSafely(0, action);
        Logging.debug(this, "SP 4");
        PacketManager.sendPacket(packetContainer, this, players);
    }

    private void sendScorePacketToAll(int column, int row) {
        sendScorePacket(column, row, tablist.players);
    }

    private void sendScorePacket(int column, int row, Collection<Player> players) {
        if (!tablist.areScoresEnabled()) return;
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_SCORE);
        int identifier = (((column - 1) * 20) + row);
        //if (identifier % 2 == 0) identifier += 79;
        packet.getStrings().writeSafely(0, "MundoSK::" + (identifier < 10 ? "0" : "") + identifier);
        packet.getStrings().writeSafely(1, Tablist.OBJECTIVE_NAME);
        packet.getIntegers().writeSafely(0, scores[column - 1][row - 1]);
        packet.getScoreboardActions().writeSafely(0, EnumWrappers.ScoreboardAction.CHANGE);
        PacketManager.sendPacket(packet, this, players);
    }

    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

    public void setColumns(int columns) {
        Logging.debug(this, "Got here, this.columns " + this.columns + ", this.rows " + this.rows + ", columns " + columns);
        columns = MathUtil.limitToRange(0, columns, 4);
        if (columns == this.columns) return;
        if (columns != 0) {
            Logging.debug(this, "Columns != 0");
            tablist.simpleTablist.clear();
            if (columns != 4 && !tablist.areAllPlayersHidden()) {
                Logging.debug(this, "Hiding all players");
                tablist.hideAllPlayers();
            }
        }
        if (columns > this.columns) {
            Logging.debug(this, "columns > this.columns");
            if (this.columns == 0) {
                this.rows = getViableRowAmount(columns, this.rows);
            } else {
                setRows(getViableRowAmount(columns, this.rows));
            }
            for (int column = this.columns + 1; column <= columns; column++)
                for (int row = 1; row <= this.rows; row++) {
                    Logging.debug(this, "col: " + column + ", ro: " + row);
                    displayNames[column - 1][row - 1] = "";
                    latencies[column - 1][row - 1] = 5;
                    heads[column - 1][row - 1] = initialIcon;
                    scores[column - 1][row - 1] = 0;
                    sendPacketToAll(column, row, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
                    if (tablist.areScoresEnabled()) sendScorePacketToAll(column, row);
                }
        } else {
            for (int column = columns + 1; column <= this.columns; column++)
                for (int row = 1; row <= this.rows; row++) {
                    displayNames[column - 1][row - 1] = "";
                    latencies[column - 1][row - 1] = 5;
                    heads[column - 1][row - 1] = null;
                    scores[column - 1][row - 1] = 0;
                    sendPacketToAll(column, row, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
                    if (tablist.areScoresEnabled()) {
                        sendScorePacketToAll(column, row);
                    }
                }
        }
        this.columns = columns;
        if (columns == 0) {
            this.rows = 0;
        }
    }

    public void setRows(int rows) {
        Logging.debug(this, "Got here, this.columns " + this.columns + ", this.rows " + this.rows + ", rows " + rows);
        rows = getViableRowAmount(columns, rows);
        if (rows == this.rows) return;
        if (!tablist.areAllPlayersHidden()) {
            Logging.debug(this, "Rows != 20, Hiding all players");
            tablist.hideAllPlayers();
        }
        if (rows > this.rows) {
            for (int column = 1; column <= this.columns; column++)
                for (int row = this.rows + 1; row <= rows; row++) {
                    displayNames[column - 1][row - 1] = "";
                    latencies[column - 1][row - 1] = 5;
                    heads[column - 1][row - 1] = initialIcon;
                    scores[column - 1][row - 1] = 0;
                    sendPacketToAll(column, row, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
                    sendScorePacketToAll(column, row);
                }
        } else {
            for (int column = 1; column <= this.columns; column++)
                for (int row = rows + 1; row <= this.rows; row++) {
                    displayNames[column - 1][row - 1] = "";
                    latencies[column - 1][row - 1] = 5;
                    heads[column - 1][row - 1] = null;
                    scores[column - 1][row - 1] = 0;
                    sendPacketToAll(column, row, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
                }
        }
        this.rows = rows;
    }

    public void addPlayers(Collection<Player> players) {
        for (int column = 1; column <= columns; column++)
            for (int row = 1; row <= rows; row++) {
                sendPacket(column, row, EnumWrappers.PlayerInfoAction.ADD_PLAYER, players);
                sendScorePacketToAll(column, row);
            }
    }

    public void removePlayers(Collection<Player> players) {
        for (int column = 1; column <= columns; column++)
            for (int row = 1; row <= rows; row++) {
                sendPacket(column, row, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER, players);
            }
    }

    public String getDisplayName(int column, int row) {
        return MathUtil.isInRange(1, column, columns) && MathUtil.isInRange(1, row, rows) ? displayNames[column - 1][row - 1] : null;
    }

    public int getLatency(int column, int row) {
        return MathUtil.isInRange(1, column, columns) && MathUtil.isInRange(1, row, rows) ? latencies[column - 1][row - 1] : 0;
    }

    public Skin getHead(int column, int row) {
        return MathUtil.isInRange(1, column, columns) && MathUtil.isInRange(1, row, rows) ? heads[column - 1][row - 1] : null;
    }

    public int getScore(int column, int row) {
        return MathUtil.isInRange(1, column, columns) && MathUtil.isInRange(1, row, rows) ? scores[column - 1][row - 1] : 0;
    }

    public void setDisplayName(int column, int row, String displayName) {
        if (MathUtil.isInRange(1, column, columns) && MathUtil.isInRange(1, row, rows)) {
            displayNames[column - 1][row - 1] = displayName;
            sendPacketToAll(column, row, EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME);
        }
    }

    public void setLatency(int column, int row, int ping) {
        if (MathUtil.isInRange(1, column, columns) && MathUtil.isInRange(1, row, rows)) {
            latencies[column - 1][row - 1] = ping;
            sendPacketToAll(column, row, EnumWrappers.PlayerInfoAction.UPDATE_LATENCY);
        }
    }

    public void setHead(int column, int row, Skin head) {
        if (MathUtil.isInRange(1, column, columns) && MathUtil.isInRange(1, row, rows)) {
            sendPacketToAll(column, row, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
            heads[column - 1][row - 1] = head;
            sendPacketToAll(column, row, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
        }
    }

    public void setScore(int column, int row, int ping) {
        if (MathUtil.isInRange(1, column, columns) && MathUtil.isInRange(1, row, rows)) {
            scores[column - 1][row - 1] = ping;
            sendScorePacketToAll(column, row);
        }
    }
}
