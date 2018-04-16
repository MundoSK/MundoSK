package com.pie.tlatoani.Skin;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.Arrays;

/**
 * Created by Tlatoani on 10/28/16.
 */
public class ExprNameTagOfPlayer extends SimpleExpression<String> {
    private Expression<Player> playerExpression;
    private Expression<Player> targetExpression;
    private boolean deft;

    @Override
    protected String[] get(Event event) {
        Player player = playerExpression.getSingle(event);
        if (player == null || !player.isOnline()) {
            return new String[0];
        }
        ModifiableProfile profile = ProfileManager.getProfile(player);
        if (targetExpression == null) {
            return new String[]{profile.getGeneralNametag()};
        } else {
            return Arrays
                    .stream(targetExpression.getArray(event))
                    .filter(Player::isOnline)
                    .map(target -> profile.getSpecificProfile(target).getNametag())
                    .toArray(String[]::new);
        }
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
        return playerExpression + "'s " + (deft ? "default " : "") + "nametag" + (targetExpression == null ? "" : " for " + targetExpression);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        playerExpression = (Expression<Player>) expressions[0];
        targetExpression = (Expression<Player>) expressions[1];
        deft = parseResult.mark == 1;
        if (deft && targetExpression != null) {
            Skript.error("You cannot specify both 'default' and target players!");
            return false;
        }
        return true;
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        String nameTag = null;
        Player player = playerExpression.getSingle(event);
        if (mode == Changer.ChangeMode.SET) {
            nameTag = (String) delta[0];
        }
        if (player == null || !player.isOnline()) {
            return;
        }
        ModifiableProfile profile = ProfileManager.getProfile(player);
        if (targetExpression != null) {
            for (Player target : targetExpression.getArray(event)) {
                if (!target.isOnline()) {
                    continue;
                }
                profile.getSpecificProfile(target).setNametag(nameTag);
            }
        } else if (deft) {
            profile.setGeneralNametag(nameTag);
        } else {
            profile.consistentlySetNametag(nameTag);
        }
    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.RESET) {
            return CollectionUtils.array(String.class);
        }
        return null;
    }
}
