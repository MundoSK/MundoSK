package com.pie.tlatoani.Tablist.Group;

import ch.njol.skript.lang.Expression;
import com.pie.tlatoani.Tablist.Tablist;
import com.pie.tlatoani.Tablist.TablistManager;
import com.pie.tlatoani.Util.Static.MundoUtil;
import com.pie.tlatoani.Util.Collections.Streamable;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Created by Tlatoani on 3/30/18.
 * Used to generalize the different cases of being provided with
 * a list of players to manipulate the tablists of or a tablist group to maniuplate the tablists of.
 */
public abstract class TablistProvider {

    /**
     * Returns a {@link Stream} of only the tablists necessary to create an appropriate view of the values in the tablist.
     * For a list of players this is identical to calling {@link Streamable#stream()} on the result of {@link #get(Event)},
     * while for a tablist group the returned {@link Stream} will only contain the dummy tablist.
     */
    public abstract Stream<Tablist> view(Event event);

    /**
     * Returns a {@link Streamable} that will contain all tablists pertaining to this TablistProvider.
     */
    public abstract Streamable<Tablist> get(Event event);

    /**
     * Checks {@code condition} against the necessary tablists.
     * For a list of players this will simply call {@link MundoUtil#check(Expression, Event, Function, boolean)},
     * meaning the result is also dependent on whether the list is "and" or "or".
     * For a tablist group this will only check against the dummy tablist.
     * @param condition The condition to check against the tablists.
     * @param positive Whether to negate the condition ({@code positive == false} will cause the condition to be negated)
     * @return
     */
    public abstract boolean check(Event event, Function<Tablist, Boolean> condition, boolean positive);

    /**
     * @return Whether the result of {@link #view(Event)} is guaranteed to have only one element
     */
    public abstract boolean isSingle();

    /**
     * Returns a syntax representation of this TablistProvider.
     * For a list of players this will simply be the list's syntax representation,
     * while for a tablist group this will be {@code "group "} concatenated before the name's syntax representation.
     * @return A syntax representation of this TablistProvider.
     */
    public abstract String toString();

    /**
     * This method simplifies the operation of selecting the present expression out of
     * what could be a player expression and what could be a string expression (for a tablist group name).
     * If {@code expressions[playerExpressionIndex] != null}, that is assumed to be a present player expression
     * and a corresponding TablistProvider is constructed based off of that.
     * Otherwise, {@code expressions[playerExpressionIndex + 1]} is assumed to be a present string expression
     * and a corresponding TablistProvider is constructed based off of that.
     * Note that the length of {@code expressions} may be equal to {@code playerExpressionIndex}
     * (meaning that the desired string expression can not be present)
     * as long as {@code }
     * @param expressions
     * @param playerExpressionIndex
     * @return
     */
    public static TablistProvider of(Expression<?>[] expressions, int playerExpressionIndex) {
        if (expressions[playerExpressionIndex] != null) {
            return new Players((Expression<Player>) expressions[playerExpressionIndex]);
        } else {
            return new Group((Expression<String>) expressions[playerExpressionIndex + 1]);
        }
    }

    private static class Players extends TablistProvider {
        private final Expression<Player> expression;

        private Players(Expression<Player> expression) {
            this.expression = expression;
        }


        @Override
        public Stream<Tablist> view(Event event) {
            return get(event).stream();
        }

        @Override
        public Streamable<Tablist> get(Event event) {
            return () -> new TablistOfPlayerIterator(expression.iterator(event));
        }

        @Override
        public String toString() {
            return expression.toString();
        }

        @Override
        public boolean check(Event event, Function<Tablist, Boolean> condition, boolean positive) {
            return MundoUtil.check(expression, event, player -> player.isOnline() && condition.apply(TablistManager.getTablistOfPlayer(player)), positive);
        }

        @Override
        public boolean isSingle() {
            return expression.isSingle();
        }
    }

    private static class TablistOfPlayerIterator implements Iterator<Tablist> {
        private final Iterator<? extends Player> playerIterator;
        private Player next = null;

        private TablistOfPlayerIterator(Iterator<? extends Player> playerIterator) {
            this.playerIterator = playerIterator;
        }

        @Override
        public boolean hasNext() {
            while (next == null && playerIterator.hasNext()) {
                next = playerIterator.next();
                if (!next.isOnline()) {
                    next = null;
                }
            }
            return next != null;
        }

        @Override
        public Tablist next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            Tablist result = TablistManager.getTablistOfPlayer(next);
            next = null;
            return result;
        }
    }

    private static class Group extends TablistProvider {
        private final Expression<String> expression;

        private Group(Expression<String> expression) {
            this.expression = expression;
        }

        @Override
        public Stream<Tablist> view(Event event) {
            return Stream.of(get(event).getDummy());
        }

        @Override
        public TablistGroup get(Event event) {
            return TablistManager.getTablistGroup(expression.getSingle(event));
        }

        @Override
        public String toString() {
            return "group " + expression;
        }

        @Override
        public boolean check(Event event, Function<Tablist, Boolean> condition, boolean positive) {
            return positive == condition.apply(get(event).getDummy());
        }

        @Override
        public boolean isSingle() {
            return true;
        }
    }
}
