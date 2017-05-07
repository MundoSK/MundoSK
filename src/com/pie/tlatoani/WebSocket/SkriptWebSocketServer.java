package com.pie.tlatoani.WebSocket;

import ch.njol.skript.lang.TriggerItem;
import com.pie.tlatoani.WebSocket.Events.*;
import mundosk_libraries.java_websocket.WebSocket;
import mundosk_libraries.java_websocket.handshake.ClientHandshake;
import mundosk_libraries.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

/**
 * Created by Tlatoani on 5/5/17.
 */
public class SkriptWebSocketServer extends WebSocketServer {
    public final WebSocketServerFunctionality functionality;

    public SkriptWebSocketServer(WebSocketServerFunctionality functionality, int port) {
        super(new InetSocketAddress(port));
        this.functionality = functionality;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        TriggerItem.walk(functionality.onOpen, new WebSocketOpenEvent.Server(this, conn));
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        TriggerItem.walk(functionality.onClose, new WebSocketCloseEvent.Server(this, conn));
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        TriggerItem.walk(functionality.onMessage, new WebSocketMessageEvent.Server(this, conn, message));
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        TriggerItem.walk(functionality.onError, new WebSocketErrorEvent.Server(this, conn, ex));
    }

    @Override
    public void onStart() {
        TriggerItem.walk(functionality.onStart, new WebSocketServerStartEvent(this));
    }

    @Override
    public void stop(int timeout) throws InterruptedException {
        super.stop(timeout);
        TriggerItem.walk(functionality.onStop, new WebSocketServerStopEvent(this));
    }
}
