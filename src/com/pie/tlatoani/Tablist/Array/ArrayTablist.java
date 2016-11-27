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
        Mundo.debug(this, "INTERIOR CHECKPOINT 1");
        int ping = latencies[column - 1][row - 1];
        Mundo.debug(this, "INTERIOR CHECKPOINT 2");
        String displayName = displayNames[column - 1][row - 1];
        Mundo.debug(this, "INTERIOR CHECKPOINT 3");
        Skin icon = heads[column - 1][row - 1];
        Mundo.debug(this, "INTERIOR CHECKPOINT 4");
        WrappedChatComponent chatComponent = WrappedChatComponent.fromJson(Tablist.colorStringToJson(displayName));
        Mundo.debug(this, "INTERIOR CHECKPOINT 5");
        int identifier = (((column - 1) * 20) + row);
        Mundo.debug(this, "INTERIOR CHECKPOINT 6");
        //if (identifier % 2 == 0) identifier += 79;
        UUID uuid = UUID.fromString(uuidbeginning + "10" + Mundo.toHexDigit(Mundo.divideNoRemainder(identifier, 10)) + (identifier % 10));
        Mundo.debug(this, "INTERIOR CHECKPOINT 7");
        WrappedGameProfile gameProfile = new WrappedGameProfile(uuid, "MundoSK::" + (identifier < 10 ? "0" : "") + identifier);
        Mundo.debug(this, "INTERIOR CHECKPOINT 8");
        if (action == EnumWrappers.PlayerInfoAction.ADD_PLAYER) {
            Mundo.debug(this, "INTERIOR CHECKPOINT 9");
            if (icon == null) icon = Tablist.DEFAULT_SKIN_TEXTURE;
            Mundo.debug(this, "INTERIOR CHECKPOINT 10");
            icon.retrieveSkinTextures(gameProfile.getProperties());
            Mundo.debug(this, "INTERIOR CHECKPOINT 11");
        }
        Mundo.debug(this, "INTERIOR CHECKPOINT 12");
        PlayerInfoData playerInfoData = new PlayerInfoData(gameProfile, ping, EnumWrappers.NativeGameMode.NOT_SET, chatComponent);
        Mundo.debug(this, "INTERIOR CHECKPOINT 13");
        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
        Mundo.debug(this, "INTERIOR CHECKPOINT 14");
        packetContainer.getPlayerInfoDataLists().writeSafely(0, Collections.singletonList(playerInfoData));
        Mundo.debug(this, "INTERIOR CHECKPOINT 15");
        packetContainer.getPlayerInfoAction().writeSafely(0, action);
        Mundo.debug(this, "INTERIOR CHECKPOINT 16");
        try {
            Mundo.debug(this, "INTERIOR CHECKPOINT 17");
            for (Player player : players) {
                Mundo.debug(this, "INTERIOR CHECKPOINT 18");
                UtilPacketEvent.protocolManager.sendServerPacket(player, packetContainer);
                Mundo.debug(this, "INTERIOR CHECKPOINT 19");
            }
        } catch (InvocationTargetException e) {
            Mundo.debug(this, "INTERIOR CHECKPOINT 20");
            Mundo.reportException(this, e);
            Mundo.debug(this, "INTERIOR CHECKPOINT 21");
        }
        Mundo.debug(this, "INTERIOR CHECKPOINT 22");
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
        Mundo.debug(this, "Got here, this.columns " + this.columns + ", this.rows " + this.rows + ", columns " + columns);
        columns = Mundo.limitToRange(0, columns, 4);
        Mundo.debug(this, "CHECKPOINT 1");
        if (columns == this.columns) return;
        Mundo.debug(this, "CHECKPOINT 2");
        if (columns != 0) {
            tablist.simpleTablist.clear();
            if (!tablist.areAllPlayersHidden()) {
                tablist.hideAllPlayers();
            }
        }
        Mundo.debug(this, "CHECKPOINT 3");
        if (columns > this.columns) {
            if (this.columns == 0) {
                this.rows = getViableRowAmount(columns, this.rows);
            } else {
                setRows(getViableRowAmount(columns, this.rows));
            }
            for (int column = this.columns + 1; column <= columns; column++)
                for (int row = 1; row <= this.rows; row++) {
                    displayNames[column - 1][row - 1] = "";
                    latencies[column - 1][row - 1] = 5;
                    heads[column - 1][row - 1] = initialIcon;
                    scores[column - 1][row - 1] = 0;
                    sendPacketToAll(column, row, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
                    if (tablist.areScoresEnabled()) sendScorePacketToAll(column, row);
                }
        } else {
            Mundo.debug(this, "CHECKPOINT 4");
            for (int column = columns + 1; column <= this.columns; column++)
                for (int row = 1; row <= this.rows; row++) {
                    Mundo.debug(this, "Removing tab " + column + "," + row);
                    displayNames[column - 1][row - 1] = "";
                    latencies[column - 1][row - 1] = 5;
                    heads[column - 1][row - 1] = null;
                    scores[column - 1][row - 1] = 0;
                    Mundo.debug(this, "CHECKPOINT 4.1");
                    sendPacketToAll(column, row, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
                    Mundo.debug(this, "CHECKPOINT 4.2");
                    if (tablist.areScoresEnabled()) {
                        Mundo.debug(this, "CHECKPOINT 4.3");
                        sendScorePacketToAll(column, row);
                        Mundo.debug(this, "CHECKPOINT 4.4");
                    }
                    Mundo.debug(this, "CHECKPOINT 4.5");
                }
            Mundo.debug(this, "CHECKPOINT 5");
        }
        Mundo.debug(this, "CHECKPOINT 6");
        this.columns = columns;
        Mundo.debug(this, "CHECKPOINT 7");
        if (columns == 0) {
            Mundo.debug(this, "CHECKPOINT 8");
            this.rows = 0;
            Mundo.debug(this, "CHECKPOINT 9");
        }
        Mundo.debug(this, "CHECKPOINT 10");
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
