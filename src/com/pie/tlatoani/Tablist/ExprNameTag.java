package com.pie.tlatoani.Tablist;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.SkinTexture.SkinManager;
import com.pie.tlatoani.SkinTexture.SkinTexture;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 10/8/16.
 */
public class ExprNameTag extends SimpleExpression<String> {
    private Expression<Player> playerExpression;

    @Override
    protected String[] get(Event event) {
        return new String[]{playerExpression.getSingle(event).getPlayerListName()};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return playerExpression + "'s nametag";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        playerExpression = (Expression<Player>) expressions[0];
        return true;
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode){
        String nameTag = null;
        if (mode == Changer.ChangeMode.SET) {
            Mundo.debug(this, "DELTA 0: " + delta[0]);
            nameTag = (String) delta[0];
        } else if (mode == Changer.ChangeMode.RESET) {
            nameTag = playerExpression.getSingle(event).getName();
        }
        //TabListManager.nameTags.put(playerExpression.getSingle(event).getUniqueId(), nameTag);
        playerExpression.getSingle(event).setPlayerListName(nameTag);
    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(String.class);
        }
        if (mode == Changer.ChangeMode.RESET) {
            return CollectionUtils.array();
        }
        return null;
    }
}
