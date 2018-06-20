package com.pie.tlatoani.Tablist;

import com.pie.tlatoani.Skin.Skin;
import com.pie.tlatoani.Tablist.Player.PlayerTablist;

/**
 * Created by Tlatoani on 6/25/17.
 * Controls all tabs that are not connected to an actual player
 * and uses the {@link PlayerTablist} to manipulate player-connected tabs as necessary.
 */
public interface SupplementaryTablist<T extends SupplementaryTablist<T>> {

    /**
     * Removes any non-player-connected tabs from the tablist containing this SupplementaryTablist.
     */
    void disable();

    /**
     * Determines whether any code with access to the {@link Tablist}
     * should be allowed to access the {@link PlayerTablist} to make modifications to player-connected tabs.
     * @return {@code true} to permit external modification, {@code false} to forbid it
     */
    boolean allowExternalPlayerTabModification();

    /**
     * Called by {@link Tablist#applyChanges(Tablist)}
     * if the other tablist's SupplementaryTablist is not of the same class as this one.
     * Creates and returns a SupplementaryTablist of the same class as this one
     * at the most basic level necessary to emulate this one
     * (as {@link #applyChanges(SupplementaryTablist)} will be called on it immediately after).
     * @param otherPlayerTablist The {@link PlayerTablist} owned by the tablist that will own the returned SupplementaryTablist
     * @return A SupplementaryTablist at the most basic level needed to emulate this one
     */
    T basicClone(PlayerTablist otherPlayerTablist);

    /**
     * Called by {@link Tablist#applyChanges(Tablist)}.
     * Applies any changes made to this SupplementaryTablist to the argument.
     * @param t The SupplementaryTablist to apply changes to
     */
    void applyChanges(T t);

    /**
     * Called by {@link Tablist#setDefaultIcon(Skin)} in order to display the new default icon.
     * Calls {@link Tab#refresh()} on all supplementary tabs that have an empty icon.
     */
    void refreshIconsIfDefault();
}
