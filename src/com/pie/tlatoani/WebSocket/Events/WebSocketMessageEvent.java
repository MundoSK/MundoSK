package com.pie.tlatoani.WebSocket.Events;

import mundosk_libraries.java_websocket.WebSocket;
import mundosk_libraries.java_websocket.server.WebSocketServer;

/**
 * Created by Tlatoani on 5/5/17.
 */
public class WebSocketMessageEvent extends WebSocketEvent {
    public final String message;

    public WebSocketMessageEvent(WebSocket webSocket, String message) {
        super(webSocket);
        this.message = message;
    }

    public static class Server extends WebSocketMessageEvent implements WebSocketServerEvent {
        public final WebSocketServer server;

        public Server(WebSocketServer server, WebSocket webSocket, String message) {
            super(webSocket, message);
            this.server = server;
        }

        @Override
        public WebSocketServer getWebSocketServer() {
            return server;
        }
    }
}
