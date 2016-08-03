package com.pie.tlatoani.Tablist.Array;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.Tablist.SkinTexture.SkinTexture;
import com.pie.tlatoani.Tablist.TabListManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 7/13/16.
 */
public class EffSetArrayTablist extends Effect {
    private Expression<Player> playerExpression;
    private int matchedPattern;
    private Expression<Number> columns;
    private Expression<Number> rows;
    private Expression<SkinTexture> iconExpression;

    @Override
    protected void execute(Event event) {
        Player player = playerExpression.getSingle(event);
        if (matchedPattern == 0) {
            TabListManager.deactivateArrayTabList(player);
        } else if (matchedPattern == 1) {
            Mundo.debug(this, "matchedPattern == 2");
            int finalColumns = columns == null ? 4 : columns.getSingle(event).intValue();
            int finalRows = rows == null ? 20 : rows.getSingle(event).intValue();
            SkinTexture finalIcon = iconExpression == null ? TabListManager.DEFAULT_SKIN_TEXTURE : iconExpression.getSingle(event);
            TabListManager.activateArrayTabList(player, finalColumns, finalRows, finalIcon);
        }
    }

    @Override
    public String toString(Event event, boolean b) {
        return "activate custom tablist for " + playerExpression;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        playerExpression = (Expression<Player>) expressions[0];
        matchedPattern = i;
        if (matchedPattern == 1) {
            columns = (Expression<Number>) expressions[1];
            rows = (Expression<Number>) expressions[2];
            iconExpression = (Expression<SkinTexture>) expressions[3];
        }
        return true;
    }
}
