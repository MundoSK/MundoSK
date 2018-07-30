package com.pie.tlatoani.Tablist.Array;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.pie.tlatoani.Skin.Skin;
import com.pie.tlatoani.Tablist.Player.PlayerTablist;
import com.pie.tlatoani.Tablist.SupplementaryTablist;
import com.pie.tlatoani.Tablist.Tab;
import com.pie.tlatoani.Tablist.Tablist;
import com.pie.tlatoani.Core.Static.Logging;
import com.pie.tlatoani.Core.Static.MathUtil;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Function;

/**
 * Created by Tlatoani on 7/15/16.
 * A SupplementaryTablist that allows modification of the tablist as a grid of tabs,
 * which will be referred to as array tabs.
 * There can be between 1 and 4 columns inclusive.
 * See {@link #getViableRowAmount(int, int)} for information about restrictions on rows.
 */
public class ArrayTablist implements SupplementaryTablist<ArrayTablist> {
    public final Tablist tablist;
    private final PlayerTablist playerTablist;

    public static final String UUID_BEGINNING = "10001000-1000-3000-8000-10001000";
    private int columns;
    private int rows;

    private final Tab[][] tabs = new Tab[4][20];

    /**
     * A utility to simplify setting a tablist's {@link SupplementaryTablist} to a new ArrayTablist.
     * @param columns The amount of columns to start with (1 - 4)
     * @param rows The amount of rows to start with (see {@link #getViableRowAmount(int, int)})
     * @return A {@link Function} that will accept a {@link PlayerTablist} and return a new ArrayTablist
     */
    public static Function<PlayerTablist, ArrayTablist> create(int columns, int rows) {
        return playerTablist -> new ArrayTablist(playerTablist, columns, rows);
    }

    /**
     * Initializes an ArrayTablist to be owned by the {@link Tablist} owning {@code playerTablist}.
     * @param playerTablist
     * @param columns The amount of columns to start with (1 - 4)
     * @param rows The amount of rows to start with (see {@link #getViableRowAmount(int, int)})
     */
    public ArrayTablist(PlayerTablist playerTablist, int columns, int rows) {
        this.tablist = playerTablist.tablist;
        this.playerTablist = playerTablist;
        this.columns = MathUtil.limitToRange(1, columns, 4);
        this.rows = getViableRowAmount(this.columns, rows);
        addTabs(1, this.columns, 1, this.rows, null);
        changeToIdealPlayerVisibility();
    }

    /**
     * Returns an amount of rows that is possible to have given that there are {@code columns}.
     * If {@code rows} already works then it is returned.
     * If {@code rows} is too many, then {@code 20} is returned.
     * If {@code rows} is too little, then the minimum possible amount of rows is returned.
     * @param columns The amount of columns
     * @param rows The possibly invalid amount of rows to make valid
     * @return An valid amount of rows
     */
    public static int getViableRowAmount(int columns, int rows) {
        return columns == 1 ? MathUtil.limitToRange(1,  rows, 20) :
               columns == 2 ? MathUtil.limitToRange(11, rows, 20) :
               columns == 3 ? MathUtil.limitToRange(14, rows, 20) :
               columns == 4 ? MathUtil.limitToRange(16, rows, 20) :
                          0;
    }

    /**
     * @param column The column in which the desired array tab is located
     * @param row The row in which the desired array tab is located
     * @return The array tab in column {@code column} and row {@code row}
     * @throws IllegalArgumentException If {@code column} isn't between {@code 1} and {@link #getColumns()} inclusive
     * or {@code row} isn't between {@code 1} and {@link #getRows()} inclusive
     */
    public Tab getTab(int column, int row) {
        if (!MathUtil.isInRange(1, column, columns)) {
            throw new IllegalArgumentException("Column = " + column + " out of range 1 to " + columns);
        }
        if (!MathUtil.isInRange(1, row, rows)) {
            throw new IllegalArgumentException("Row = " + row + " out of range 1 to " + rows);

        }
        return tabs[column - 1][row - 1];
    }

    /**
     * @return The amount of columns in the tablist
     */
    public int getColumns() {
        return columns;
    }

    /**
     * @return The amount of rows in the tablist
     */
    public int getRows() {
        return rows;
    }

    /**
     * Changes the amount of columns in the tablist to be {@code columns} limited between 1 and 4 inclusive
     * @param columns The new amount of columns in the tablist
     */
    public void setColumns(int columns) {
        setColumns(columns, null);
    }

    /**
     * Changes the amount of columns in the tablist to be {@code columns} limited between 1 and 4 inclusive
     * @param columns The new amount of columns in the tablist
     * @param icon The icon of the created tabs (or null for an empty icon) if tabs are created
     */
    public void setColumns(int columns, @Nullable Skin icon) {
        Logging.debug(this, "Got here, this.columns " + this.columns + ", this.rows " + this.rows + ", columns " + columns);
        columns = MathUtil.limitToRange(1, columns, 4);
        if (columns == this.columns) {
            return;
        } else if (columns > this.columns) {
            setRows(getViableRowAmount(columns, this.rows), icon);
            addTabs(this.columns + 1, columns, 1, this.rows, icon);
        } else {
            removeTabs(columns + 1, this.columns, 1, this.rows);
        }
        this.columns = columns;
        changeToIdealPlayerVisibility();
    }

    /**
     * Changes the amount of rows in the tablist to be
     * {@code rows} limited according to {@link #getViableRowAmount(int, int)}
     * @param rows The new amount of rows in the tablist
     */
    public void setRows(int rows) {
        setRows(rows, null);
    }

    /**
     * Changes the amount of columns in the tablist to be {@code rows} limited according to {@link #getViableRowAmount(int, int)}
     * @param rows The new amount of rows in the tablist
     * @param icon The icon of the created tabs (or null for an empty icon) if tabs are created
     */
    public void setRows(int rows, @Nullable Skin icon) {
        Logging.debug(this, "Got here, this.columns " + this.columns + ", this.rows " + this.rows + ", rows " + rows);
        rows = getViableRowAmount(columns, rows);
        if (rows == this.rows) {
            return;
        } else if (rows > this.rows) {
            addTabs(1, this.columns, this.rows + 1, rows, icon);
        } else {
            removeTabs(1, this.columns, rows + 1, this.rows);
        }
        this.rows = rows;
        changeToIdealPlayerVisibility();
    }

    //Utility Methods

    /**
     * Creates a {@link Tab} with the name and {@link UUID} necessary
     * in order to maintain the correct ordering of tabs in the tablist
     * according to {@code column} and {@code row} given that all other array tabs are created using this method.
     * @param column The column at which the returned {@link Tab} will be located
     * @param row The row at which the returned {@link Tab} will be located
     * @return A new {@link Tab}
     */
    private Tab createTab(int column, int row, Skin icon) {
        String identifier = (((column - 1) * 2) + (row / 10)) + "" + (row % 10);
        String name = "#MundoSK::" + identifier;
        UUID uuid = UUID.fromString(UUID_BEGINNING + "10" + identifier);
        return new Tab(tablist, name, uuid, null, null, icon, null);
    }

    /**
     * Registers {@code tab} as representing the tab at column {@code column} and row {@code row}
     * @param column The column to specify {@code tab} as being in
     * @param row The row to specify {@code tab} as being in
     * @param tab The Tab to register
     */
    private void setTab(int column, int row, Tab tab) {
        tabs[column - 1][row - 1] = tab;
    }

    /**
     * Adds tabs between the locations ({@code columnMin}, {@code rowMin})
     * and ({@code columnMax)}, {@code rowMax}) inclusive in the tablist.
     * @param columnMin The leftmost column to add tabs to
     * @param columnMax The righmost column to add tabs to
     * @param rowMin The uppermost row to add tabs to
     * @param rowMax The lowermost row to add tabs to
     * @param icon The icon to give the created tabs, or null for them to have an empty icon
     */
    private void addTabs(int columnMin, int columnMax, int rowMin, int rowMax, @Nullable Skin icon) {
        Logging.debug(this, "Adding Tabs, columnMin = " + columnMin + ", columnMax = " + columnMax + ", rowMin = " + rowMin + ", rowMax = " + rowMax);
        for (int column = columnMin; column <= columnMax; column++)
            for (int row = rowMin; row <= rowMax; row++) {
                Logging.debug(this, "Adding Tab, column = " + column + ", row = " + row);
                Tab tab = createTab(column, row, icon);
                tablist.sendPacket(tab.playerInfoPacket(EnumWrappers.PlayerInfoAction.ADD_PLAYER), this);
                setTab(column, row, tab);
            }
    }

    /**
     * Removes the tabs between the locations ({@code columnMin}, {@code rowMin})
     * and ({@code columnMax)}, {@code rowMax}) inclusive in the tablist.
     * @param columnMin The leftmost column to remove tabs from
     * @param columnMax The righmost column to remove tabs from
     * @param rowMin The uppermost row to remove tabs from
     * @param rowMax The lowermost row to remove tabs from
     */
    private void removeTabs(int columnMin, int columnMax, int rowMin, int rowMax) {
        Logging.debug(this, "Removing Tabs, columnMin = " + columnMin + ", columnMax = " + columnMax + ", rowMin = " + rowMin + ", rowMax = " + rowMax);
        for (int column = columnMin; column <= columnMax; column++)
            for (int row = rowMin; row <= rowMax; row++) {
                Logging.debug(this, "Removing Tab, column = " + column + ", row = " + row);
                Tab tab = getTab(column, row);
                tablist.sendPacket(tab.playerInfoPacket(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER), this);
                tab.setScore(null);
                setTab(column, row, null);
            }
    }

    /**
     * If there are 4 columns and 20 rows,
     * this method will call {@link PlayerTablist#clearModifications()} on the {@link PlayerTablist}.
     * Otherwise, it will call {@link PlayerTablist#hideAllPlayers()} on the {@link PlayerTablist}.
     * This is because if there are not both 4 columns and 20 rows, =
     * having players be visible means that their tabs will show in addition to the array tab.
     * However, if there are 4 columns and 20 rows, then all players should be visible and displayed as default
     * in order to minimize packet sending/modification as well as prevent bugs that occur
     * when players must be shown and removed repeatedly.
     */
    private void changeToIdealPlayerVisibility() {
        if (columns == 4 && rows == 20) {
            Logging.debug(this, "Columns = " + columns + ", Rows = " + rows + ", Showing all players");
            playerTablist.clearModifications();
        } else {
            Logging.debug(this, "Columns = " + columns + ", Rows = " + rows + ", Hiding all players");
            playerTablist.hideAllPlayers();
        }
    }

    //Interface methods

    @Override
    public void disable() {
        removeTabs(1, columns, 1, rows);
    }

    @Override
    public boolean allowExternalPlayerTabModification() {
        return false;
    }

    @Override
    public ArrayTablist basicClone(PlayerTablist playerTablist) {
        return new ArrayTablist(playerTablist, columns, rows);
    }

    @Override
    public void applyChanges(ArrayTablist arrayTablist) {
        arrayTablist.setColumns(Math.max(columns, arrayTablist.getColumns()));
        arrayTablist.setRows(Math.max(rows, arrayTablist.getRows()));
        for (int column = 1; column <= columns; column++)
            for (int row = 1; row <= rows; row++) {
                Tab otherTab = arrayTablist.getTab(column, row);
                getTab(column, row).applyChanges(otherTab);
            }
    }

    @Override
    public void refreshIconsIfDefault() {
        for (int column = 1; column <= columns; column++)
            for (int row = 1; row <= rows; row++) {
                Tab tab = getTab(column, row);
                if (!tab.getIcon().isPresent()) {
                    tab.refresh();
                }
            }
    }
}
