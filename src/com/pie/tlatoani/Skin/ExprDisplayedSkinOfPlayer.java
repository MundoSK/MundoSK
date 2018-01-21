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
 * Created by Tlatoani on 9/18/16.
 */
public class ExprDisplayedSkinOfPlayer extends SimpleExpression<Skin> {
    private Expression<Player> playerExpression;
    private Expression<Player> targetExpression;
    private Expression<Player> excludeExpression;
    private boolean deft;

    @Override
    protected Skin[] get(Event event) {
        Player player = playerExpression.getSingle(event);
        if (!player.isOnline()) {
            return new Skin[0];
        }
        ModifiableProfile profile = ProfileManager.getProfile(playerExpression.getSingle(event));
        if (targetExpression == null) {
            return new Skin[]{profile.getGeneralDisplayedSkin()};
        } else {
            return Arrays
                    .stream(targetExpression.getArray(event))
                    .filter(Player::isOnline)
                    .map(target -> profile.getSpecificProfile(target).getDisplayedSkin())
                    .toArray(Skin[]::new);
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
        return (deft ? "default" : "") + playerExpression + "'s displayed skin" +
                (targetExpression == null
                        ? (excludeExpression == null
                                ? ""
                                : " excluding " + excludeExpression)
                        : " for " + targetExpression);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        playerExpression = (Expression<Player>) expressions[0];
        deft = parseResult.mark == 1;
        targetExpression = (Expression<Player>) expressions[1];
        excludeExpression = (Expression<Player>) expressions[2];
        if (deft && (targetExpression != null || excludeExpression != null)) {
            Skript.error("You cannot specify both 'default' and target players or excluded players!");
            return false;
        }
        return true;
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode){
        Skin skinDelta = null;
        if (mode == Changer.ChangeMode.SET) {
            skinDelta = (Skin) delta[0];
        }
        Player player = playerExpression.getSingle(event);
        if (!player.isOnline()) {
            return;
        }
        ModifiableProfile profile = ProfileManager.getProfile(player);
        if (targetExpression != null) {
            for (Player target : targetExpression.getArray(event)) {
                if (!target.isOnline()) {
                    continue;
                }
                profile.getSpecificProfile(target).setDisplayedSkin(skinDelta);
            }
        } else {
            if (excludeExpression != null) {
                for (Player excludedTarget : excludeExpression.getArray(event)) {
                    if (!excludedTarget.isOnline()) {
                        continue;
                    }
                    ModifiableProfile.Specific specificProfile = profile.getSpecificProfile(excludedTarget);
                    if (specificProfile.displayedSkin == null) {
                        specificProfile.displayedSkin = profile.getGeneralDisplayedSkin();
                    }
                }
            }
            if (deft || excludeExpression != null) {
                profile.setGeneralDisplayedSkin(skinDelta);
            } else {
                profile.consistentlySetDisplayedSkin(skinDelta);
            }
        }
    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(Skin.class);
        }
        if (mode == Changer.ChangeMode.RESET || mode == Changer.ChangeMode.DELETE) {
            return CollectionUtils.array();
        }
        return null;
    }
}
