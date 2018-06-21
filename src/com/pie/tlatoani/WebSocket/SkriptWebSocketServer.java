package com.pie.tlatoani.WebSocket;

import ch.njol.skript.lang.TriggerItem;
import com.pie.tlatoani.Core.Static.Logging;
import com.pie.tlatoani.WebSocket.Events.*;
import mundosk_libraries.java_websocket.WebSocket;
import mundosk_libraries.java_websocket.drafts.Draft;
import mundosk_libraries.java_websocket.exceptions.InvalidDataException;
import mundosk_libraries.java_websocket.framing.CloseFrame;
import mundosk_libraries.java_websocket.handshake.ClientHandshake;
import mundosk_libraries.java_websocket.handshake.ServerHandshakeBuilder;
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
        Logging.debug(this, functionality.toString());
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        functionality.onOpen.ifPresent(triggerItem -> TriggerItem.walk(triggerItem, new WebSocketOpenEvent.Server(this, conn, handshake)));
    }

    @Override
    public ServerHandshakeBuilder onWebsocketHandshakeReceivedAsServer(WebSocket conn, Draft draft, ClientHandshake request) throws InvalidDataException {
        ServerHandshakeBuilder response = super.onWebsocketHandshakeReceivedAsServer(conn, draft, request);
        WebSocketHandshakeEvent.Server event = new WebSocketHandshakeEvent.Server(this, conn, request, response);
        functionality.onHandshake.ifPresent(triggerItem -> TriggerItem.walk(triggerItem, event));
        if (!event.allowed) {
            throw new InvalidDataException(CloseFrame.REFUSE);
        }
        return response;
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        functionality.onClose.ifPresent(triggerItem -> TriggerItem.walk(triggerItem, new WebSocketCloseEvent.Server(this, conn, code, reason, remote)));
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        functionality.onMessage.ifPresent(triggerItem -> TriggerItem.walk(triggerItem, new WebSocketMessageEvent.Server(this, conn, message)));
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        conn.getReadyState();
        functionality.onError.ifPresent(triggerItem -> TriggerItem.walk(triggerItem, new WebSocketErrorEvent.Server(this, conn, ex)));
    }

    @Override
    public void onStart() {
        functionality.onStart.ifPresent(triggerItem -> TriggerItem.walk(triggerItem, new WebSocketServerStartEvent(this)));
    }

    @Override
    public void stop(int timeout) throws InterruptedException {
        super.stop(timeout);
        functionality.onStop.ifPresent(triggerItem -> TriggerItem.walk(triggerItem, new WebSocketServerStopEvent(this)));
    }
}
