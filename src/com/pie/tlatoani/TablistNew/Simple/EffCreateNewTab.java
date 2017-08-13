package com.pie.tlatoani.TablistNew.Simple;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Skin.Skin;
import com.pie.tlatoani.TablistNew.OldTablist;
import com.pie.tlatoani.TablistNew.Tablist;
import com.pie.tlatoani.TablistNew.TablistManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.Optional;

/**
 * Created by Tlatoani on 7/13/16.
 */
public class EffCreateNewTab extends Effect {
    private Expression<String> id;
    private Expression<Player> playerExpression;
    private Expression<String> displayName;
    private Optional<Expression<Number>> ping;
    private Optional<Expression<Skin>> iconExpression;
    private Optional<Expression<Number>> score;

    @Override
    protected void execute(Event event) {
        String id = this.id.getSingle(event);
        String displayName = this.displayName.getSingle(event);
        Integer latency = ping.map(expression -> expression.getSingle(event).intValue()).orElse(5);
        Skin icon = iconExpression.map(expression -> expression.getSingle(event)).orElse(Tablist.DEFAULT_SKIN_TEXTURE);
        Integer score = this.score.map(expression -> expression.getSingle(event).intValue()).orElse(0);
        for (Player player : playerExpression.getArray(event)) {
            Tablist tablist = TablistManager.getTablistOfPlayer(player);
            if (tablist.getSupplementaryTablist() instanceof SimpleTablist) {
                SimpleTablist simpleTablist = (SimpleTablist) tablist.getSupplementaryTablist();
                simpleTablist.createTab(id, displayName, latency, icon, score);
            }
        }
    }

    @Override
    public String toString(Event event, boolean b) {
        return "create simple tab " + id + " for " + playerExpression + " with display name " + displayName + (ping == null ? "" : " latency " + ping) + (iconExpression == null ? "" : " icon " + iconExpression);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        id = (Expression<String>) expressions[0];
        playerExpression = (Expression<Player>) expressions[1];
        displayName = (Expression<String>) expressions[2];
        ping = Optional.ofNullable((Expression<Number>) expressions[3]);
        iconExpression = Optional.ofNullable((Expression<Skin>) expressions[4]);
        score = Optional.ofNullable((Expression<Number>) expressions[5]);
        return true;
    }
}
