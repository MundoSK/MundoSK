package com.pie.tlatoani.WebSocket;

import com.pie.tlatoani.Core.Skript.MundoPropertyExpression;
import com.pie.tlatoani.Core.Static.OptionalUtil;
import mundosk_libraries.java_websocket.WebSocket;

/**
 * Created by Tlatoani on 9/3/17.
 */
public class ExprWebSocketID extends MundoPropertyExpression<WebSocket, String> {

    @Override
    public String convert(WebSocket webSocket) {
        return OptionalUtil.cast(webSocket, SkriptWebSocketClient.class).map(client -> client.functionality.id).orElse(null);
    }
}
