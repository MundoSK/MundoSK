package com.pie.tlatoani.TablistNew;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 1/17/17.
 */
public class ExprTablist extends SimpleExpression<OldTablist> {
    Expression<Player> playerExpression;

    @Override
    protected OldTablist[] get(Event event) {
        return new OldTablist[]{OldTablist.getTablistForPlayer(playerExpression.getSingle(event))};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends OldTablist> getReturnType() {
        return OldTablist.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return playerExpression + "'s tablist";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        playerExpression = (Expression<Player>) expressions[0];
        return true;
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        OldTablist.setTablistOfPlayer(playerExpression.getSingle(event), (OldTablist) delta[0]);
    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(OldTablist.class);
        }
        return null;
    }
}
