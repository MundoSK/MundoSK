package com.pie.tlatoani.Skin.Retrieval;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Skin.Skin;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;

import java.io.File;
import java.util.UUID;

/**
 * Created by Tlatoani on 5/6/17.
 * This expression should always be evaluated in async
 */
public class ExprRetrievedSkin extends SimpleExpression<Skin> {
    Expression<String> stringExpr;
    Expression<OfflinePlayer> offlinePlayerExpr;
    Expression<Timespan> timeoutExpr;
    RetrieveMode mode;
    boolean def;

    public static final int DEFAULT_TIMEOUT_MILLIS = 10000;

    public enum RetrieveMode {
        FILE, URL, OFFLINE_PLAYER, UUID
    }

    Skin getSkin(Event event) {
        int timeoutMillis = timeoutExpr == null ? DEFAULT_TIMEOUT_MILLIS : new Long(timeoutExpr.getSingle(event).getMilliSeconds()).intValue();
        switch (mode) {
            case FILE: return MineSkinClient.fromMineSkinString(MineSkinClient.mineSkinFromFile(new File(stringExpr.getSingle(event)), timeoutMillis, def));
            case URL: return MineSkinClient.fromMineSkinString(MineSkinClient.mineSkinFromUrl(stringExpr.getSingle(event), timeoutMillis, def));
            case OFFLINE_PLAYER: return PlayerSkinRetrieval.retrieveSkin(offlinePlayerExpr.getSingle(event), timeoutMillis);
            case UUID: return PlayerSkinRetrieval.retrieveSkin(Bukkit.getOfflinePlayer(UUID.fromString(stringExpr.getSingle(event))), timeoutMillis);
        }
        throw new IllegalStateException("RetrieveMode = " + mode);
    }

    @Override
    protected Skin[] get(Event event) {
        return new Skin[]{getSkin(event)};
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
        switch (mode) {
            case FILE: return "retrieved " + (def ? "" : "slim ") + "skin from file " + stringExpr;
            case URL: return "retrieved " + (def ? "" : "slim ") + "skin from url " + stringExpr;
            case OFFLINE_PLAYER: return "retrieved skin of " + offlinePlayerExpr;
            case UUID: return "retrieved skin from uuid " + stringExpr;
        }
        throw new IllegalStateException("RetrieveMode = " + mode);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        mode = RetrieveMode.values()[parseResult.mark & 0b11];
        def = (parseResult.mark & 0b100) == 0;
        if (mode == RetrieveMode.OFFLINE_PLAYER || mode == RetrieveMode.UUID) {
            offlinePlayerExpr = (Expression<OfflinePlayer>) expressions[0];
            stringExpr = (Expression<String>) expressions[1];
        } else {
            stringExpr = (Expression<String>) expressions[0];
            offlinePlayerExpr = null;
        }
        timeoutExpr = (Expression<Timespan>) expressions[expressions.length - 1];
        return true;
    }
}
