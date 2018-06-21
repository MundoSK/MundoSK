package com.pie.tlatoani.WebSocket;

import com.pie.tlatoani.Core.Skript.MundoPropertyExpression;
import mundosk_libraries.java_websocket.WebSocket;

/**
 * Created by Tlatoani on 9/3/17.
 */
public class ExprWebSocketState extends MundoPropertyExpression<WebSocket, WebSocket.READYSTATE> {
    @Override
    public WebSocket.READYSTATE convert(WebSocket webSocket) {
        return webSocket.getReadyState();
    }
}
