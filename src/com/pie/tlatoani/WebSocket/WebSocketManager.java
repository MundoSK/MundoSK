package com.pie.tlatoani.WebSocket;

import ch.njol.skript.classes.Comparator;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.ExpressionType;
import com.pie.tlatoani.Throwable.ThrowableMundo;
import com.pie.tlatoani.Util.Logging;
import com.pie.tlatoani.Registration.Registration;
import com.pie.tlatoani.WebSocket.Events.*;
import com.pie.tlatoani.WebSocket.Handshake.*;
import mundosk_libraries.java_websocket.WebSocket;
import mundosk_libraries.java_websocket.handshake.Handshakedata;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tlatoani on 5/4/17.
 */
public final class WebSocketManager {
    private static Map<String, WebSocketClientFunctionality> clientFunctionalities = new HashMap<>();
    private static Map<String, WebSocketServerFunctionality> serverFunctionalities = new HashMap<>();
    private static Map<Integer, SkriptWebSocketServer> servers = new HashMap<>();
    
    public static void load() {

        Registration.registerType(WebSocket.class, "websocket")
                .document("WebSocket", "1.8", "A websocket object representing one end of a WebSocket connection that can be used to transmit informations between multiple servers and other online services.")
                .defaultExpression(new EventValueExpression<>(WebSocket.class));
        Registration.registerType(Handshakedata.class, "handshake");
        Registration.registerEnum(WebSocket.READYSTATE.class, "websocketstate", WebSocket.READYSTATE.values())
                .pair("NOT YET CONNECTED", WebSocket.READYSTATE.NOT_YET_CONNECTED)
                .document("WebSocketState", "1.8", "A state that a websocket connection can be in.");

        Registration.registerComparator(WebSocket.class, WebSocket.READYSTATE.class, false,
                ((webSocket, readystate) -> Comparator.Relation.get(webSocket.getReadyState() == readystate)));

        Registration.registerEffect(EffCloseWebSocket.class, "close websocket %websocket% [with message %-string%]")
                .document("Close WebSocket", "1.8", "Closes the specified websocket connection, optionally specifying a closing message to send.");
        Registration.registerEffect(EffWebSocketSendMessage.class, "websocket send %strings% [through %-websockets%]")
                .document("WebSocket Send", "1.8", "Sends the specified messages through the specified websockets.");
        Registration.registerEffect(EffStartWebSocketServer.class, "start websocket server %string% at port %number%")
                .document("Start WebSocket Server", "1.8", "Starts a WebSocket server using the specified server template at the specified port. "
                        + "A WebSocket server allows other servers/online services to initiate websocket connections with the server.");
        Registration.registerEffect(EffStopWebSocketServer.class, "stop websocket server at port %number% [with timeout %-number%]")
                .document("Stop WebSocket Server", "1.8", "Stops the WebSocket server at the specified port, optionally specifying a timeout in milliseconds.");

        Registration.registerEvent("WebSocket Client", ScopeWebSocketClient.class, WebSocketEvent.class, "websocket client %string%")
                .document("WebSocket Client Template", "1.8", "Not an actual event, but rather a template for a websocket client, with the specified ID. "
                        + "Under the main \"event\" line you can have four different sub-scopes that handle websocket events:"
                        + "\non open: This is called when the websocket connection initially opens."
                        + "\non message: This is called when the other end of the websocket connection sends a message."
                        + "\non error: This is called when an error occurs related to the websocket connection."
                        + "\non close: This is called when the websocket connection is closed.")
                .eventValue(WebSocket.class, "1.8", "The websocket object being controlled by this template.")
                .eventValue(String.class, "1.8", "In 'on message', this is the received message. In 'on close', this is the reason for closing.")
                .eventValue(ThrowableMundo.class, "1.8", "In 'on error', this is the error that occurred.")
                .eventValue(Number.class, "1.8", "In 'on close', this is the code for the closing.")
                .eventValue(Boolean.class, "1.8", "In 'on close', this is whether the closing was initiated remotely (true) or locally (false).");
        Registration.registerEvent("WebSocket Server", ScopeWebSocketServer.class, WebSocketEvent.class, "websocket server %string%")
                .document("WebSocket Server Template", "1.8", "Not an actual event, but rather a template for a websocket server, with the specified ID. "
                        + "Under the main \"event\" line you can have four different sub-scopes that handle websocket events:"
                        + "\non start: This is called when the websocket server is started."
                        + "\non stop: This is called when the websocket server is stopped."
                        + "\non open: This is called when a client opens a websocket connection with this websocket server."
                        + "\non message: This is called when the other end of a websocket connection sends a message."
                        + "\non error: This is called when an error occurs related to a websocket connection."
                        + "\non close: This is called when a websocket connection is closed.")
                .eventValue(WebSocket.class, "1.8", "The websocket object associated with this particular connection, in 'on open', 'on message', 'on error', and 'on close'.")
                .eventValue(String.class, "1.8", "In 'on message', this is the received message. In 'on close', this is the reason for closing.")
                .eventValue(ThrowableMundo.class, "1.8", "In 'on error', this is the error that occurred.")
                .eventValue(Number.class, "1.8", "In 'on close', this is the code for the closing.")
                .eventValue(Boolean.class, "1.8", "In 'on close', this is whether the closing was initiated remotely (true) or locally (false).");

        Registration.registerEventValue(WebSocketEvent.class, WebSocket.class, event -> event.webSocket);
        Registration.registerEventValue(WebSocketMessageEvent.class, String.class, event -> event.message);
        Registration.registerEventValue(WebSocketErrorEvent.class, Throwable.class, event -> event.error);
        Registration.registerEventValue(WebSocketCloseEvent.class, Number.class, event -> event.code);
        Registration.registerEventValue(WebSocketCloseEvent.class, String.class, event -> event.reason);
        Registration.registerEventValue(WebSocketCloseEvent.class, Boolean.class, event -> event.remote);

        Registration.registerExpression(ExprNewWebSocket.class, WebSocket.class, ExpressionType.COMBINED, "[new] websocket %string% connected to uri %string% [with (handshake|http) headers %-handshake%]")
                .document("New WebSocket", "1.8", "Creates a new websocket connection using the websocket client with the specified id, connecting to the specified URI."
                        /*+ "Optionally, you can specify additional HTTP headers, which you can use to add additional information in the initial connection (ex. a password). "
                        + "A header is a mapping from one string (referred to as the name in MundoSK syntax) to another (each header has a unique name). "
                        + "You can specify headers using a jsonobject or using a list variable."*/);
        Registration.registerExpression(ExprWebSocketServerPort.class, Number.class, ExpressionType.SIMPLE, "websocket [server] port")
                .document("WebSocket Server Port", "1.8", "For use under 'websocket server %string%': An expression for the port on which this websocket server is open.");
        Registration.registerExpression(ExprAllWebSockets.class, WebSocket.class, ExpressionType.PROPERTY, "all websockets [of server at port %-number%]")
                .document("All WebSockets of Server", "1.8", "An expression for all of the websocket connections of the websocket server at the specified port. "
                        + "When used under 'websocket server %string%', the port is optional, in which case it will return the websockets for the websocket server being controlled.");
        Registration.registerExpression(ExprWebSocketServerID.class, String.class, ExpressionType.PROPERTY, "id of websocket server at port %number%")
                .document("ID of WebSocket Server", "1.8", "An expression for the ID of the websocket server template controlling the websocket server at the specified port.");
        Registration.registerPropertyExpression(ExprWebSocketID.class, String.class, "websocket", "websocket id")
                .document("ID of WebSocket Client", "1.8", "An expression for the ID of the websocket client template controlling the specified websocket. "
                        + "This will not be set if the specified websocket belongs to a websocket server (meaning the connection was initiated externally).");
        Registration.registerPropertyExpression(ExprWebSocketHost.class, String.class, "websocket", "local host", "remote host", "external host")
                .document("Host of WebSocket", "1.8", "An expression for the host, local or external, of the specified websocket.");
        Registration.registerPropertyExpression(ExprWebSocketPort.class, Number.class, "websocket", "local port", "remote port", "external port")
                .document("Port of WebSocket", "1.8", "An expression for the port, local or external, of the specified websocket.");
        Registration.registerPropertyExpression(ExprWebSocketState.class, WebSocket.READYSTATE.class, "websocket", "websocket state")
                .document("Connection State of WebSocket", "1.8", "An expression for the connection state of the specified websocket.");

        loadHandshake();
    }

    private static void loadHandshake() {
        Registration.registerExpression(ExprHandshake.class, Handshakedata.class, ExpressionType.SIMPLE, "[websocket] [handshake] request", "[websocket] [handshake] response", "new [websocket] handshake");
        Registration.registerExpression(ExprRequestIsAllowed.class, Boolean.class, ExpressionType.SIMPLE, "[websocket] [handshake] request is (0¦allowed|1¦refused)");
        Registration.registerExpression(ExprHeader.class, String.class, ExpressionType.COMBINED, "[handshake] [http] header %string% of %handshake%");
        Registration.registerExpression(ExprHeaderNames.class, String.class, ExpressionType.PROPERTY, "[all] [handshake] [http] header names of %handshake%");
        Registration.registerExpression(ExprContent.class, Number.class, ExpressionType.PROPERTY, "handshake content of %handshake%");
        Registration.registerPropertyExpression(ExprHTTPStatus.class, Number.class, "handshake", "http status", "handshake http status");
        Registration.registerPropertyExpression(ExprHTTPStatusMessage.class, String.class, "handshake", "http status message", "handshake http status message");
        Registration.registerPropertyExpression(ExprResourceDescriptor.class, String.class, "handshake", "resource descriptor", "handshake resource descriptor");
    }

    public static WebSocketClientFunctionality getClientFunctionality(String id) {
        return clientFunctionalities.computeIfAbsent(id, __ -> new WebSocketClientFunctionality(id));
    }

    public static void clearClientFunctionalities() {
        clientFunctionalities.values().forEach(WebSocketClientFunctionality::unload);
    }

    public static WebSocketServerFunctionality getServerFunctionality(String id) {
        return serverFunctionalities.computeIfAbsent(id, __ -> new WebSocketServerFunctionality(id));
    }

    public static void clearServerFunctionalities() {
        serverFunctionalities.values().forEach(WebSocketServerFunctionality::unload);
    }

    public static SkriptWebSocketServer getServer(int port) {
        return servers.get(port);
    }

    public static SkriptWebSocketServer startServer(int port, String id) {
        SkriptWebSocketServer server = new SkriptWebSocketServer(getServerFunctionality(id), port);
        if (servers.computeIfAbsent(port, __ -> server) != server) {
            throw new IllegalArgumentException("There is already a WebSocketServer put at the port " + port);
        }
        server.start();
        return server;
    }

    public static void stopServer(int port, int timeout) {
        SkriptWebSocketServer server = servers.get(port);
        if (server != null) {
            try {
                server.stop(timeout);
                servers.remove(port);
            } catch (InterruptedException e) {
                Logging.reportException(WebSocketManager.class, e);
            }
        }
    }

    public static void stopAllServers(int timeout) {
        servers.forEach((__, server) -> {
            try {
                server.stop(timeout);
            } catch (InterruptedException e) {
                Logging.reportException(WebSocketManager.class, e);
            }
        });
        servers.clear();
    }
}
