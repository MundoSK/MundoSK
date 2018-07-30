package com.pie.tlatoani.Tablist.Simple;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.pie.tlatoani.Core.Static.OptionalUtil;
import com.pie.tlatoani.ProtocolLib.PacketUtil;
import com.pie.tlatoani.Skin.Skin;
import com.pie.tlatoani.Tablist.Tab;
import com.pie.tlatoani.Tablist.Tablist;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class SimpleTab extends Tab {
    private final String id;
    private final String uuidEnding;
    private Optional<Location> location;
    private Optional<String> priority;

    public enum Location {
        BEFORE_PLAYERS,
        WITHIN_PLAYERS,
        AFTER_PLAYERS
    }

    public SimpleTab(SimpleTablist simpleTablist, String id, @Nullable Location location, @Nullable String priority, String uuidEnding, @Nullable String displayName, @Nullable Integer latencyBars, @Nullable Skin icon, @Nullable Integer score) {
        super(simpleTablist.tablist, "SimpleTab-" + uuidEnding, UUID.fromString(SimpleTablist.UUID_BEGINNING + "10" + uuidEnding), displayName, latencyBars, icon, score);
        this.id = id;
        this.uuidEnding = uuidEnding;
        this.location = Optional.ofNullable(location);
        this.priority = Optional.ofNullable(priority);
    }

    public String getName() {
        String priority = this.priority.orElse(id.length() < 12 ? id : id.substring(0, 12));
        switch (location.orElse(Location.WITHIN_PLAYERS)) {
            case WITHIN_PLAYERS: return priority + "#M" + uuidEnding;
            case BEFORE_PLAYERS: return "#M" + priority + uuidEnding;
            case AFTER_PLAYERS:  return "~M" + priority + uuidEnding;
        }
        throw new IllegalStateException("location = " + location);
    }

    @Override
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

    @Override
    public PacketContainer updateScorePacket() {
        return PacketUtil.scorePacket(
                getName(),
                Tablist.OBJECTIVE_NAME,
                getScore().orElse(0),
                EnumWrappers.ScoreboardAction.CHANGE
        );
    }

    public Optional<Location> getLocation() {
        return location;
    }

    public Optional<String> getPriority() {
        return priority;
    }

    public void setLocation(@Nullable Location location) {
        if (OptionalUtil.equal(location, this.location)) {
            return;
        }
        tablist.sendPacket(playerInfoPacket(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER), this);
        this.location = Optional.ofNullable(location);
        tablist.sendPacket(playerInfoPacket(EnumWrappers.PlayerInfoAction.ADD_PLAYER), this);
    }

    public void setPriority(@Nullable String priority) {
        if (priority != null && priority.length() > 12) {
            throw new IllegalArgumentException("The priority must be non-null and at most 12 characters in length, priority = " + priority);
        }
        if (OptionalUtil.equal(priority, this.priority)) {
            return;
        }
        tablist.sendPacket(playerInfoPacket(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER), this);
        this.priority = Optional.ofNullable(priority);
        tablist.sendPacket(playerInfoPacket(EnumWrappers.PlayerInfoAction.ADD_PLAYER), this);
    }

    public void applyChanges(SimpleTab otherTab) {
        if (otherTab == null) {
            throw new IllegalArgumentException("The otherTab = " + otherTab + " should not be null");
        }
        getScore().ifPresent(otherTab::setScore);
        boolean needsRefresh = (icon.isPresent() && !OptionalUtil.referencesEqual(icon.get(), otherTab.getIcon()))
                || (location.isPresent() && OptionalUtil.equal(location.get(), otherTab.getLocation()))
                || (priority.isPresent() && OptionalUtil.equal(priority.get(), otherTab.getPriority()));
        if (needsRefresh) {
            displayName.ifPresent(val -> otherTab.displayName = Optional.of(val));
            latencyBars.ifPresent(val -> otherTab.latencyBars = Optional.of(val));
            location.ifPresent(val -> otherTab.location = Optional.of(val));
            priority.ifPresent(val -> otherTab.priority = Optional.of(val));
            icon.ifPresent(val -> otherTab.icon = Optional.of(val));
            otherTab.refresh();
            if (otherTab.tablist.areScoresEnabled()) {
                tablist.sendPacket(otherTab.updateScorePacket(), SimpleTab.this);
            }
        } else {
            displayName.ifPresent(otherTab::setDisplayName);
            latencyBars.ifPresent(otherTab::setLatencyBars);
        }
    }
}
