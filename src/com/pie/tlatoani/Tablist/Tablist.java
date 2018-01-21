package com.pie.tlatoani.Tablist;

import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.pie.tlatoani.ProtocolLib.PacketManager;
import com.pie.tlatoani.ProtocolLib.PacketUtil;
import com.pie.tlatoani.Skin.Skin;
import com.pie.tlatoani.Tablist.Player.PlayerTablist;
import com.pie.tlatoani.Tablist.Simple.SimpleTablist;
import mundosk_libraries.packetwrapper.*;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.function.Function;

/**
 * Created by Tlatoani on 4/15/17.
 */
public class Tablist {
    public final Player target;

    private boolean scoresEnabled;
    private String[] header = new String[0];
    private String[] footer = new String[0];

    private final PlayerTablist playerTablist = new PlayerTablist(this);
    private SupplementaryTablist supplementaryTablist = new SimpleTablist(playerTablist);



    public static final Skin DEFAULT_SKIN_TEXTURE = Skin.ALL_WHITE;
    public static final String OBJECTIVE_NAME = "MundoSK_Tablist";

    public Tablist(Player target) {
        this.target = target;
    }

    //PlayerTablist

    public Optional<PlayerTablist> getPlayerTablist() {
        if (supplementaryTablist.allowExternalPlayerTabModification()) {
            return Optional.of(playerTablist);
        } else {
            return Optional.empty();
        }
    }

    void onJoin(Player player) {
        playerTablist.onJoin(player);
    }

    void onQuit(Player player) {
        playerTablist.onQuit(player);
    }

    PlayerInfoData onPlayerInfoPacket(PlayerInfoData oldPlayerInfoData, Player objPlayer) {
        return playerTablist.onPlayerInfoPacket(oldPlayerInfoData, objPlayer);
    }

    void onScoreboardTeamPacket(WrapperPlayServerScoreboardTeam packet) {
        playerTablist.onScoreboardTeamPacket(packet);
    }

    public boolean arePlayersVisible() {
        return playerTablist.arePlayersVisible();
    }

    public boolean isPlayerVisible(Player player) {
        return playerTablist.isPlayerVisible(player);
    }

    //SupplementaryTablist

    public SupplementaryTablist getSupplementaryTablist() {
        return supplementaryTablist;
    }

    public void setSupplementaryTablist(Function<PlayerTablist, SupplementaryTablist> supplementaryTablistProvider) {
        supplementaryTablist.disable();
        supplementaryTablist = supplementaryTablistProvider.apply(playerTablist);
    }

    //Scores

    public boolean areScoresEnabled() {
        return scoresEnabled;
    }

    public void setScoresEnabled(boolean enabled) {
        if (enabled) {
            enableScores();
        } else {
            disableScores();
        }
    }

    public void enableScores() {
        if (!areScoresEnabled()) {
            scoresEnabled = true;

            WrapperPlayServerScoreboardObjective createPacket = new WrapperPlayServerScoreboardObjective();
            createPacket.setName(OBJECTIVE_NAME);
            createPacket.setDisplayName(OBJECTIVE_NAME);
            createPacket.setMode(WrapperPlayServerScoreboardObjective.Mode.ADD_OBJECTIVE);
            createPacket.setHealthDisplay(WrapperPlayServerScoreboardObjective.HealthDisplay.INTEGER);
            PacketManager.sendPacket(createPacket.getHandle(), this, target);

            WrapperPlayServerScoreboardDisplayObjective displayPacket = new WrapperPlayServerScoreboardDisplayObjective();
            displayPacket.setPosition(0);
            displayPacket.setScoreName(OBJECTIVE_NAME);
            PacketManager.sendPacket(displayPacket.getHandle(), this, target);
        }
    }

    public void disableScores() {
        if (areScoresEnabled()) {
            scoresEnabled = false;
            WrapperPlayServerScoreboardObjective removePacket = new WrapperPlayServerScoreboardObjective();
            removePacket.setName(OBJECTIVE_NAME);
            removePacket.setDisplayName(OBJECTIVE_NAME);
            removePacket.setMode(WrapperPlayServerScoreboardObjective.Mode.REMOVE_OBJECTIVE);
            removePacket.setHealthDisplay(WrapperPlayServerScoreboardObjective.HealthDisplay.INTEGER);
            PacketManager.sendPacket(removePacket.getHandle(), this, target);
        }
    }

    //Header and Footer

    public String[] getHeader() {
        return header;
    }

    public String[] getFooter() {
        return footer;
    }

    public void setHeader(String[] header) {
        this.header = header;
        refreshHeaderAndFooter();
    }

    public void setFooter(String[] footer) {
        this.footer = footer;
        refreshHeaderAndFooter();
    }

    private void refreshHeaderAndFooter() {
        WrapperPlayServerPlayerListHeaderFooter packet = new WrapperPlayServerPlayerListHeaderFooter();
        packet.setHeader(PacketUtil.stringsToChatComponent(header));
        packet.setFooter(PacketUtil.stringsToChatComponent(footer));
        PacketManager.sendPacket(packet.getHandle(), this, target);

    }
}
