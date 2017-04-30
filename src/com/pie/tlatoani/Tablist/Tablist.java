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
    public static final Skin DEFAULT_SKIN_TEXTURE = Skin.WHITE;
    public static final String OBJECTIVE_NAME = "MundoSK_Tablist";

    public static class Storage {
        public final Tablist tablist;
        public final ArrayList<Player> players = new ArrayList<>();
        public boolean scoresEnabled;
        public Either<PlayerTablist, Boolean> playerTablistOrVisibility = Either.right(true);
        public Optional<ArrayTablist> arrayTablistOptional = Optional.empty();
        public Optional<SimpleTablist> simpleTablistOptional = Optional.empty();

        public Storage(Tablist tablist) {
            this.tablist = tablist;
        }
    }

    public boolean containsPlayers() {
        return !storage.players.isEmpty();
    }

    public Either<PlayerTablist, Boolean> getPlayerTablistOrVisibility() {
        return storage.playerTablistOrVisibility;
    }

    public PlayerTablist forcePlayerTablist() {
        return storage.playerTablistOrVisibility.map(playerTablist -> playerTablist, visibility -> {
            showAllPlayers();
            PlayerTablist playerTablist = null;
            storage.playerTablistOrVisibility = Either.left(playerTablist);
            return playerTablist;
        });
    }

    public void showAllPlayers() {
        storage.playerTablistOrVisibility.consume(PlayerTablist::showAllPlayers, visibility -> {
            if (!visibility) {
                storage.playerTablistOrVisibility = Either.right(true);
                for (Player player : Bukkit.getOnlinePlayers()) {
                    UtilPacketEvent.sendPacket(TablistManager.playerInfoPacket(player, EnumWrappers.PlayerInfoAction.ADD_PLAYER), Tablist.class, storage.players);
                }
            }
        });
    }

    public void hideAllPlayers() {
        storage.playerTablistOrVisibility.consume(PlayerTablist::hideAllPlayers, visibility -> {
            if (visibility) {
                storage.playerTablistOrVisibility = Either.right(false);
                for (Player player : Bukkit.getOnlinePlayers()) {
                    UtilPacketEvent.sendPacket(TablistManager.playerInfoPacket(player, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER), Tablist.class, storage.players);
                }
            }
        });
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
            arrayTablist.setColumns(Mundo.limitToRange(1, columns, 4));
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
