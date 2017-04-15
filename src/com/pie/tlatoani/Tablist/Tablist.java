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
    private Either<PlayerTablist, Boolean> playerTablistOrVisibility = Either.right(true);
    private Optional<ArrayTablist> arrayTablistOptional = Optional.empty();
    private Optional<SimpleTablist> simpleTablistOptional = Optional.empty();

    public Either<PlayerTablist, Boolean> getPlayerTablistOrVisibility() {
        return playerTablistOrVisibility;
    }

    public PlayerTablist forcePlayerTablist() {
        return playerTablistOrVisibility.map(playerTablist -> playerTablist, visibility -> {
            showAllPlayers();
            PlayerTablist playerTablist = null;
            playerTablistOrVisibility = Either.left(playerTablist);
            return playerTablist;
        });
    }

    public void showAllPlayers() {
        if (playerTablistOrVisibility.getRight().orElse(false)) {
            return;
        }
        playerTablistOrVisibility = Either.right(true);
        //add stuff
    }

    public void hideAllPlayers() {
        if (!playerTablistOrVisibility.getRight().orElse(true)) {
            return;
        }
        playerTablistOrVisibility = Either.right(false);
        //add stuff
    }

    public Optional<ArrayTablist> getArrayTablistOptional() {
        return arrayTablistOptional;
    }

    public ArrayTablist showArrayTablist(int columns, int rows) {
        ArrayTablist arrayTablist = arrayTablistOptional.orElseGet(() -> {
            boolean canShowPlayers = columns == 4 && rows == 20;
            if (canShowPlayers) {
                showAllPlayers();
            } else {
                hideAllPlayers();
            }
            ArrayTablist newArrayTablist = null;
            arrayTablistOptional = Optional.of(newArrayTablist);
            return newArrayTablist;
        });
        arrayTablist.setColumns(Mundo.limitToRange(1, columns, 4));
        arrayTablist.setRows(rows);
        return arrayTablist;
    }

    public void minimizeArrayTablist() {
        arrayTablistOptional.ifPresent(arrayTablist -> {
            if (arrayTablist.getColumns() > 0) {
                arrayTablist.setColumns(0);
            }
        });
    }

    public Optional<SimpleTablist> getSimpleTablistOptional() {
        return simpleTablistOptional;
    }

    public SimpleTablist forceSimpleTablist() {
        return simpleTablistOptional.orElseGet(() -> {
            SimpleTablist newSimpleTablist = null;
            simpleTablistOptional = Optional.of(newSimpleTablist);
            return newSimpleTablist;
        });
    }

}
