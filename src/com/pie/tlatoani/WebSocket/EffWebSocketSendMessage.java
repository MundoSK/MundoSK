package com.pie.tlatoani.WebSocket;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.WebSocket.Events.WebSocketEvent;
import mundosk_libraries.java_websocket.WebSocket;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 5/5/17.
 */
public class EffWebSocketSendMessage extends Effect {
    private Expression<WebSocket> webSocketExpr;
    private Expression<String> messageExpr;

    @Override
    protected void execute(Event event) {
        String[] messages = messageExpr.getArray(event);
        if (webSocketExpr == null) {
            WebSocket webSocket = ((WebSocketEvent) event).webSocket;
            for (String message : messages) {
                webSocket.send(message);
            }
        } else {
            for (WebSocket webSocket : webSocketExpr.getArray(event)) {
                for (String message : messages) {
                    webSocket.send(message);
                }
            }
        }
    }

    @Override
    public String toString(Event event, boolean b) {
        return "websocket send " + messageExpr + (webSocketExpr == null ? "" : " through " + webSocketExpr);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        messageExpr = (Expression<String>) expressions[0];
        webSocketExpr = (Expression<WebSocket>) expressions[1];
        /*if (webSocketExpr == null && !Utilities.isAssignableFromCurrentEvent(WebSocketEvent.class)) {
            Skript.error("'websocket send %string%' can only be used under 'websocket server' and 'websocket client'!");
            return false;
        }*/
        return true;
    }
}
