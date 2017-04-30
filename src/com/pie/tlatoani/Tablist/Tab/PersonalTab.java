package com.pie.tlatoani.Tablist.Tab;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.Skin.Skin;
import com.pie.tlatoani.Tablist.Tablist;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * Created by Tlatoani on 4/26/17.
 */
public class PersonalTab implements Tab {
    public final Player player;
    public final PersonalizableTab parent;

    Optional<String> displayName = null;
    Optional<Byte> latency = null;
    Optional<Skin> icon = null;
    Optional<Integer> score = null;

    public PersonalTab(PersonalizableTab parent, Player player) {
        this.parent = parent;
        this.player = player;
    }

    public boolean containsValue() {
        return displayName != null
                || latency != null
                || icon != null
                || score != null;
    }

    @Override
    public Tablist getTablist() {
        return parent.getTablist();
    }

    @Override
    public void send(PacketContainer packet) {
        parent.send(packet, player);
    }

    @Override
    public void send(PacketContainer packet, Player player) {
        parent.send(packet, player);
    }

    //Done in order to allow parent to make changes to packets that affect PersonalTabs as well
    @Override
    public PacketContainer playerInfoPacket(EnumWrappers.PlayerInfoAction action, String displayName, Byte latency, Skin icon) {
        return parent.playerInfoPacket(action, displayName, latency, icon);
    }

    @Override
    public PacketContainer updateScorePacket(Integer score) {
        return parent.updateScorePacket(score);
    }

    @Override
    public String getDisplayName() {
        return displayName != null ? displayName.orElse(null) : parent.getDisplayName();
    }

    @Override
    public Byte getLatency() {
        return latency != null ? latency.orElse(null) : parent.getLatency();
    }

    @Override
    public Skin getIcon() {
        return icon != null ? icon.orElse(null) : parent.getIcon();
    }

    @Override
    public Integer getScore() {
        return score != null ? score.orElse(null) : parent.getScore();
    }

    @Override
    public void setDisplayName(String value) {
        if (value == null && displayName == null) {
            return;
        }
        displayName = Optional.ofNullable(value);
        send(playerInfoPacket(EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME, getDisplayName(), null, null));
    }

    @Override
    public void setLatency(Byte value) {
        if (value == null && latency == null) {
            return;
        }
        latency = Optional.ofNullable(value);
        send(playerInfoPacket(EnumWrappers.PlayerInfoAction.UPDATE_LATENCY, null, getLatency(), null));
    }

    @Override
    public void setIcon(Skin value) {
        if (value == null && icon == null) {
            return;
        }
        icon = Optional.ofNullable(value);
        send(hidePacket());
        Mundo.sync(1, () -> send(showPacket()));
    }

    @Override
    public void setScore(Integer value) {
        if (value == null && score == null) {
            return;
        }
        score = Optional.ofNullable(value);
        if (parent.getTablist().areScoresEnabled()) {
            send(updateScorePacket(getScore()));
        }
    }
}
