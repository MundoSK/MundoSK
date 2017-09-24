package com.pie.tlatoani.WebSocket.Events;

import mundosk_libraries.java_websocket.WebSocket;
import mundosk_libraries.java_websocket.server.WebSocketServer;

/**
 * Created by Tlatoani on 5/5/17.
 */
public class WebSocketErrorEvent extends WebSocketEvent {
    public final Exception error;

    public WebSocketErrorEvent(WebSocket webSocket, Exception error) {
        super(webSocket);
        this.error = error;
    }

    public static class Server extends WebSocketErrorEvent implements WebSocketServerEvent {
        public final WebSocketServer server;

        public Server(WebSocketServer server, WebSocket webSocket, Exception error) {
            super(webSocket, error);
            this.server = server;
        }

        @Override
        public WebSocketServer getWebSocketServer() {
            return server;
        }
    }
}
