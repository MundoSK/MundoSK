package com.pie.tlatoani.Tablist;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.pie.tlatoani.ProtocolLib.PacketUtil;
import com.pie.tlatoani.Skin.Skin;
import com.pie.tlatoani.Util.Invalidatable;
import com.pie.tlatoani.Core.Static.MathUtil;
import com.pie.tlatoani.Core.Static.OptionalUtil;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by Tlatoani on 5/8/17.
 * Represents a rectangle in the tablist of a (possibly non-existent) player
 * Note that the four attributes of a Tab (display name, latency bars, icon, score) can be empty,
 * which is considered to be an "unmodified" state. How attributes considered empty
 * appear in-game depends on the attribute and possibly on overriden methods in subclasses of Tab.
 */
public class Tab {
    public final Tablist tablist;
    public final String name;
    public final UUID uuid;

    protected Optional<String> displayName;
    protected Optional<Integer> latencyBars;
    protected Optional<Skin> icon;
    protected Invalidatable<Integer> score;

    /**
     * Instantiates a Tab with all attributes empty
     * @param tablist The {@link Tablist} containing this tab
     * @param name The profile name of this tab
     * @param uuid The {@link UUID} of this tab
     */
    public Tab(Tablist tablist, String name, UUID uuid) {
        this(tablist, name, uuid, null, null, null, null);
    }

    /**
     * Instantiates a Tab with the specified attributes
     * @param tablist The {@link Tablist} containing this tab
     * @param name The profile name of this tab
     * @param uuid The {@link UUID} of this tab
     * @param displayName The display name of this tab (see {@link #getDisplayName()}), or null for an empty display name
     * @param latencyBars The latency bars of this tab (see {@link #getLatencyBars()}), or null for an empty latency bars
     * @param icon The icon of this tab (see {@link #getIcon()}), or null for an empty icon
     * @param score The score of this tab (see {@link #getScore()}), or null for an empty score
     */
    public Tab(Tablist tablist, String name, UUID uuid, @Nullable String displayName, @Nullable Integer latencyBars, @Nullable Skin icon, @Nullable Integer score) {
        if (tablist == null || name == null || uuid == null) {
            throw new NullPointerException("The tablist, name, and uuid parameters should all be non-null: "
                    + "tablist = " + tablist + ", name = " + name + ", uuid = " + uuid);
        }
        this.tablist = tablist;
        this.name = name;
        this.uuid = uuid;

        this.displayName = Optional.ofNullable(displayName);
        this.latencyBars = Optional.ofNullable(latencyBars);
        this.icon = Optional.ofNullable(icon);
        this.score = score == null ? Invalidatable.invalid() : tablist.createScore(score);
    }

    /**
     * Returns the name that is currently being used for this tab.
     * By default this just returns {@code this.name},
     * but it can be overridden to change what name is being used throughout the lifespan of this tab.
     * @return The name that is currently being used for this tab
     */
    public String getName() {
        return name;
    }

    /**
     * Generates a packet of type {@link com.comphenix.protocol.PacketType.Play.Server#PLAYER_INFO}
     * with the attributes of this Tab and {@code action}
     * @param action
     * @return A {@link com.comphenix.protocol.PacketType.Play.Server#PLAYER_INFO} packet
     */
    public PacketContainer playerInfoPacket(EnumWrappers.PlayerInfoAction action) {
        if (action == null) {
            throw new IllegalArgumentException("The action = " + action + " should not be null");
        }
        return PacketUtil.playerInfoPacket(
                displayName.orElse(""),
                latencyBars.orElse(5),
                null,
                getName(),
                uuid,
                icon.orElse(tablist.getDefaultIcon().orElse(Tablist.DEFAULT_SKIN_TEXTURE)),
                action
        );
    }

    /**
     * Generates a packet of type {@link com.comphenix.protocol.PacketType.Play.Server#SCOREBOARD_SCORE}
     * with {@link Tablist#OBJECTIVE_NAME}, the score of this tab, and {@link EnumWrappers.ScoreboardAction#CHANGE}.
     * @return A  {@link com.comphenix.protocol.PacketType.Play.Server#SCOREBOARD_SCORE} packet
     */
    public PacketContainer updateScorePacket() {
        return PacketUtil.scorePacket(
                getName(),
                Tablist.OBJECTIVE_NAME,
                getScore().orElse(0),
                EnumWrappers.ScoreboardAction.CHANGE
        );
    }

    public void refresh() {
        tablist.sendPacket(playerInfoPacket(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER), this);
        tablist.sendPacket(playerInfoPacket(EnumWrappers.PlayerInfoAction.ADD_PLAYER), this);
    }

    /**
     * @return true if all of the attributes of this Tab are empty, false otherwise
     */
    public boolean isDefault() {
        return !(displayName.isPresent() || latencyBars.isPresent() || icon.isPresent() || getScore().isPresent());
    }

    /**
     * The display name is the name shown on a tab,
     * with its intended purpose in Minecraft being to be identical to a player's name.
     * By default an empty display name will be equivalent to a display name of "".
     * @return An {@link Optional} containing the display name of this tab,
     * or {@link Optional#empty()} if the display name is empty
     */
    public Optional<String> getDisplayName() {
        return displayName;
    }

    /**
     * See {@link #getDisplayName()}
     * @param displayName The display name which you would like this tab to have,
     *                    or null if you want the display name to be empty
     */
    public void setDisplayName(@Nullable String displayName) {
        if (OptionalUtil.equal(displayName, this.displayName)) {
            return;
        }
        this.displayName = Optional.ofNullable(displayName);
        tablist.sendPacket(playerInfoPacket(EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME), this);
    }

    /**
     * The latency bars is the amount of bars (0 to 5 inclusive) that are shown on the tab,
     * with its intended purpose in Minecraft being to communicate the relative quality of a player's connection.
     * By default an empty latency bars will be equivalent to a latency bars of 5.
     * @return An {@link Optional} containing the latency bars of this tab,
     * or {@link Optional#empty()} if the latency bars is empty
     */
    public Optional<Integer> getLatencyBars() {
        return latencyBars;
    }

    /**
     * See {@link #getLatencyBars()}
     * @param latencyBars The latency bars which you would like this tab to have,
     *                    or null if you want the latency bars to be empty
     * @throws IllegalArgumentException If the latencyBars parameter is non-null and outside of the range 0 to 5 inclusive
     */
    public void setLatencyBars(@Nullable Integer latencyBars) {
        if (latencyBars != null && !MathUtil.isInRange(0, latencyBars, 5)) {
            throw new IllegalArgumentException(
                    "Illegal latencyBars value, should be within 0 and 5 inclusive, latency: " + latencyBars);
        }
        if (OptionalUtil.equal(latencyBars, this.latencyBars)) {
            return;
        }
        this.latencyBars = Optional.ofNullable(latencyBars);
        tablist.sendPacket(playerInfoPacket(EnumWrappers.PlayerInfoAction.UPDATE_LATENCY), this);
    }

    /**
     * The icon is the skin of the head that is shown on the tab,
     * with its intended purpose in Minecraft being to be identical to a player's skin.
     * By default an empty icon will be equivalent to {@link Tablist#getDefaultIcon()} of this tab's tablist if that is nonempty,
     * or {@link Tablist#DEFAULT_SKIN_TEXTURE} otherwise.
     * @return An {@link Optional} containing the icon of this tab, or {@link Optional#empty()} if the icon  is empty
     */
    public Optional<Skin> getIcon() {
        return icon;
    }

    /**
     * See {@link #getIcon()}
     * @param icon The icon which you would like this tab to have, or null if you want the icon to be empty
     */
    public void setIcon(@Nullable Skin icon) {
        if (OptionalUtil.referencesEqual(icon, this.icon)) {
            return;
        }
        tablist.sendPacket(playerInfoPacket(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER), this);
        this.icon = Optional.ofNullable(icon);
        tablist.sendPacket(playerInfoPacket(EnumWrappers.PlayerInfoAction.ADD_PLAYER), this);
    }

    /**
     * The score is the integer shown in yellow on the tab if scores are enabled on that tablist.
     * By default, if scores are enabled (see {@link Tablist#areScoresEnabled()}, an empty score will be equivalent to 0.
     * If scores are not enabled, the score is guaranteed to be empty and no integer is actually shown on the tab.
     * @return An {@link Optional} containing the score of this tab, or {@link Optional#empty()} if the score is empty
     */
    public Optional<Integer> getScore() {
        return score.get();
    }

    /**
     * See {@link #getScore()}
     * This method will do nothing if scores are not enabled.
     * @param score The score which you would like this tab to have, or null if you want the score to be empty
     */
    public void setScore(@Nullable Integer score) {
        if (!tablist.areScoresEnabled() || OptionalUtil.equal(score, getScore())) {
            return;
        }
        this.score = score == null ? Invalidatable.invalid() : tablist.createScore(score);
        tablist.sendPacket(updateScorePacket(), this);
    }

    /**
     * For every nonempty attribute of this Tab object,
     * sets the value of that attribute for {@code otherTab} to be equal to that of this tab.
     * @param otherTab
     */
    public void applyChanges(Tab otherTab) {
        if (otherTab == null) {
            throw new IllegalArgumentException("The otherTab should not be null");
        }
        if (icon.isPresent() && !OptionalUtil.referencesEqual(icon.get(), otherTab.getIcon())) {
            displayName.ifPresent(val -> otherTab.displayName = Optional.of(val));
            latencyBars.ifPresent(val -> otherTab.latencyBars = Optional.of(val));
            otherTab.setIcon(icon.get());
        } else {
            displayName.ifPresent(otherTab::setDisplayName);
            latencyBars.ifPresent(otherTab::setLatencyBars);
        }
        getScore().ifPresent(otherTab::setScore);
    }
}
