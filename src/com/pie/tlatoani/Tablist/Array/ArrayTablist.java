package com.pie.tlatoani.Tablist.Array;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;
import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.ProtocolLib.UtilPacketEvent;
import com.pie.tlatoani.Skin.Skin;
import com.pie.tlatoani.Tablist.Tablist;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Consumer;

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
        return columns == 1 ? Mundo.limitToRange(1,  rows, 20) :
               columns == 2 ? Mundo.limitToRange(11, rows, 20) :
               columns == 3 ? Mundo.limitToRange(14, rows, 20) :
               columns == 4 ? Mundo.limitToRange(16, rows, 20) :
                          0;
    }

    private void sendPacketToAll(int column, int row, EnumWrappers.PlayerInfoAction action) {
        sendPacket(column, row, action, tablist.players);
    }

    private void sendPacket(int column, int row, EnumWrappers.PlayerInfoAction action, Collection<Player> players) {
        int ping = latencies[column - 1][row - 1];
        String displayName = displayNames[column - 1][row - 1];
        Skin icon = heads[column - 1][row - 1];
        WrappedChatComponent chatComponent = WrappedChatComponent.fromJson(Tablist.colorStringToJson(displayName));
        int identifier = (((column - 1) * 20) + row);
        if (identifier % 2 == 0) identifier += 79;
        UUID uuid = UUID.fromString(uuidbeginning + "10" + Mundo.toHexDigit(Mundo.divideNoRemainder(identifier, 10)) + (identifier % 10));
        WrappedGameProfile gameProfile = new WrappedGameProfile(uuid, "MundoSKTablist::" + identifier);
        if (action == EnumWrappers.PlayerInfoAction.ADD_PLAYER) {
            if (icon == null) icon = Tablist.DEFAULT_SKIN_TEXTURE;
            icon.retrieveSkinTextures(gameProfile.getProperties());
        }
        PlayerInfoData playerInfoData = new PlayerInfoData(gameProfile, ping, EnumWrappers.NativeGameMode.NOT_SET, chatComponent);
        List<PlayerInfoData> playerInfoDatas = Collections.singletonList(playerInfoData);
        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
        packetContainer.getPlayerInfoDataLists().writeSafely(0, playerInfoDatas);
        packetContainer.getPlayerInfoAction().writeSafely(0, action);
        players.forEach(new Consumer<Player>() {
            @Override
            public void accept(Player player) {
                try {
                    UtilPacketEvent.protocolManager.sendServerPacket(player, packetContainer);
                } catch (InvocationTargetException e) {
                    Mundo.reportException(this, e);
                }
            }
        });
    }

    private void sendScorePacketToAll(int column, int row) {
        sendScorePacket(column, row, tablist.players);
    }

    private void sendScorePacket(int column, int row, Collection<Player> players) {
        if (!tablist.areScoresEnabled()) return;
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_SCORE);
        int identifier = (((column - 1) * 20) + row);
        if (identifier % 2 == 0) identifier += 79;
        packet.getStrings().writeSafely(0, "MundoSKTablist::" + identifier);
        packet.getStrings().writeSafely(1, Tablist.OBJECTIVE_NAME);
        packet.getIntegers().writeSafely(0, scores[column - 1][row - 1]);
        packet.getScoreboardActions().writeSafely(0, EnumWrappers.ScoreboardAction.CHANGE);
        players.forEach(new Consumer<Player>() {
            @Override
            public void accept(Player player) {
                try {
                    UtilPacketEvent.protocolManager.sendServerPacket(player, packet);
                } catch (InvocationTargetException e) {
                    Mundo.reportException(this, e);
                }
            }
        });
    }

    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

    public void setColumns(int columns) {
        columns = Mundo.limitToRange(0, columns, 4);
        if (columns == 0) {
            return;
        } else {
            tablist.simpleTablist.clear();
            tablist.hideAllPlayers();
        }
        if (columns > this.columns) {
            setRows(getViableRowAmount(columns, this.rows));
        }
        if (columns > this.columns) {
            for (int column = this.columns + 1; column <= columns; column++)
                for (int row = 1; row <= this.rows; row++) {
                    displayNames[column - 1][row - 1] = "";
                    latencies[column - 1][row - 1] = 5;
                    heads[column - 1][row - 1] = initialIcon;
                    scores[column - 1][row - 1] = 0;
                    sendPacketToAll(column, row, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
                    sendScorePacketToAll(column, row);
                }
        } else if (columns < this.columns) {
            for (int column = columns + 1; column <= this.columns; column++)
                for (int row = 1; row <= this.rows; row++) {
                    displayNames[column - 1][row - 1] = null;
                    latencies[column - 1][row - 1] = 5;
                    heads[column - 1][row - 1] = null;
                    scores[column - 1][row - 1] = 0;
                    sendPacketToAll(column, row, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
                    sendScorePacketToAll(column, row);
                }
        }
        this.columns = columns;
    }

    public void setRows(int rows) {
        Mundo.debug(this, "Got here, this.columns " + this.columns + ", this.rows " + this.rows + ", rows " + rows);
        rows = getViableRowAmount(columns, rows);
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
        } else if (rows < this.rows) {
            for (int column = 1; column <= this.columns; column++)
                for (int row = rows + 1; row <= this.rows; row++) {
                    displayNames[column - 1][row - 1] = null;
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
        return Mundo.isInRange(1, column, columns) && Mundo.isInRange(1, row, rows) ? displayNames[column - 1][row - 1] : null;
    }

    public int getLatency(int column, int row) {
        return Mundo.isInRange(1, column, columns) && Mundo.isInRange(1, row, rows) ? latencies[column - 1][row - 1] : 0;
    }

    public Skin getHead(int column, int row) {
        return Mundo.isInRange(1, column, columns) && Mundo.isInRange(1, row, rows) ? heads[column - 1][row - 1] : null;
    }

    public int getScore(int column, int row) {
        return Mundo.isInRange(1, column, columns) && Mundo.isInRange(1, row, rows) ? scores[column - 1][row - 1] : 0;
    }

    public void setDisplayName(int column, int row, String displayName) {
        if (Mundo.isInRange(1, column, columns) && Mundo.isInRange(1, row, rows)) {
            displayNames[column - 1][row - 1] = displayName;
            sendPacketToAll(column, row, EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME);
        }
    }

    public void setLatency(int column, int row, int ping) {
        if (Mundo.isInRange(1, column, columns) && Mundo.isInRange(1, row, rows)) {
            latencies[column - 1][row - 1] = ping;
            sendPacketToAll(column, row, EnumWrappers.PlayerInfoAction.UPDATE_LATENCY);
        }
    }

    public void setHead(int column, int row, Skin head) {
        if (Mundo.isInRange(1, column, columns) && Mundo.isInRange(1, row, rows)) {
            sendPacketToAll(column, row, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
            heads[column - 1][row - 1] = head;
            sendPacketToAll(column, row, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
        }
    }

    public void setScore(int column, int row, int ping) {
        if (Mundo.isInRange(1, column, columns) && Mundo.isInRange(1, row, rows)) {
            scores[column - 1][row - 1] = ping;
            sendScorePacketToAll(column, row);
        }
    }
}
