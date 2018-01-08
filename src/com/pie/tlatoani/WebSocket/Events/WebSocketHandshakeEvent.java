package com.pie.tlatoani.WebSocket.Events;

import mundosk_libraries.java_websocket.WebSocket;
import mundosk_libraries.java_websocket.handshake.ClientHandshake;
import mundosk_libraries.java_websocket.handshake.ServerHandshake;
import mundosk_libraries.java_websocket.handshake.ServerHandshakeBuilder;
import mundosk_libraries.java_websocket.server.WebSocketServer;

/**
 * Created by Tlatoani on 12/29/17.
 */
public class WebSocketHandshakeEvent extends WebSocketEvent{
    public final ClientHandshake request;
    public final ServerHandshake response;

    private WebSocketHandshakeEvent(WebSocket webSocket, ClientHandshake request, ServerHandshake response) {
        super(webSocket);
        this.request = request;
        this.response = response;
    }

    public static class Client extends WebSocketHandshakeEvent {

        public Client(WebSocket webSocket, ClientHandshake request, ServerHandshake response) {
            super(webSocket, request, response);
        }
    }

    public static class Server extends WebSocketHandshakeEvent implements WebSocketServerEvent {
        public final WebSocketServer server;
        public boolean allowed = true;

        public Server(WebSocketServer server, WebSocket webSocket, ClientHandshake request, ServerHandshakeBuilder response) {
            super(webSocket, request, response);
            this.server = server;
        }

        @Override
        public WebSocketServer getWebSocketServer() {
            return server;
        }

        //Used so that casting is not necessary for users of this class
        public ServerHandshakeBuilder getResponse() {
            return (ServerHandshakeBuilder) response;
        }
    }
}
