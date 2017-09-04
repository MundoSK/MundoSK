package com.pie.tlatoani.WebSocket;

import com.pie.tlatoani.Util.MundoPropertyExpression;
import com.pie.tlatoani.Util.MundoUtil;
import mundosk_libraries.java_websocket.WebSocket;

import java.net.InetSocketAddress;

/**
 * Created by Tlatoani on 9/3/17.
 */
public class ExprWebSocketID extends MundoPropertyExpression<WebSocket, String> {

    @Override
    public String convert(WebSocket webSocket) {
        return MundoUtil.cast(webSocket, SkriptWebSocketClient.class).map(client -> client.functionality.id).orElse(null);
    }
}
