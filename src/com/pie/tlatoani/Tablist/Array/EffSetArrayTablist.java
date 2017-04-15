package com.pie.tlatoani.Tablist.Array;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Skin.Skin;
import com.pie.tlatoani.Tablist.OldTablist;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 7/13/16.
 */
@Deprecated
public class EffSetArrayTablist extends Effect {
    private Expression<Player> playerExpression;
    private int matchedPattern;
    private Expression<Number> columns;
    private Expression<Number> rows;
    private Expression<Skin> iconExpression;

    @Override
    protected void execute(Event event) {
        Player player = playerExpression.getSingle(event);
        ArrayTablist arrayTablist = OldTablist.getTablistForPlayer(player).arrayTablist;
        if (matchedPattern == 0) {
            arrayTablist.setColumns(0);
        } else if (matchedPattern == 1) {
            int finalColumns = columns == null ? 4 : columns.getSingle(event).intValue();
            int finalRows = rows == null ? 20 : rows.getSingle(event).intValue();
            Skin finalIcon = iconExpression == null ? null : iconExpression.getSingle(event);
            arrayTablist.initialIcon = finalIcon;
            arrayTablist.setColumns(finalColumns);
            arrayTablist.setRows(finalRows);
        }
    }

    @Override
    public String toString(Event event, boolean b) {
        return "activate array tablist for " + playerExpression;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        playerExpression = (Expression<Player>) expressions[0];
        matchedPattern = i;
        if (matchedPattern == 1) {
            columns = (Expression<Number>) expressions[1];
            rows = (Expression<Number>) expressions[2];
            iconExpression = (Expression<Skin>) expressions[3];
        }
        Skript.warning("The 'activate array tablist' effect is deprecated and will be removed in a future version! It is recommended to instead use the specific effects for setting initial icon, column, and row in that order!");
        return true;
    }
}
