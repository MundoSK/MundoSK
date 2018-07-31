package com.pie.tlatoani.Tablist.Simple;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.pie.tlatoani.Core.Static.OptionalUtil;
import com.pie.tlatoani.Skin.Skin;
import com.pie.tlatoani.Tablist.Tab;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

/**
 * Used to contain the priority attribute that simple tabs have.
 * See {@link #getPriority()} for more information about priority.
 */
public class SimpleTab extends Tab {
    public final String uuidEnding;
    private Optional<String> priority;

    /**
     * Twelve spaces.
     */
    public static final String SPACES = "            ";
    public static final String UUID_BEGINNING = "10001000-1000-3000-8000-20002000";

    /**
     * Instantiates a Tab with the specified attributes
     * @param simpleTablist The {@link SimpleTablist} containing this simple tab
     * @param id The id of this tab
     * @param uuidEnding The last two characters of the UUID of this tab
     * @param priority The priority of this tab (see {@link #getPriority()}, or null for an empty priority
     * @param displayName The display name of this tab (see {@link #getDisplayName()}), or null for an empty display name
     * @param latencyBars The latency bars of this tab (see {@link #getLatencyBars()}), or null for an empty latency bars
     * @param icon The icon of this tab (see {@link #getIcon()}), or null for an empty icon
     * @param score The score of this tab (see {@link #getScore()}), or null for an empty score
     */
    public SimpleTab(SimpleTablist simpleTablist, String id, @Nullable String priority, String uuidEnding, @Nullable String displayName, @Nullable Integer latencyBars, @Nullable Skin icon, @Nullable Integer score) {
        super(simpleTablist.tablist, (id + SPACES).substring(0, 12) + "#M" + uuidEnding, UUID.fromString(UUID_BEGINNING + "10" + uuidEnding), displayName, latencyBars, icon, score);
        this.uuidEnding = uuidEnding;
        this.priority = Optional.ofNullable(priority);
    }

    @Override
    public String getName() {
        return priority.map(str -> str + SPACES.substring(12 - str.length()) + "#M" + uuidEnding).orElse(name);
    }

    /**
     * The priority determines the ordering of this simple tab relative to other simple tabs
     * (and possibly player tabs depending on the Minecraft client).
     * Priorities are used alphabetically,
     * meaning a simple tab with priority {@code "a"} will come before a simple tab with priority {@code "b"},
     * and a simple tab with priority {@code "a"} will also come before a simple tab with priority {@code "ab"}.
     * Note that as priorities will have spaces appended to them when being used as tab profile names,
     * a character that comes typographically before the space character {@code ' '}
     * may cause a priority like {@code "a\n"} to come before {@code "a"}.
     * Generally you should not use priorities with abnormal characters like {@code '\n'} for this and other reasons.
     * Priorities can be at most 12 characters in length. They will never have trailing spaces.
     * An empty priority will be equivalent to the first 12 characters of the id of this simple tab.
     * @return An {@link Optional} containing the priority of this tab,
     * or {@link Optional#empty()} if the priority is empty
     */
    public Optional<String> getPriority() {
        return priority;
    }

    /**
     * See {@link #getPriority()} ()}
     * Priorities cannot be longer than 12 characters in length.
     * Trailing spaces in priorities should be removed before calling this method.
     * @param priority The priority which you would like this simple tab to have,
     *                 or null if you want the priority to be empty
     * @throws IllegalArgumentException If the priority is longer than 12 characters in length or has trailing spaces
     */
    public void setPriority(@Nullable String priority) {
        if (priority != null && (priority.length() > 12 || priority.endsWith(" "))) {
            throw new IllegalArgumentException("The priority must be at most 12 characters in length and not end in a space, priority = \"" + priority + "\" (quoted to show spaces if they exist)");
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
                || (priority.isPresent() && !OptionalUtil.equal(priority.get(), otherTab.getPriority()));
        if (needsRefresh) {
            priority.ifPresent(val -> otherTab.priority = Optional.of(val));
            displayName.ifPresent(val -> otherTab.displayName = Optional.of(val));
            latencyBars.ifPresent(val -> otherTab.latencyBars = Optional.of(val));
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
