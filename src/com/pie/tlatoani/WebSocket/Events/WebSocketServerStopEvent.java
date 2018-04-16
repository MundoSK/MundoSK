package com.pie.tlatoani.WebSocket.Events;

import com.pie.tlatoani.Util.Skript.BaseEvent;
import mundosk_libraries.java_websocket.server.WebSocketServer;

/**
 * Created by Tlatoani on 5/5/17.
 */
public class WebSocketServerStopEvent extends BaseEvent implements WebSocketServerEvent {
    public final WebSocketServer webSocketServer;

    public WebSocketServerStopEvent(WebSocketServer webSocketServer) {
        this.webSocketServer = webSocketServer;
    }

    @Override
    public WebSocketServer getWebSocketServer() {
        return webSocketServer;
    }
}
