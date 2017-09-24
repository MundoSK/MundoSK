package com.pie.tlatoani.Tablist;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.pie.tlatoani.ProtocolLib.PacketManager;
import com.pie.tlatoani.ProtocolLib.PacketUtil;
import com.pie.tlatoani.Skin.Skin;
import com.pie.tlatoani.Tablist.Player.PlayerTablist;
import com.pie.tlatoani.Tablist.Simple.SimpleTablist;
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

            PacketContainer createPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.SCOREBOARD_OBJECTIVE); //Used to get some defaults
            createPacket.getStrings().writeSafely(0, OBJECTIVE_NAME);
            createPacket.getStrings().writeSafely(1, OBJECTIVE_NAME);
            createPacket.getIntegers().writeSafely(0, 0);
            PacketManager.sendPacket(createPacket, this, target);

            PacketContainer displayPacket = new PacketContainer(PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE);
            displayPacket.getIntegers().writeSafely(0, 0);
            displayPacket.getStrings().writeSafely(0, OBJECTIVE_NAME);
            PacketManager.sendPacket(displayPacket, this, target);
        }
    }

    public void disableScores() {
        if (areScoresEnabled()) {
            scoresEnabled = false;
            PacketContainer removePacket = new PacketContainer(PacketType.Play.Server.SCOREBOARD_OBJECTIVE);
            removePacket.getStrings().writeSafely(0, OBJECTIVE_NAME);
            removePacket.getStrings().writeSafely(1, OBJECTIVE_NAME);
            removePacket.getIntegers().writeSafely(0, 1);
            PacketManager.sendPacket(removePacket, this, target);
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
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER);
        packet.getChatComponents().writeSafely(0, PacketUtil.stringsToChatComponent(header));
        packet.getChatComponents().writeSafely(1, PacketUtil.stringsToChatComponent(footer));
        PacketManager.sendPacket(packet, this, target);

    }
}
