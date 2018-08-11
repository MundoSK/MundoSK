package com.pie.tlatoani.Tablist.Simple;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.pie.tlatoani.Core.Static.MathUtil;
import com.pie.tlatoani.Core.Static.OptionalUtil;
import com.pie.tlatoani.Skin.Skin;
import com.pie.tlatoani.Tablist.Player.PlayerTablist;
import com.pie.tlatoani.Tablist.SupplementaryTablist;
import com.pie.tlatoani.Tablist.Tab;
import com.pie.tlatoani.Tablist.Tablist;
import com.pie.tlatoani.Util.IntUsage;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Optional;

/**
 * Created by Tlatoani on 7/15/16.
 * Used as the default {@link SupplementaryTablist}.
 * Allows creation and manipulation of individual tabs by {@link String} IDs.
 * These tabs are referred to here as simple tabs, and are represented by the {@link SimpleTab} class.
 * Could theoretically be used to simulate any {@link SupplementaryTablist}.
 */
public class SimpleTablist implements SupplementaryTablist<SimpleTablist> {
    public final Tablist tablist;
    private final PlayerTablist playerTablist;

    private final HashMap<String, SimpleTab> tabs = new HashMap<>();
    private final IntUsage intUsage = new IntUsage(256);

    /**
     * Initializes a SimpleTablist to be owned by the {@link Tablist} owning {@code playerTablist}.
     */
    public SimpleTablist(PlayerTablist playerTablist) {
        this.tablist = playerTablist.tablist;
        this.playerTablist = playerTablist;
    }

    /**
     * Deletes all simple tabs.
     */
    public void clear() {
        for (Tab tab : tabs.values()) {
            tablist.sendPacket(tab.playerInfoPacket(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER), this);
        }
        tabs.clear();
    }

    /**
     * Creates a simple tab according to the specified parameters.
     * If a simple tab already exists with ID {@code id}, that one is deleted before the creation of the new one.
     * @param id The ID by which to refer to the simple tab
     * @param priority The priority of the simple tab (or null for empty)
     * @param displayName The display name of the simple tab (or null for empty)
     * @param latencyBars The latency bars (0 - 5) of the simple tab (or null for empty)
     * @param icon The icon of the simple tab (or null for empty)
     * @param score The score of the simple tab (or null for empty)
     * @return The newly created simple tab
     */
    public SimpleTab createTab(
            String id,
            @Nullable String priority,
            @Nullable String displayName,
            @Nullable Integer latencyBars,
            @Nullable Skin icon,
            @Nullable Integer score
    ) {
        if (id == null) {
            throw new IllegalArgumentException("The id cannot be null!");
        }
        if (priority != null && priority.length() > 12) {
            throw new IllegalArgumentException("The priority must be at most 12 characters in length, priority = " + priority);
        }
        return tabs.compute(id, (__, oldTab) -> {
            if (oldTab != null) {
                tablist.sendPacket(oldTab.playerInfoPacket(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER), this);
            }
            int num = intUsage.useFirstUnused() - 1;
            String uuidEnding = MathUtil.toHexDigit(num / 16) + "" + MathUtil.toHexDigit(num % 16);
            SimpleTab newTab = new SimpleTab(this,
                    id,
                    priority,
                    uuidEnding,
                    displayName,
                    latencyBars,
                    icon,
                    score
            );
            tablist.sendPacket(newTab.playerInfoPacket(EnumWrappers.PlayerInfoAction.ADD_PLAYER), this);
            if (tablist.areScoresEnabled()) {
                tablist.sendPacket(newTab.updateScorePacket(), this);
            }
            return newTab;
        });
    }

    /**
     * @param id The ID of the desired simple tab
     * @return An {@link Optional} containing the simple tab with ID {@code id},
     * or {@link Optional#empty()} if no such simple tab exists.
     */
    public Optional<SimpleTab> getTab(String id) {
        return Optional.ofNullable(tabs.get(id));
    }

    /**
     * Deletes the simple tab with ID {@code id} if it exists.
     */
    public void deleteTab(String id) {
        Tab tab = tabs.remove(id);
        if (tab != null) {
            tablist.sendPacket(tab.playerInfoPacket(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER), this);
        }
    }

    public boolean isEmpty() {
        return tabs.isEmpty();
    }

    @Override
    public void disable() {
        clear();
    }

    @Override
    public boolean allowExternalPlayerTabModification() {
        return true;
    }

    @Override
    public SimpleTablist basicClone(PlayerTablist otherPlayerTablist) {
        return new SimpleTablist(otherPlayerTablist);
    }

    @Override
    public void applyChanges(SimpleTablist simpleTablist) {
        tabs.forEach((id, tab) -> {
            OptionalUtil.consume(
                    simpleTablist.getTab(id),
                    () -> simpleTablist.createTab(
                            id,
                            tab.getPriority().orElse(null),
                            tab.getDisplayName().orElse(null),
                            tab.getLatencyBars().orElse(null),
                            tab.getIcon().orElse(null),
                            tab.getScore().orElse(null)
                    ),
                    tab::applyChanges
            );
        });
    }

    @Override
    public void refreshIconsIfDefault() {
        for (Tab tab : tabs.values()) {
            if (!tab.getIcon().isPresent()) {
                tab.refresh();
            }
        }
    }
}
