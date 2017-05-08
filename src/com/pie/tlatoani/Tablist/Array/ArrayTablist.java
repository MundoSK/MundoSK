package com.pie.tlatoani.Tablist.Array;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.Skin.Skin;
import com.pie.tlatoani.Tablist.Tab;
import com.pie.tlatoani.Tablist.Tablist;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Created by Tlatoani on 7/15/16.
 */
public class ArrayTablist {
    public final Tablist tablist;
    private final Tablist.Storage storage;

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

    public void maximize() {
        if (columns == 0) {
            columns = 4;
        } else {
            setColumns(4);
        }
        setRows(20);
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
                    int identifier = (((column - 1) * 20) + row);
                    String name = "MundoSK::" + (identifier < 10 ? "0" : "") + identifier;
                    UUID uuid = UUID.fromString(UUID_BEGINNING + "10" + Mundo.toHexDigit(Mundo.divideNoRemainder(identifier, 10)) + (identifier % 10));
                    Tab tab = new Tab(storage, name, uuid, "", 5, Tablist.DEFAULT_SKIN_TEXTURE, 0);
                    tab.sendPacket(tab.playerInfoPacket(EnumWrappers.PlayerInfoAction.ADD_PLAYER));
                    setTab(column, row, tab);
                }
        } else {
            for (int column = columns + 1; column <= this.columns; column++)
                for (int row = 1; row <= this.rows; row++) {
                    Tab tab = getTab(column, row);
                    tab.sendPacket(tab.playerInfoPacket(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER));
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
                    int identifier = (((column - 1) * 20) + row);
                    String name = "MundoSK::" + (identifier < 10 ? "0" : "") + identifier;
                    UUID uuid = UUID.fromString(UUID_BEGINNING + "10" + Mundo.toHexDigit(Mundo.divideNoRemainder(identifier, 10)) + (identifier % 10));
                    Tab tab = new Tab(storage, name, uuid, "", 5, Tablist.DEFAULT_SKIN_TEXTURE, 0);
                    tab.sendPacket(tab.playerInfoPacket(EnumWrappers.PlayerInfoAction.ADD_PLAYER));
                    setTab(column, row, tab);
                }
        } else {
            for (int column = 1; column <= this.columns; column++)
                for (int row = rows + 1; row <= this.rows; row++) {
                    Tab tab = getTab(column, row);
                    tab.sendPacket(tab.playerInfoPacket(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER));
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

    private void setTab(int column, int row, Tab tab) {
        tabs[column - 1][row - 1] = tab;
    }

}
