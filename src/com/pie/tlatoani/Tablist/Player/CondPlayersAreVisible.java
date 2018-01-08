package com.pie.tlatoani.Tablist.Player;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.pie.tlatoani.Tablist.Simple.SimpleTablist;
import com.pie.tlatoani.Tablist.Tablist;
import com.pie.tlatoani.Tablist.TablistManager;
import com.pie.tlatoani.Util.MundoUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.Arrays;

/**
 * Created by Tlatoani on 8/11/16.
 */
public class CondPlayersAreVisible extends SimpleExpression<Boolean> {
    private Expression<Player> playerExpression;
    private boolean positive;

    @Override
    protected Boolean[] get(Event event) {
        return new Boolean[]{MundoUtil.check(playerExpression, event, player ->
                player.isOnline() && TablistManager.getTablistOfPlayer(player).arePlayersVisible()
        , positive)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Boolean> getReturnType() {
        return Boolean.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "players are " + (positive ? "visible" : "hidden") + " in " + playerExpression + "'s tablist";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        playerExpression = (Expression<Player>) expressions[0];
        positive = parseResult.mark == 0;
        return true;
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        Boolean visible = positive == (Boolean) delta[0];
        if (visible) {
            for (Player player : playerExpression.getArray(event)) {
                if (!player.isOnline()) {
                    continue;
                }
                Tablist tablist = TablistManager.getTablistOfPlayer(player);
                if (!tablist.arePlayersVisible() && !tablist.getPlayerTablist().isPresent()) {
                    tablist.setSupplementaryTablist(SimpleTablist::new);
                }
                tablist.getPlayerTablist().ifPresent(PlayerTablist::showAllPlayers);
            }
        } else {
            for (Player player : playerExpression.getArray(event)) {
                if (!player.isOnline()) {
                    continue;
                }
                Tablist tablist = TablistManager.getTablistOfPlayer(player);
                if (tablist.arePlayersVisible() && !tablist.getPlayerTablist().isPresent()) {
                    tablist.setSupplementaryTablist(SimpleTablist::new);
                }
                tablist.getPlayerTablist().ifPresent(PlayerTablist::hideAllPlayers);
            }
        }
    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(Boolean.class);
        }
        return null;
    }
}
