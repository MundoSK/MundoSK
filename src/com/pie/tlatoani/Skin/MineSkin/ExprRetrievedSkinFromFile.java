package com.pie.tlatoani.Skin.MineSkin;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Skin.Skin;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;

import java.io.File;

/**
 * Created by Tlatoani on 5/6/17.
 * This expression should always be evaluated in async
 */
public class ExprRetrievedSkinFromFile extends SimpleExpression<Skin> {
    private Expression<String> stringExpr;
    private Expression<OfflinePlayer> offlinePlayerExpr;
    private int mode;
    public static int FILE = 0;
    public static int URL = 1;
    public static int OFFLINE_PLAYER = 2;

    private String getRawString(Event event) {
        switch (mode) {
            case 0: return MineSkinClient.rawStringFromFile(new File(stringExpr.getSingle(event)));
            case 1: return MineSkinClient.rawStringFromURL(stringExpr.getSingle(event));
            case 2: return MineSkinClient.rawStringFromUUID(offlinePlayerExpr.getSingle(event).getUniqueId());
        }
        throw new IllegalStateException("Mode = " + mode + ", should be 0, 1, 2");
    }

    @Override
    protected Skin[] get(Event event) {
        String raw = getRawString(event);
        return new Skin[]{MineSkinClient.fromRawString(raw)};
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
            case 0: return "retrieved skin from file " + stringExpr;
            case 1: return "retrieved skin from url " + stringExpr;
            case 2: return "retrieved skin of " + offlinePlayerExpr;
        }
        throw new IllegalStateException("Mode = " + mode + ", should be 0, 1, 2");
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        mode = parseResult.mark;
        stringExpr = (Expression<String>) expressions[0];
        offlinePlayerExpr = (Expression<OfflinePlayer>) expressions[1];
        return true;
    }
}
