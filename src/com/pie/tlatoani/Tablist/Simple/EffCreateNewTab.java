package com.pie.tlatoani.Tablist.Simple;

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
public class EffCreateNewTab extends Effect {
    private Expression<String> id;
    private Expression<OldTablist> tablistExpression;
    private Expression<Player> playerExpression;
    private Expression<String> displayName;
    private Expression<Number> ping;
    private Expression<Skin> iconExpression;
    private Expression<Number> score;

    @Override
    protected void execute(Event event) {
        OldTablist oldTablist = tablistExpression != null ? tablistExpression.getSingle(event) : OldTablist.getTablistForPlayer(playerExpression.getSingle(event));
        Player player = playerExpression != null ? playerExpression.getSingle(event) : null;
        oldTablist.simpleTablist.createTab(player, id.getSingle(event), displayName.getSingle(event), (ping == null ? 5 : ping.getSingle(event).byteValue()), (iconExpression == null ? OldTablist.DEFAULT_SKIN_TEXTURE : iconExpression.getSingle(event)), (score == null ? 0 : score.getSingle(event).intValue()));
    }

    @Override
    public String toString(Event event, boolean b) {
        return "create tab id " + id + " for " + playerExpression + " with display name " + displayName + (ping == null ? "" : " latency " + ping) + (iconExpression == null ? "" : " icon " + iconExpression);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        id = (Expression<String>) expressions[0];
        tablistExpression = (Expression<OldTablist>) expressions[1];
        playerExpression = (Expression<Player>) expressions[2];
        displayName = (Expression<String>) expressions[3];
        ping = (Expression<Number>) expressions[4];
        iconExpression = (Expression<Skin>) expressions[5];
        score = (Expression<Number>) expressions[6];
        return true;
    }
}
