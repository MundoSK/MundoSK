package com.pie.tlatoani.Tablist;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.common.collect.ImmutableList;
import com.pie.tlatoani.ProtocolLib.PacketManager;
import com.pie.tlatoani.ProtocolLib.PacketUtil;
import com.pie.tlatoani.Skin.Skin;
import com.pie.tlatoani.Tablist.Group.TablistGroup;
import com.pie.tlatoani.Tablist.Player.PlayerTablist;
import com.pie.tlatoani.Tablist.Simple.SimpleTablist;
import com.pie.tlatoani.Util.Invalidatable;
import com.pie.tlatoani.Core.Static.OptionalUtil;
import mundosk_libraries.packetwrapper.WrapperPlayServerPlayerListHeaderFooter;
import mundosk_libraries.packetwrapper.WrapperPlayServerScoreboardDisplayObjective;
import mundosk_libraries.packetwrapper.WrapperPlayServerScoreboardObjective;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Function;

/**
 * Created by Tlatoani on 4/15/17.
 * Represents the tablist (the list of players in Minecraft shown when you hold a button, by default the 'tab' button)
 * of a player (referred to here as the target).
 * Most of the functionality is delegated to two contained objects:
 * a {@link PlayerTablist} that never changes, which handles all of the tabs connected to actual players,
 * and a {@link SupplementaryTablist} which handles all other tabs, can be changed using {@link #setSupplementaryTablist(Function)},
 * and is a {@link SimpleTablist} by default.
 */
public class Tablist {
    private final Optional<Player> target;

    private boolean scoresEnabled;
    private final Invalidatable.Creator scoreCreator = new Invalidatable.Creator();

    private ImmutableList<String> header = ImmutableList.of();
    private ImmutableList<String> footer = ImmutableList.of();

    private Optional<Skin> defaultIcon = Optional.empty();
    private final PlayerTablist playerTablist = new PlayerTablist(this);
    private SupplementaryTablist supplementaryTablist = new SimpleTablist(playerTablist);


    public static final Skin DEFAULT_SKIN_TEXTURE = Skin.ALL_WHITE;
    public static final String OBJECTIVE_NAME = "MundoSK_Tablist";

    /**
     * Instantiates a Tablist without a real target, meaning it is only used to store tablist data.
     * Currently used only for {@link TablistGroup}s.
     */
    public Tablist() {
        this.target = Optional.empty();
    }

    /**
     * Instantiates a Tablist with the specified target. Only used in {@link TablistManager#getTablistGroup(String)}.
     * @param target The {@link Player} whose in-game tablist will be manipulated through this Tablist
     */
    Tablist(Player target) {
        this.target = Optional.of(target);
    }

    //PlayerTablist

    /**
     * Returns the {@link PlayerTablist} of this tablist.
     * This will return {@link Optional#empty()} if the current {@link SupplementaryTablist}
     * of this tablist returns false on {@link SupplementaryTablist#allowExternalPlayerTabModification()}
     * @return An {@link Optional} containing this tablist's {@link PlayerTablist},
     * or {@link Optional#empty()} if this tablist's {@link SupplementaryTablist}
     * forbids external manipulation of the {@link PlayerTablist}
     */
    public Optional<PlayerTablist> getPlayerTablist() {
        if (supplementaryTablist.allowExternalPlayerTabModification()) {
            return Optional.of(playerTablist);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Called by {@link TablistManager} when a player joins,
     * in order to allow the {@link PlayerTablist} to send any necessary packets
     * @param player The player that joined
     */
    void onJoin(Player player) {
        playerTablist.onJoin(player);
    }

    /**
     * Called by {@link TablistManager} when a player quits, in order to remove their data from the {@link PlayerTablist}
     * @param player The player that quit
     */
    void onQuit(Player player) {
        playerTablist.onQuit(player);
    }

    /**
     * Called by {@link TablistManager} when a {@link com.comphenix.protocol.PacketType.Play.Server#PLAYER_INFO} packet
     * is being sent to the target of this tablist.
     * This method (if necessary) modifies the {@link PlayerInfoData} contained in the packet
     * to correspond to the attributes of the {@link Tab} representing {@code objPlayer}
     * @param oldPlayerInfoData The {@link PlayerInfoData} previously contained by the packet for {@code objPlayer}
     * @param objPlayer The player whose tab is being modified in the target's tablist
     * @return A {@link PlayerInfoData} with data modified as necessary from {@code oldPlayerInfoData}
     * to correspond to the attributes of {@code objPlayer}'s tab
     * @throws UnsupportedOperationException If this Tablist does not have a target
     */
    PlayerInfoData onPlayerInfoPacket(PlayerInfoData oldPlayerInfoData, Player objPlayer) {
        if (!this.target.isPresent()) {
            throw new UnsupportedOperationException("This Tablist does not have a target!");
        }
        Player target = this.target.get();
        return playerTablist.getTabIfModified(objPlayer).map(tab -> {
            WrappedChatComponent displayName =
                    tab.getDisplayName()
                    .map(rawDisplayName -> Optional
                            .ofNullable(target.getScoreboard())
                            .map(scoreboard -> scoreboard.getEntryTeam(objPlayer.getName()))
                            .map(team -> team.getPrefix() + rawDisplayName + team.getSuffix())
                            .orElse(rawDisplayName))
                    .map(WrappedChatComponent::fromText)
                    .orElse(oldPlayerInfoData.getDisplayName());
            return new PlayerInfoData(
                    oldPlayerInfoData.getProfile(),
                    tab.getLatencyBars().map(PacketUtil::getPossibleLatency).orElse(oldPlayerInfoData.getLatency()),
                    oldPlayerInfoData.getGameMode(),
                    displayName);
        }).orElse(oldPlayerInfoData);
    }

    /**
     * Calls {@link PlayerTablist#arePlayersVisible()} on this tablist's {@link PlayerTablist} and returns the result.
     * Useful if the {@link PlayerTablist} itself cannot be returned (see {@link #getPlayerTablist()}).
     * @return Whether tabs of actual players are visible in this tablist
     */
    public boolean arePlayersVisible() {
        return playerTablist.arePlayersVisible();
    }

    /**
     * Calls {@link PlayerTablist#isPlayerVisible(Player)} on this tablist's {@link PlayerTablist} for {@code player}
     * and returns the result.
     * Useful if the {@link PlayerTablist} itself cannot be returned (see {@link #getPlayerTablist()}).
     * @return Whether the tab of {@code player} is visible in this tablist
     */
    public boolean isPlayerVisible(Player player) {
        return playerTablist.isPlayerVisible(player);
    }

    //SupplementaryTablist

    /**
     * @return The {@link SupplementaryTablist} currently managing this tablist's non-player-connected tabs.
     */
    public SupplementaryTablist getSupplementaryTablist() {
        return supplementaryTablist;
    }

    /**
     * Changes the {@link SupplementaryTablist} managing this tablist's non-player-connected tabs by
     * first disabling the previous one, then applying calling {@code supplementaryTablistProvider}
     * on this tablist's {@link PlayerTablist} in order to get the new {@link SupplementaryTablist}.
     * Note that the {@link PlayerTablist} is provided only to be given to the new {@link SupplementaryTablist}
     * and should not be stored outside of the new {@link SupplementaryTablist}.
     * @param supplementaryTablistProvider
     * A function that creates and returns a {@link SupplementaryTablist} given this tablist's {@link PlayerTablist}
     */
    public void setSupplementaryTablist(Function<PlayerTablist, ? extends SupplementaryTablist> supplementaryTablistProvider) {
        supplementaryTablist.disable();
        supplementaryTablist = null;
        supplementaryTablist = supplementaryTablistProvider.apply(playerTablist);
    }

    //Default Icon

    /**
     * Returns the default icon, which is the icon that is displayed for tabs that have an empty icon.
     * This can also be empty, in which case {@link Tablist#DEFAULT_SKIN_TEXTURE} is displayed.
     * @return An {@link Optional} containing the default icon, or {@link Optional#empty()} if the default icon is empty
     */
    public Optional<Skin> getDefaultIcon() {
        return defaultIcon;
    }

    /**
     * This method sets the default icon to {@code icon}.
     * This method can be called during an invocation of {@link #setSupplementaryTablist(Function)}
     * when {@link #supplementaryTablist} is {@code null}.
     * @param icon The new default icon, or null to make the default icon empty
     */
    public void setDefaultIcon(@Nullable Skin icon) {
        if (!OptionalUtil.referencesEqual(icon, defaultIcon)) {
            defaultIcon = Optional.ofNullable(icon);
            if (supplementaryTablist != null) {
                supplementaryTablist.refreshIconsIfDefault();
            }
        }
    }

    //Scores

    /**
     * Returns whether scores are visible on this tablist and can be manipulated through this Tablist.
     * Note that scores can be added to the tablist through outside means,
     * but that will not change the result of calling this method.
     * @return {@code true} if scores are enabled for this tablist, {@code false} otherwise
     */
    public boolean areScoresEnabled() {
        return scoresEnabled;
    }

    /**
     * Calls {@link #enableScores()} if {@code enabled} is true, and {@link #disableScores()} otherwise.
     * @param enabled Whether to enable or disable scores.
     */
    public void setScoresEnabled(boolean enabled) {
        if (enabled) {
            enableScores();
        } else {
            disableScores();
        }
    }

    /**
     * If scores are not currently enabled, this method enables them by sending a
     * {@link com.comphenix.protocol.PacketType.Play.Server#SCOREBOARD_OBJECTIVE} packet and a
     * {@link com.comphenix.protocol.PacketType.Play.Server#SCOREBOARD_DISPLAY_OBJECTIVE} packet
     * in order to create an objective for scores to be displayed on the tablist
     * and set that objective's display location to the tablist.
     */
    public void enableScores() {
        if (!areScoresEnabled()) {
            scoresEnabled = true;

            WrapperPlayServerScoreboardObjective createPacket = new WrapperPlayServerScoreboardObjective();
            createPacket.setName(OBJECTIVE_NAME);
            createPacket.setDisplayName(OBJECTIVE_NAME);
            createPacket.setMode(WrapperPlayServerScoreboardObjective.Mode.ADD_OBJECTIVE);
            createPacket.setHealthDisplay(WrapperPlayServerScoreboardObjective.HealthDisplay.INTEGER);
            sendPacket(createPacket.getHandle(), this);

            WrapperPlayServerScoreboardDisplayObjective displayPacket = new WrapperPlayServerScoreboardDisplayObjective();
            displayPacket.setPosition(0);
            displayPacket.setScoreName(OBJECTIVE_NAME);
            sendPacket(displayPacket.getHandle(), this);
        }
    }

    /**
     * If scores are currently enabled, this method disables them by sending a
     * {@link com.comphenix.protocol.PacketType.Play.Server#SCOREBOARD_OBJECTIVE} packet
     * to remove the objective created for scores to be displayed on the tablist.
     */
    public void disableScores() {
        if (areScoresEnabled()) {
            scoresEnabled = false;
            scoreCreator.invalidate();

            WrapperPlayServerScoreboardObjective removePacket = new WrapperPlayServerScoreboardObjective();
            removePacket.setName(OBJECTIVE_NAME);
            removePacket.setDisplayName(OBJECTIVE_NAME);
            removePacket.setMode(WrapperPlayServerScoreboardObjective.Mode.REMOVE_OBJECTIVE);
            removePacket.setHealthDisplay(WrapperPlayServerScoreboardObjective.HealthDisplay.INTEGER);
            sendPacket(removePacket.getHandle(), this);
        }
    }

    /**
     * Should only be called by a {@link Tab} contained in this tablist.
     * If scores are enabled, this method creates and returns an {@link Invalidatable}
     * that will contain the value {@code score} until invalidated, which will happen when scores are disabled.
     * If scores are already disabled, this method will return {@link Invalidatable#invalid()}.
     * @param score The integer score to be contained in an {@link Invalidatable}
     * @return An {@link Invalidatable} that will contain {@code score} until scores are disabled
     */
    Invalidatable<Integer> createScore(int score) {
        if (scoresEnabled) {
            return scoreCreator.create(score);
        } else {
            return Invalidatable.invalid();
        }
    }

    //Header and Footer

    /**
     * Returns the lines of the current header of the tablist (the text displayed above the actual tabs).
     * @return The lines of the current header
     */
    public ImmutableList<String> getHeader() {
        return header;
    }

    /**
     * Returns the lines of the current footer of the tablist (the text displayed below the actual tabs).
     * @return The lines of the current footer
     */
    public ImmutableList<String> getFooter() {
        return footer;
    }

    /**
     * Sets the lines of the current header of the tablist by calling {@link #setHeaderAndFooter(ImmutableList, ImmutableList)}.
     * @param header The new lines of the header of the tablist
     */
    public void setHeader(ImmutableList<String> header) {
        setHeaderAndFooter(header, footer);
    }

    /**
     * Sets the lines of the current footer of the tablist by calling {@link #setHeaderAndFooter(ImmutableList, ImmutableList)}.
     * @param footer The new lines of the footer of the tablist
     */
    public void setFooter(ImmutableList<String> footer) {
        setHeaderAndFooter(header, footer);
    }

    /**
     * Sets the lines of the current header and footer of the tablist,
     * which involves sending a {@link com.comphenix.protocol.PacketType.Play.Server#PLAYER_LIST_HEADER_FOOTER} packet
     * in order to make the displayed header and footer for the target what it is according to the arguments.
     * @param header The new lines of the header of the tablist
     * @param footer The new lines of the footer of the tablist
     */
    public void setHeaderAndFooter(ImmutableList<String> header, ImmutableList<String> footer) {
        this.header = header;
        this.footer = footer;
        WrapperPlayServerPlayerListHeaderFooter packet = new WrapperPlayServerPlayerListHeaderFooter();
        packet.setHeader(PacketUtil.stringsToChatComponent(header));
        packet.setFooter(PacketUtil.stringsToChatComponent(footer));
        sendPacket(packet.getHandle(), this);
    }

    //For use within tablist only

    /**
     * If the target exists and is online, this method sends a certain packet to them.
     * @param packet The packet to send to the target
     * @param exceptLoc The class said to be responsible for sending the packet (important for error logging)
     */
    public void sendPacket(PacketContainer packet, Object exceptLoc) {
        target.filter(Player::isOnline).ifPresent(player -> PacketManager.sendPacket(packet, exceptLoc, player));
    }

    //For Tablist groups

    /**
     * Applies any changes made to this tablist to {@code tablist}.
     * If scores are enabled for this tablist, they will also be enabled for {@code tablist}.
     * If this tablist has a non-empty default icon, {@code tablist} will have the same default icon.
     * This tablist's {@link PlayerTablist}'s {@link PlayerTablist#applyChanges(PlayerTablist)}
     * method will be called on {@code tablist}'s {@link PlayerTablist}.
     * If this tablist's {@link SupplementaryTablist} is not an empty {@link SimpleTablist},
     * this tablist's {@link SupplementaryTablist}'s {@link SupplementaryTablist#applyChanges(SupplementaryTablist)}
     * method will be called on {@code tablist}'s {@link SupplementaryTablist},
     * possible after setting {@code tablist}'s {@link SupplementaryTablist}
     * to one returned by this tablist's {@link SupplementaryTablist}'s {@link SupplementaryTablist#basicClone(PlayerTablist)} method.
     * Every nonempty line of this tablist's header and footer will be set on {@code tablist}.
     * @param tablist The tablist to apply changes to
     */
    public void applyChanges(Tablist tablist) {
        if (areScoresEnabled()) {
            tablist.enableScores();
        }
        if (getSupplementaryTablist() instanceof SimpleTablist && ((SimpleTablist) getSupplementaryTablist()).isEmpty()) {
            playerTablist.applyChanges(tablist.playerTablist);
            defaultIcon.ifPresent(tablist::setDefaultIcon);
        } else if (tablist.getSupplementaryTablist().getClass() == supplementaryTablist.getClass()) {
            playerTablist.applyChanges(tablist.playerTablist);
            defaultIcon.ifPresent(tablist::setDefaultIcon);
            supplementaryTablist.applyChanges(tablist.getSupplementaryTablist());
        } else {
            tablist.setSupplementaryTablist(otherPlayerTablist -> {
                playerTablist.applyChanges(otherPlayerTablist);
                defaultIcon.ifPresent(tablist::setDefaultIcon);
                return supplementaryTablist.basicClone(otherPlayerTablist);
            });
            supplementaryTablist.applyChanges(tablist.getSupplementaryTablist());
        }
        ImmutableList<String> oldHeader = tablist.getHeader();
        ImmutableList.Builder<String> headerBuilder = ImmutableList.builder();
        for (int i = 0; i < Math.max(oldHeader.size(), header.size()); i++) {
            if (i < header.size() && !header.get(i).isEmpty()) {
                headerBuilder.add(header.get(i));
            } else if (i < oldHeader.size()) {
                headerBuilder.add(oldHeader.get(i));
            } else {
                headerBuilder.add("");
            }
        }
        ImmutableList<String> oldFooter = tablist.getFooter();
        ImmutableList.Builder<String> footerBuilder = ImmutableList.builder();
        for (int i = 0; i < Math.max(oldFooter.size(), footer.size()); i++) {
            if (i < footer.size() && !footer.get(i).isEmpty()) {
                footerBuilder.add(footer.get(i));
            } else if (i < oldFooter.size()) {
                footerBuilder.add(oldFooter.get(i));
            } else {
                footerBuilder.add("");
            }
        }
        tablist.setHeaderAndFooter(headerBuilder.build(), footerBuilder.build());
    }

    @Override
    public String toString() {
        return target.map(player -> "Tablist(" + player.getName() + ")").orElse(super.toString());
    }
}