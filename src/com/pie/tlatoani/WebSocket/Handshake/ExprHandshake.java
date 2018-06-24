package com.pie.tlatoani.WebSocket.Handshake;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Core.Static.Utilities;
import com.pie.tlatoani.WebSocket.Events.WebSocketHandshakeEvent;
import com.pie.tlatoani.WebSocket.Events.WebSocketOpenEvent;
import mundosk_libraries.java_websocket.handshake.Handshakedata;
import mundosk_libraries.java_websocket.handshake.HandshakedataImpl1;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 12/30/17.
 */
public class ExprHandshake extends SimpleExpression<Handshakedata> {
    private Type type;

    public enum Type {
        REQUEST("websocket request handshake"),
        RESPONSE("websocket response handshake"),
        NEW("new websocket handshake");

        public final String fullSyntax;

        Type(String fullSyntax) {
            this.fullSyntax = fullSyntax;
        }
    }

    @Override
    protected Handshakedata[] get(Event event) {
        Handshakedata handshake;
        if (type == Type.REQUEST) {
            if (event instanceof WebSocketOpenEvent.Server) {
                handshake = ((WebSocketOpenEvent.Server) event).request;
            } else if (event instanceof WebSocketHandshakeEvent) {
                handshake = ((WebSocketHandshakeEvent) event).request;
            } else {
                throw new IllegalArgumentException("Illegal class of event: " + event);
            }
        } else if (type == Type.RESPONSE) {
            if (event instanceof WebSocketOpenEvent.Client) {
                handshake = ((WebSocketOpenEvent.Client) event).response;
            } else if (event instanceof WebSocketHandshakeEvent) {
                handshake = ((WebSocketHandshakeEvent) event).response;
            } else {
                throw new IllegalArgumentException("Illegal class of event: " + event);
            }
        } else {
            handshake = new HandshakedataImpl1();
        }
        return new Handshakedata[]{handshake};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Handshakedata> getReturnType() {
        return Handshakedata.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return type.fullSyntax;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        type = Type.values()[i];
        if (type == Type.REQUEST) {
            if (!Utilities.isAssignableFromCurrentEvent(WebSocketOpenEvent.Server.class, WebSocketHandshakeEvent.class)) {
                Skript.error("The 'websocket request handshake' expression can only be used in the 'on open' section of a 'websocket server' template "
                            + "or the 'on request' section of a 'websocket client' or 'websocket server' template!");
                return false;
            }
        } else if (type == Type.RESPONSE) {
            if (!Utilities.isAssignableFromCurrentEvent(WebSocketOpenEvent.Client.class, WebSocketHandshakeEvent.class)) {
                Skript.error("The 'websocket response handshake' expression can only be used in the 'on open' section of a 'websocket client' template "
                        + "or the 'on request' section of a 'websocket client' or 'websocket server' template!");
                return false;
            }
        }
        return true;
    }
}
