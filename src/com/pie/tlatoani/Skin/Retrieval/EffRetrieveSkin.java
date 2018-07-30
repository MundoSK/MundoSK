package com.pie.tlatoani.Skin.Retrieval;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.*;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Core.Static.Scheduling;
import com.pie.tlatoani.Skin.ProfileManager;
import com.pie.tlatoani.Skin.Skin;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public class EffRetrieveSkin extends Effect {
    private Variable<?> variable;
    private ExprRetrievedSkin expression;

    @Override
    protected TriggerItem walk(Event event) {
        if (expression.mode == ExprRetrievedSkin.RetrieveMode.OFFLINE_PLAYER || expression.mode == ExprRetrievedSkin.RetrieveMode.UUID) {
            OfflinePlayer offlinePlayer;
            if (expression.mode == ExprRetrievedSkin.RetrieveMode.OFFLINE_PLAYER) {
                offlinePlayer = expression.offlinePlayerExpr.getSingle(event);
            } else {
                offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(expression.stringExpr.getSingle(event)));
            }
            if (offlinePlayer.isOnline()) {
                Skin delta = ProfileManager.getProfile(offlinePlayer.getPlayer()).getActualSkin();
                variable.change(event, new Skin[]{delta}, Changer.ChangeMode.SET);
                return getNext();
            } else {
                long timeoutMillis = Optional
                        .ofNullable(expression.timeoutExpr)
                        .flatMap(expr -> Optional.ofNullable(expr.getSingle(event)))
                        .map(Timespan::getMilliSeconds)
                        .orElse((long) ExprRetrievedSkin.DEFAULT_TIMEOUT_MILLIS);
                Scheduling.async(() -> {
                    Skin delta = PlayerSkinRetrieval.retrieveOfflineSkin(offlinePlayer, (int) timeoutMillis);
                    Scheduling.sync(() -> {
                        variable.change(event, new Skin[]{delta}, Changer.ChangeMode.SET);
                        TriggerItem.walk(getNext(), event);
                    });
                });
                return null;
            }
        } else {
            Scheduling.async(() -> {
                Skin delta = expression.getSkin(event);
                Scheduling.sync(() -> {
                    variable.change(event, new Skin[]{delta}, Changer.ChangeMode.SET);
                    TriggerItem.walk(getNext(), event);
                });
            });
            return null;
        }
    }

    @Override
    protected void execute(Event event) {}

    @Override
    public String toString(Event event, boolean b) {
        return expression.toString(event, b).replace("retrieved", "retrieve") + " into " + variable;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        if (!(expressions[expressions.length - 1] instanceof Variable)) {
            Skript.error("The 'retrieve skin' effect can only retrieve into variables!");
            return false;
        }
        variable = (Variable) expressions[expressions.length - 1];
        expression = new ExprRetrievedSkin();
        expression.init(Arrays.copyOfRange(expressions, 0, expressions.length - 1), i, kleenean, parseResult);
        return true;
    }
}
