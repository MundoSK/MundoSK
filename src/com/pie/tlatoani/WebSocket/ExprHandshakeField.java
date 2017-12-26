package com.pie.tlatoani.WebSocket;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Util.MundoUtil;
import com.pie.tlatoani.WebSocket.Events.WebSocketOpenEvent;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 12/25/17.
 */
public class ExprHandshakeField extends SimpleExpression<String> {
    private Expression<String> keyExpr;

    @Override
    protected String[] get(Event event) {
        String key = keyExpr.getSingle(event);
        return MundoUtil.cast(event, WebSocketOpenEvent.class)
                .map(wsOpenEvent -> new String[]{wsOpenEvent.handshake.getFieldValue(key)})
                .orElse(new String[0]);
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
        return "handshake field " + keyExpr;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        keyExpr = (Expression<String>) expressions[0];
        if (!MundoUtil.isAssignableFromCurrentEvent(WebSocketOpenEvent.class)) {
            Skript.error("The 'value of handshake field' expression can only be used in the 'on open' section of a websocket client or server template!");
            return false;
        }
        return true;
    }
}
