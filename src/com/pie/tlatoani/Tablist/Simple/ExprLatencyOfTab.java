package com.pie.tlatoani.Tablist.Simple;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.pie.tlatoani.Tablist.OldTab;
import com.pie.tlatoani.Tablist.OldTablist;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 7/13/16.
 */
public class ExprLatencyOfTab extends SimpleExpression<Number> {
    private Expression<String> id;
    private Expression<OldTablist> tablistExpression;
    private Expression<Player> playerExpression;

    @Override
    protected Number[] get(Event event) {
        OldTablist oldTablist = tablistExpression != null ? tablistExpression.getSingle(event) : OldTablist.getTablistForPlayer(playerExpression.getSingle(event));
        Player player = playerExpression != null ? playerExpression.getSingle(event) : null;
        String id = this.id.getSingle(event);
        OldTab oldTab = oldTablist.simpleTablist.getTabIfVisibleFor(player, id);
        if (oldTab == null) {
            return new Number[0];
        }
        return new Number[]{oldTab.getLatency(player)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "latency of tab id " + id + " for " + playerExpression;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        id = (Expression<String>) expressions[0];
        tablistExpression = (Expression<OldTablist>) expressions[1];
        playerExpression = (Expression<Player>) expressions[2];
        return true;
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        OldTablist oldTablist = tablistExpression != null ? tablistExpression.getSingle(event) : OldTablist.getTablistForPlayer(playerExpression.getSingle(event));
        Player player = playerExpression != null ? playerExpression.getSingle(event) : null;
        String id = this.id.getSingle(event);
        OldTab oldTab = oldTablist.simpleTablist.getTabIfVisibleFor(player, id);
        if (oldTab != null) {
            oldTab.setLatency(player, ((Number) delta[0]).byteValue());
        }
    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(Number.class);
        }
        return null;
    }
}
