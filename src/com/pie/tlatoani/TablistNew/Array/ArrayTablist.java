package com.pie.tlatoani.TablistNew.Array;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.Skin.Skin;
import com.pie.tlatoani.TablistNew.Player.PlayerTablist;
import com.pie.tlatoani.TablistNew.Tab;
import com.pie.tlatoani.TablistNew.SupplementaryTablist;
import com.pie.tlatoani.TablistNew.Tablist;
import com.pie.tlatoani.Util.Logging;
import com.pie.tlatoani.Util.MathUtil;

import java.util.*;

/**
 * Created by Tlatoani on 7/15/16.
 */
public class ArrayTablist implements SupplementaryTablist {
    public final Tablist tablist;
    private final PlayerTablist playerTablist;

    public final static String UUID_BEGINNING = "10001000-1000-3000-8000-10001000";
    private int columns;
    private int rows;
    public Skin initialIcon;

    private final Tab[][] tabs = new Tab[4][20];

    public ArrayTablist(PlayerTablist playerTablist, int columns, int rows, Skin initialIcon) {
        this.tablist = playerTablist.tablist;
        this.playerTablist = playerTablist;
        this.columns = MathUtil.limitToRange(1, columns, 4);
        this.rows = getViableRowAmount(this.columns, rows);
        this.initialIcon = initialIcon;
        addTabs(1, this.columns, 1, this.rows);
        changeToIdealPlayerVisibility();
    }

    public static int getViableRowAmount(int columns, int rows) {
        return columns == 1 ? MathUtil.limitToRange(1,  rows, 20) :
               columns == 2 ? MathUtil.limitToRange(11, rows, 20) :
               columns == 3 ? MathUtil.limitToRange(14, rows, 20) :
               columns == 4 ? MathUtil.limitToRange(16, rows, 20) :
                          0;
    }

    public void maximize() {
        setColumns(4);
        setRows(20);
    }

    public Tab getTab(int column, int row) {
        if (!MathUtil.isInRange(1, column, columns)) {
            throw new IllegalArgumentException("Column = " + column + " out of range 1 to " + columns);
        }
        if (!MathUtil.isInRange(1, row, rows)) {
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
        Logging.debug(this, "Got here, this.columns " + this.columns + ", this.rows " + this.rows + ", columns " + columns);
        columns = MathUtil.limitToRange(1, columns, 4);
        if (columns == this.columns) {
            return;
        } else if (columns > this.columns) {
            setRows(getViableRowAmount(columns, this.rows));
            addTabs(this.columns + 1, columns, 1, this.rows);
        } else {
            removeTabs(columns + 1, this.columns, 1, this.rows);
        }
        this.columns = columns;
        changeToIdealPlayerVisibility();
    }

    public void setRows(int rows) {
        Logging.debug(this, "Got here, this.columns " + this.columns + ", this.rows " + this.rows + ", rows " + rows);
        rows = getViableRowAmount(columns, rows);
        if (rows == this.rows) {
            return;
        } else if (rows > this.rows) {
            addTabs(1, this.columns, this.rows + 1, rows);
        } else {
            removeTabs(1, this.columns, rows + 1, this.rows);
        }
        this.rows = rows;
        changeToIdealPlayerVisibility();
    }

    //Utility Methods

    private void setTab(int column, int row, Tab tab) {
        tabs[column - 1][row - 1] = tab;
    }

    private void addTabs(int columnMin, int columnMax, int rowMin, int rowMax) {
        for (int column = columnMin; column <= columnMax; column++)
            for (int row = rowMin; row <= rowMax; row++) {
                int identifier = (((column - 1) * 20) + row);
                String name = "MundoSK::" + (identifier < 10 ? "0" : "") + identifier;
                UUID uuid = UUID.fromString(UUID_BEGINNING + "10" + MathUtil.toHexDigit(identifier / 10) + (identifier % 10));
                Tab tab = new Tab(tablist.target, name, uuid, "", 5, Tablist.DEFAULT_SKIN_TEXTURE, 0);
                tab.sendPacket(tab.playerInfoPacket(EnumWrappers.PlayerInfoAction.ADD_PLAYER));
                setTab(column, row, tab);
            }
    }

    private void removeTabs(int columnMin, int columnMax, int rowMin, int rowMax) {
        for (int column = columnMin; column <= columnMax; column++)
            for (int row = rowMin; row <= rowMax; row++) {
                Tab tab = getTab(column, row);
                tab.sendPacket(tab.playerInfoPacket(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER));
                tab.setScore(null);
                setTab(column, row, null);
            }
    }

    private void changeToIdealPlayerVisibility() {
        if (columns == 4 && rows == 20) {
            playerTablist.showAllPlayers();
        } else {
            playerTablist.hideAllPlayers();
        }
    }

    @Override
    public void disable() {
        removeTabs(1, columns, 1, rows);
    }

    @Override
    public boolean allowExternalPlayerTabModification() {
        return false;
    }
}
