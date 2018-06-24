package com.pie.tlatoani.Tablist.Group;

import com.google.common.collect.Iterators;
import com.pie.tlatoani.Tablist.Tablist;
import com.pie.tlatoani.Tablist.TablistManager;
import com.pie.tlatoani.Util.Collections.Streamable;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Stream;

/**
 * Created by Tlatoani on 3/3/18.
 * Used to provide a simple way to put multiple tablist modifications on a certain group of players,
 * while being able to easily add a player to the group and have the modifications be automatically applied to them
 */
public class TablistGroup implements Streamable<Tablist> {
    private Tablist dummy = new Tablist();
    private final Map<Player, Tablist> tablists = new HashMap<>();

    /**
     * Returns the dummy {@link Tablist} used to contain the modifications done to this group.
     * This method should only be used to view the modifications done to this group
     * and never to apply certain modifications only to the dummy {@link Tablist}.
     * @return The dummy {@link Tablist} of this group
     */
    public Tablist getDummy() {
        return dummy;
    }

    @Override
    public Iterator<Tablist> iterator() {
        return Iterators.concat(Collections.singleton(dummy).iterator(), tablists.values().iterator());
    }

    @Override
    public Stream<Tablist> stream() {
        return Stream.concat(Stream.of(dummy), tablists.values().stream());
    }

    /**
     * Returns an unmodifable view of the players in this tablist group.
     * @return An unmodifiable {@link Set} containing all of the players in this tablist group.
     */
    public Set<Player> getPlayers() {
        return Collections.unmodifiableSet(tablists.keySet());
    }

    /**
     * Adds {@code player} to this tablist group, applying the necessary modifications to their tablist.
     * @param player The player to be added to the tablist group.
     * @throws IllegalArgumentException If {@code player} is null or offline
     */
    public void add(Player player) {
        if (player == null || !player.isOnline()) {
            throw new IllegalArgumentException(
                    "The player parameter in add(Player player) must be non-null and online, player: " + player);
        }
        tablists.computeIfAbsent(player, __ -> {
            Tablist tablist = TablistManager.getTablistOfPlayer(player);
            dummy.applyChanges(tablist);
            return tablist;
        });
    }

    /**
     * Removes {@code player} from this tablist group.
     * @param player The player to be removed from the tablist group.
     * @return {@code true} if {@code player} was in the tablist group, {@code false} otherwise
     * @throws IllegalArgumentException If {@code player} is null or offline
     */
    public boolean remove(Player player) {
        if (player == null || !player.isOnline()) {
            throw new IllegalArgumentException(
                    "The player parameter in remove(Player player) must be non-null and online, player: " + player);
        }
        return tablists.remove(player) != null;
    }

    /**
     * Removes all players from this tablist group.
     */
    public void clear() {
        tablists.clear();
    }

    /**
     * Removes all players from this tablist group and replaces the dummy tablist with a new instance,
     * thus removing all tablist modifications from this group.
     */
    public void reset() {
        clear();
        dummy = new Tablist();
    }
}
