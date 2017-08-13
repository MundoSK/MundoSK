package com.pie.tlatoani.TablistNew.Array;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.pie.tlatoani.Skin.Skin;
import com.pie.tlatoani.TablistNew.OldTab;
import com.pie.tlatoani.TablistNew.OldTablist;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 7/25/16.
 */
public class ExprIconOfTab extends SimpleExpression<Skin> {
    private Expression<OldTablist> tablistExpression;
    private Expression<Player> playerExpression;
    private Expression<Number> column;
    private Expression<Number> row;
    private int pattern;

    @Override
    protected Skin[] get(Event event) {
        OldTablist oldTablist = tablistExpression != null ? tablistExpression.getSingle(event) : OldTablist.getTablistForPlayer(playerExpression.getSingle(event));
        Player player = playerExpression != null ? playerExpression.getSingle(event) : null;
        if (pattern == 0) {
            return new Skin[]{oldTablist.arrayTablist.initialIcon};
        } else {
            OldTab oldTab = oldTablist.arrayTablist.getOldTab(column.getSingle(event).intValue(), row.getSingle(event).intValue());
            return new Skin[]{oldTab.getIcon(player)};
        }
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Skin> getReturnType() {
        return Skin.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return pattern == 0 ? "icon of tab " + column + ", " + row + " for " + playerExpression : "initial icon of " + playerExpression + "'s array tablist";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        if ((pattern = i) == 0) {
            column = (Expression<Number>) expressions[0];
            row = (Expression<Number>) expressions[1];
            tablistExpression = (Expression<OldTablist>) expressions[2];
            playerExpression = (Expression<Player>) expressions[3];
        } else {
            tablistExpression = (Expression<OldTablist>) expressions[0];
            playerExpression = (Expression<Player>) expressions[1];
        }
        return true;
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        OldTablist oldTablist = tablistExpression != null ? tablistExpression.getSingle(event) : OldTablist.getTablistForPlayer(playerExpression.getSingle(event));
        Player player = playerExpression != null ? playerExpression.getSingle(event) : null;
        if (pattern == 0) {
            OldTab oldTab = oldTablist.arrayTablist.getOldTab(column.getSingle(event).intValue(), row.getSingle(event).intValue());
            oldTab.setIcon(player, (Skin) delta[0]);
        } else {
            oldTablist.arrayTablist.initialIcon = (Skin) delta[0];
        }
    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(Skin.class);
        }
        return null;
    }
}
