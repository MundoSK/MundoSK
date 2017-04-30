package com.pie.tlatoani.Tablist.Array;

import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.Skin.Skin;
import com.pie.tlatoani.Tablist.Tab.BaseTab;
import com.pie.tlatoani.Tablist.Tab.PersonalizableTab;
import com.pie.tlatoani.Tablist.Tab.Tab;
import com.pie.tlatoani.Tablist.Tab.VisibleByDefaultTab;
import com.pie.tlatoani.Tablist.Tablist;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Created by Tlatoani on 7/15/16.
 */
public class ArrayTablist {
    public final Tablist tablist;
    private final Tablist.Storage storage;

    /*private final String[][] displayNames = new String[4][20];
    private final int[][] latencies = new int[4][20];
    private final Skin[][] heads = new Skin[4][20];
    private final int[][] scores = new int[4][20];*/

    public final static String UUID_BEGINNING = "10001000-1000-3000-8000-10001000";
    private int columns = 0;
    private int rows = 0;
    public Skin initialIcon = Tablist.DEFAULT_SKIN_TEXTURE;

    private final Tab[][] tabs = new Tab[4][20];



    public ArrayTablist(Tablist.Storage storage) {
        this.tablist = storage.tablist;
        this.storage = storage;
    }

    public static int getViableRowAmount(int columns, int rows) {
        return columns == 1 ? Mundo.limitToRange(1,  rows, 20) :
               columns == 2 ? Mundo.limitToRange(11, rows, 20) :
               columns == 3 ? Mundo.limitToRange(14, rows, 20) :
               columns == 4 ? Mundo.limitToRange(16, rows, 20) :
                          0;
    }

    /*
    private void sendPacketToAll(int column, int row, EnumWrappers.PlayerInfoAction action) {
        sendPacket(column, row, action, tablist.players);
    }

    private void sendPacket(int column, int row, EnumWrappers.PlayerInfoAction action, Collection<Player> players) {
        int ping = latencies[column - 1][row - 1];
        String displayName = displayNames[column - 1][row - 1];
        Skin icon = heads[column - 1][row - 1];
        WrappedChatComponent chatComponent = WrappedChatComponent.fromJson(Tablist.colorStringToJson(displayName));
        int identifier = (((column - 1) * 20) + row);
        //if (identifier % 2 == 0) identifier += 79;
        UUID uuid = UUID.fromString(UUID_BEGINNING + "10" + Mundo.toHexDigit(Mundo.divideNoRemainder(identifier, 10)) + (identifier % 10));
        WrappedGameProfile gameProfile = new WrappedGameProfile(uuid, "MundoSK::" + (identifier < 10 ? "0" : "") + identifier);
        if (action == EnumWrappers.PlayerInfoAction.ADD_PLAYER) {
            if (icon == null) icon = Tablist.DEFAULT_SKIN_TEXTURE;
            icon.retrieveSkinTextures(gameProfile.getProperties());
        }
        PlayerInfoData playerInfoData = new PlayerInfoData(gameProfile, ping, EnumWrappers.NativeGameMode.NOT_SET, chatComponent);
        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
        packetContainer.getPlayerInfoDataLists().writeSafely(0, Collections.singletonList(playerInfoData));
        packetContainer.getPlayerInfoAction().writeSafely(0, action);
        try {
            for (Player player : players) {
                UtilPacketEvent.protocolManager.sendServerPacket(player, packetContainer);
            }
        } catch (InvocationTargetException e) {
            Mundo.reportException(this, e);
        }
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
        for (Player player : players) {
            try {
                UtilPacketEvent.protocolManager.sendServerPacket(player, packet);
            } catch (InvocationTargetException e) {
                Mundo.reportException(this, e);
            }
        }
    }*/

    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

    public void setColumns(int columns) {
        Mundo.debug(this, "Got here, this.columns " + this.columns + ", this.rows " + this.rows + ", columns " + columns);
        columns = Mundo.limitToRange(0, columns, 4);
        if (columns == this.columns) return;
        if (columns > this.columns) {
            if (this.columns == 0) {
                this.rows = getViableRowAmount(columns, this.rows);
            } else {
                setRows(getViableRowAmount(columns, this.rows));
            }
            for (int column = this.columns + 1; column <= columns; column++)
                for (int row = 1; row <= this.rows; row++) {
                    /*displayNames[column - 1][row - 1] = "";
                    latencies[column - 1][row - 1] = 5;
                    heads[column - 1][row - 1] = initialIcon;
                    scores[column - 1][row - 1] = 0;
                    sendPacketToAll(column, row, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
                    if (tablist.areScoresEnabled()) sendScorePacketToAll(column, row);*/
                    int identifier = (((column - 1) * 20) + row);
                    String name = "MundoSK::" + (identifier < 10 ? "0" : "") + identifier;
                    UUID uuid = UUID.fromString(UUID_BEGINNING + "10" + Mundo.toHexDigit(Mundo.divideNoRemainder(identifier, 10)) + (identifier % 10));
                    Tab tab = new BaseTab(storage, name, uuid, "", (byte) 5, initialIcon, 0);
                    tab.send(tab.showPacket());
                    setTab(column, row, tab);
                }
        } else {
            for (int column = columns + 1; column <= this.columns; column++)
                for (int row = 1; row <= this.rows; row++) {
                    /*displayNames[column - 1][row - 1] = "";
                    latencies[column - 1][row - 1] = 5;
                    heads[column - 1][row - 1] = null;
                    scores[column - 1][row - 1] = 0;
                    sendPacketToAll(column, row, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
                    if (tablist.areScoresEnabled()) {
                        sendScorePacketToAll(column, row);
                    }*/
                    Tab tab = getTab(column, row);
                    tab.send(tab.hidePacket());
                    tab.setScore(null);
                    setTab(column, row, null);
                }
        }
        this.columns = columns;
        if (columns == 0) {
            this.rows = 0;
            storage.arrayTablistOptional = Optional.empty();
        } else {
            Mundo.debug(this, "Columns != 0");
            if (columns == 4 && rows == 20) {
                tablist.showAllPlayers();
            } else {
                tablist.hideAllPlayers();
            }
        }
    }

    public void setRows(int rows) {
        Mundo.debug(this, "Got here, this.columns " + this.columns + ", this.rows " + this.rows + ", rows " + rows);
        rows = getViableRowAmount(columns, rows);
        if (rows == this.rows) {
            return;
        }
        if (rows > this.rows) {
            for (int column = 1; column <= this.columns; column++)
                for (int row = this.rows + 1; row <= rows; row++) {
                    /*displayNames[column - 1][row - 1] = "";
                    latencies[column - 1][row - 1] = 5;
                    heads[column - 1][row - 1] = initialIcon;
                    scores[column - 1][row - 1] = 0;
                    sendPacketToAll(column, row, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
                    sendScorePacketToAll(column, row);*/
                    int identifier = (((column - 1) * 20) + row);
                    String name = "MundoSK::" + (identifier < 10 ? "0" : "") + identifier;
                    UUID uuid = UUID.fromString(UUID_BEGINNING + "10" + Mundo.toHexDigit(Mundo.divideNoRemainder(identifier, 10)) + (identifier % 10));
                    Tab tab = new BaseTab(storage, name, uuid, "", (byte) 5, initialIcon, 0);
                    tab.send(tab.showPacket());
                    setTab(column, row, tab);
                }
        } else {
            for (int column = 1; column <= this.columns; column++)
                for (int row = rows + 1; row <= this.rows; row++) {
                    /*displayNames[column - 1][row - 1] = "";
                    latencies[column - 1][row - 1] = 5;
                    heads[column - 1][row - 1] = null;
                    scores[column - 1][row - 1] = 0;
                    sendPacketToAll(column, row, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);*/
                    Tab tab = getTab(column, row);
                    tab.send(tab.hidePacket());
                    tab.setScore(null);
                    setTab(column, row, null);
                }
        }
        this.rows = rows;
        if (columns == 4 && rows == 20) {
            tablist.showAllPlayers();
        } else {
            tablist.hideAllPlayers();
        }
    }

    public void maximize() {
        if (columns == 0) {
            columns = 4;
        } else {
            setColumns(4);
        }
        setRows(20);
    }

    /*
    public void addPlayers(Collection<Player> players) {
        for (int column = 1; column <= columns; column++)
            for (int row = 1; row <= rows; row++) {
                //sendPacket(column, row, EnumWrappers.PlayerInfoAction.ADD_PLAYER, players);
                //sendScorePacketToAll(column, row);
                Tab tab = getOldTab(column, row);
                for (Player player : players) {
                    tab.add(player);
                }
            }
    }*/

    public void addPlayer(Player player) {
        for (int column = 1; column <= columns; column++)
            for (int row = 1; row <= rows; row++) {
                Tab tab = getTab(column, row);
                tab.send(tab.showPacket(), player);
            }
    }

    public void removePlayer(Player player) {
        for (int column = 1; column <= columns; column++)
            for (int row = 1; row <= rows; row++) {
                //sendPacket(column, row, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER, players);
                Tab tab = getTab(column, row);
                if (tab instanceof Tab.OldPersonalizable) {
                    ((Tab.OldPersonalizable) tab).removePlayer(player);
                } else {
                    tab.send(tab.hidePacket(), player);
                }
            }
    }

    public Tab getTab(int column, int row) {
        if (!Mundo.isInRange(1, column, columns)) {
            throw new IllegalArgumentException("Column = " + column + " out of range 1 to " + columns);
        }
        if (!Mundo.isInRange(1, row, rows)) {
            throw new IllegalArgumentException("Row = " + row + " out of range 1 to " + rows);

        }
        return tabs[column - 1][row - 1];
    }

    public PersonalizableTab forcePersonalizable(int column, int row) {
        Tab tab = getTab(column, row);
        if (tab instanceof ArrayPersonalizable || tab == null) {
            return (PersonalizableTab) tab;
        }
        PersonalizableTab personalizableTab = new ArrayPersonalizable((BaseTab) tab);
        setTab(column, row, personalizableTab);
        return personalizableTab;
    }

    private void setTab(int column, int row, Tab tab) {
        tabs[column - 1][row - 1] = tab;
    }

    //All code after here will be removed

    /*
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
    }*/

    //Tab Class Modifications

    public static class ArrayPersonalizable extends VisibleByDefaultTab {

        public ArrayPersonalizable(BaseTab base) {
            super(base);
        }

        @Override
        public void hideFor(Player player) {
            throw new UnsupportedOperationException("ArrayTablist does not allow you to hide individual tabs!");
        }
    }
}
