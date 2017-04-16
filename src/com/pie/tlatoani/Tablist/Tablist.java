package com.pie.tlatoani.Tablist;

import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.Tablist.Array.ArrayTablist;
import com.pie.tlatoani.Tablist.Player.PlayerTablist;
import com.pie.tlatoani.Tablist.Simple.SimpleTablist;
import com.pie.tlatoani.Util.Either;

import java.util.Optional;

/**
 * Created by Tlatoani on 4/15/17.
 */
public class Tablist {
    private Storage storage = new Storage();

    public static class Storage {
        public Either<PlayerTablist, Boolean> playerTablistOrVisibility = Either.right(true);
        public Optional<ArrayTablist> arrayTablistOptional = Optional.empty();
        public Optional<SimpleTablist> simpleTablistOptional = Optional.empty();
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
        if (storage.playerTablistOrVisibility.getRight().orElse(false)) {
            return;
        }
        storage.playerTablistOrVisibility = Either.right(true);
        //add stuff
    }

    public void hideAllPlayers() {
        if (!storage.playerTablistOrVisibility.getRight().orElse(true)) {
            return;
        }
        storage.playerTablistOrVisibility = Either.right(false);
        //add stuff
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
        arrayTablist.setColumns(Mundo.limitToRange(1, columns, 4));
        arrayTablist.setRows(rows);
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

}
