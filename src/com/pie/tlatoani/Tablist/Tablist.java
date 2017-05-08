package com.pie.tlatoani.Tablist;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.ProtocolLib.UtilPacketEvent;
import com.pie.tlatoani.Skin.Skin;
import com.pie.tlatoani.Tablist.Array.ArrayTablist;
import com.pie.tlatoani.Tablist.Player.PlayerTablist;
import com.pie.tlatoani.Tablist.Simple.SimpleTablist;
import com.pie.tlatoani.Util.Either;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Created by Tlatoani on 4/15/17.
 */
public class Tablist {
    private Storage storage = new Storage(this);
    public final Player target;

    public static final Skin DEFAULT_SKIN_TEXTURE = Skin.WHITE;
    public static final String OBJECTIVE_NAME = "MundoSK_Tablist";

    public Tablist(Player target) {
        this.target = target;
    }

    public static class Storage {
        public final Tablist tablist;
        public final ArrayList<Player> players = new ArrayList<>();
        public boolean scoresEnabled;
        public Optional<PlayerTablist> playerTablistOptional = Optional.of(new PlayerTablist(this));
        public Optional<ArrayTablist> arrayTablistOptional = Optional.empty();
        public SimpleTablist simpleTablist = new SimpleTablist(this);

        public Storage(Tablist tablist) {
            this.tablist = tablist;
        }
    }

    public Optional<PlayerTablist> getPlayerTablistOptional() {
        return storage.playerTablistOptional;
    }

    public void showAllPlayers() {
        Mundo.optionalCase(storage.playerTablistOptional, PlayerTablist::showAllPlayers, () -> {
            storage.playerTablistOptional = Optional.of(new PlayerTablist(storage));
            for (Player player : Bukkit.getOnlinePlayers()) {
                UtilPacketEvent.sendPacket(TablistManager.playerInfoPacket(player, EnumWrappers.PlayerInfoAction.ADD_PLAYER), Tablist.class, storage.players);
            }
        });
    }

    public void hideAllPlayers() {
        storage.playerTablistOptional.ifPresent(PlayerTablist::hideAllPlayers);
    }

    public Optional<ArrayTablist> getArrayTablistOptional() {
        return storage.arrayTablistOptional;
    }

    public ArrayTablist showArrayTablist(int columns, int rows) {
        ArrayTablist arrayTablist = storage.arrayTablistOptional.orElseGet(() -> {
            boolean canShowPlayers = columns == 4 && rows == 20;
            if (canShowPlayers) {
                showAllPlayers();
            } else {
                hideAllPlayers();
            }
            clearSimpleTablist();
            ArrayTablist newArrayTablist = null;
            storage.arrayTablistOptional = Optional.of(newArrayTablist);
            return newArrayTablist;
        });
        if (columns == 4 & rows == 20) {
            arrayTablist.maximize();
        } else {
            arrayTablist.setColumns(columns);
            arrayTablist.setRows(rows);
        }
        return arrayTablist;
    }

    public void minimizeArrayTablist() {
        storage.arrayTablistOptional.ifPresent(arrayTablist -> {
            arrayTablist.setColumns(0);
        });
    }

    public Optional<SimpleTablist> getSimpleTablistOptional() {
        return storage.simpleTablistOptional;
    }

    public SimpleTablist forceSimpleTablist() {
        return storage.simpleTablistOptional.orElseGet(() -> {
            SimpleTablist newSimpleTablist = null;
            storage.simpleTablistOptional = Optional.of(newSimpleTablist);
            minimizeArrayTablist();
            return newSimpleTablist;
        });
    }

    public void clearSimpleTablist() {
        storage.simpleTablistOptional.ifPresent(simpleTablist -> {
            simpleTablist.clear();
        });
    }

    public boolean areScoresEnabled() {
        return storage.scoresEnabled;
    }

    public void enableScores() {
        if (!areScoresEnabled()) {
            storage.scoresEnabled = true;
            PacketContainer createPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.SCOREBOARD_OBJECTIVE); //Used to get some defaults
            createPacket.getStrings().writeSafely(0, OBJECTIVE_NAME);
            createPacket.getStrings().writeSafely(1, OBJECTIVE_NAME);
            createPacket.getIntegers().writeSafely(0, 0);
            PacketContainer displayPacket = new PacketContainer(PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE);
            displayPacket.getIntegers().writeSafely(0, 0);
            displayPacket.getStrings().writeSafely(0, OBJECTIVE_NAME);
            UtilPacketEvent.sendPacket(createPacket, this, storage.players);
            UtilPacketEvent.sendPacket(displayPacket, this, storage.players);
        }
    }

    public void disableScores() {
        if (areScoresEnabled()) {
            storage.scoresEnabled = false;
            PacketContainer removePacket = new PacketContainer(PacketType.Play.Server.SCOREBOARD_OBJECTIVE);
            removePacket.getStrings().writeSafely(0, OBJECTIVE_NAME);
            removePacket.getStrings().writeSafely(1, OBJECTIVE_NAME);
            removePacket.getIntegers().writeSafely(0, 1);
            UtilPacketEvent.sendPacket(removePacket, this, storage.players);
        }
    }

    void addPlayer(Player player) {
        storage.players.remove(player);
        storage.playerTablistOrVisibility.consume(playerTablist -> playerTablist.addPlayer(player), visibility -> {
            if (!visibility) {
                for (Player objPlayer : Bukkit.getOnlinePlayers()) {
                    UtilPacketEvent.sendPacket(TablistManager.playerInfoPacket(objPlayer, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER), Tablist.class, player);
                }
            }
        });
        storage.simpleTablistOptional.ifPresent(simpleTablist -> simpleTablist.addPlayer(player));
        storage.arrayTablistOptional.ifPresent(arrayTablist -> arrayTablist.addPlayer(player));
    }

    void removePlayer(Player player) {
        storage.players.remove(player);
        storage.playerTablistOrVisibility.consume(playerTablist -> playerTablist.removePlayer(player), visibility -> {
            if (!visibility) {
                for (Player objPlayer : Bukkit.getOnlinePlayers()) {
                    UtilPacketEvent.sendPacket(TablistManager.playerInfoPacket(objPlayer, EnumWrappers.PlayerInfoAction.ADD_PLAYER), Tablist.class, player);
                }
            }
        });
        storage.simpleTablistOptional.ifPresent(simpleTablist -> simpleTablist.removePlayer(player));
        storage.arrayTablistOptional.ifPresent(arrayTablist -> arrayTablist.removePlayer(player));
    }

}
