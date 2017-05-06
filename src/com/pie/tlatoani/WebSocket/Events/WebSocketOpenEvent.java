package com.pie.tlatoani.WebSocket.Events;

import mundosk_libraries.org.java_websocket.WebSocket;
import mundosk_libraries.org.java_websocket.server.WebSocketServer;

/**
 * Created by Tlatoani on 5/5/17.
 */
public class WebSocketOpenEvent extends WebSocketEvent {

    public WebSocketOpenEvent(WebSocket webSocket) {
        super(webSocket);
    }

    public static class Server extends WebSocketOpenEvent implements WebSocketServerEvent {
        public final WebSocketServer server;

        public Server(WebSocketServer server, WebSocket webSocket) {
            super(webSocket);
            this.server = server;
        }

        @Override
        public WebSocketServer getWebSocketServer() {
            return server;
        }
    }
}
