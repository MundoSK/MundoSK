package com.pie.tlatoani.WebSocket.Handshake;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.pie.tlatoani.Core.Static.Utilities;
import com.pie.tlatoani.WebSocket.Events.WebSocketHandshakeEvent;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 12/30/17.
 */
public class CondRequestIsAccepted extends SimpleExpression<Boolean> {
    private boolean accepted;

    @Override
    protected Boolean[] get(Event event) {
        if (event instanceof WebSocketHandshakeEvent.Server) {
            return new Boolean[]{((WebSocketHandshakeEvent.Server) event).allowed == accepted};
        }
        throw new IllegalArgumentException("Illegal class of event: " + event);
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Boolean> getReturnType() {
        return Boolean.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "websocket handshake request is " + (accepted ? "accepted" : "refused");
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        accepted = parseResult.mark == 0;
        if (!Utilities.isAssignableFromCurrentEvent(WebSocketHandshakeEvent.Server.class)) {
            Skript.error("The '" + toString(null, false) + "' expression can only be used in the 'on handshake' section of a 'websocket server' template");
            return false;
        }
        return true;
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        if (event instanceof WebSocketHandshakeEvent.Server) {
            Boolean value = (Boolean) delta[0];
            ((WebSocketHandshakeEvent.Server) event).allowed = (value == accepted);
        } else {
            throw new IllegalArgumentException("Illegal class of event: " + event);
        }
    }

    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) return CollectionUtils.array(Boolean.class);
        return null;
    }
}
