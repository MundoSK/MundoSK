package com.pie.tlatoani.WebSocket;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Util.Static.Logging;
import mundosk_libraries.java_websocket.WebSocket;
import mundosk_libraries.java_websocket.handshake.Handshakedata;
import org.bukkit.event.Event;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tlatoani on 5/5/17.
 */
public class ExprNewWebSocket extends SimpleExpression<WebSocket> {
    private Expression<String> idExpr;
    private Expression<String> uriExpr;
    private Expression<Handshakedata> handshakeExpr;

    @Override
    protected WebSocket[] get(Event event) {
        WebSocketClientFunctionality functionality = WebSocketManager.getClientFunctionality(idExpr.getSingle(event));
        URI uri;
        try {
            uri = new URI(uriExpr.getSingle(event));
        } catch (URISyntaxException e) {
            Logging.reportException(this, e);
            return null;
        }
        SkriptWebSocketClient webSocket;
        if (handshakeExpr == null) {
            webSocket = new SkriptWebSocketClient(functionality, uri);
        } else {
            Map<String, String> headers = new HashMap<>();
            Handshakedata handshake = handshakeExpr.getSingle(event);
            handshake.iterateHttpFields().forEachRemaining(name -> {
                headers.put(name, handshake.getFieldValue(name));
            });
            webSocket = new SkriptWebSocketClient(functionality, uri, headers);
        }
        webSocket.connect();
        return new WebSocket[]{webSocket};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends WebSocket> getReturnType() {
        return SkriptWebSocketClient.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "websocket " + idExpr + " connected to uri " + uriExpr + (handshakeExpr == null ? "" : " with request " + handshakeExpr);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        idExpr = (Expression<String>) expressions[0];
        uriExpr = (Expression<String>) expressions[1];
        handshakeExpr = (Expression<Handshakedata>) expressions[2];
        return true;
    }
}
