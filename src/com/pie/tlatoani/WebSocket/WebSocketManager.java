package com.pie.tlatoani.WebSocket;

import ch.njol.skript.classes.Comparator;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.ExpressionType;
import com.pie.tlatoani.Core.Static.Logging;
import com.pie.tlatoani.Core.Registration.Registration;
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
        Registration.registerType(Handshakedata.class, "handshake")
                .document("Handshake", "1.8", "A handshake, sent by two websockets to each other when they are initially connection. "
                        + "Handshakes contain information necessary to initiate a websocket connection and you can add more information "
                        + "(ex. a password) that you want to be sent on the initial connection.");
        Registration.registerEnum(WebSocket.READYSTATE.class, "websocketstate", WebSocket.READYSTATE.values())
                .pair("NOT YET CONNECTED", WebSocket.READYSTATE.NOT_YET_CONNECTED)
                .document("WebSocket State", "1.8", "A state that a websocket connection can be in.");

        Registration.registerComparator(WebSocket.class, WebSocket.READYSTATE.class, false,
                ((webSocket, readystate) -> Comparator.Relation.get(webSocket.getReadyState() == readystate)));

        Registration.registerEffect(EffCloseWebSocket.class, "close websocket %websocket% [with message %-string%]")
                .document("Close WebSocket", "1.8", "Closes the specified websocket connection, optionally specifying a closing message to send.");
        Registration.registerEffect(EffWebSocketSendMessage.class, "websocket send %strings% [through %websockets%]")
                .document("WebSocket Send", "1.8", "Sends the specified messages through the specified websockets. "
                        + "If no websockets are specified, the messages are sent through the event-websocket.");
        Registration.registerEffect(EffStartWebSocketServer.class, "start [a] websocket server %string% at port %number%")
                .document("Start WebSocket Server", "1.8", "Starts a WebSocket server using the specified server template at the specified port. "
                        + "A WebSocket server allows other servers/online services to initiate websocket connections with the server.");
        Registration.registerEffect(EffStopWebSocketServer.class, "stop [the] websocket server at port %number% [with timeout %-number%]")
                .document("Stop WebSocket Server", "1.8", "Stops the WebSocket server at the specified port, optionally specifying a timeout in milliseconds.");

        Registration.registerEvent("WebSocket Client", ScopeWebSocketClient.class, WebSocketEvent.class, "websocket client %string%")
                .document("WebSocket Client Template", "1.8", "Not an actual event, but rather a template for a websocket client, with the specified ID. "
                        + "Under the main \"event\" line you can have five different sub-scopes that handle websocket events:"
                        , "on open: This is called when the websocket connection initially opens."
                        , "on handshake: This is called before 'on open', when the response handshake has been received from the server, "
                        + "but before the websocket is technically open, meaning you can't yet send messages to the server."
                        , "on message: This is called when the other end of the websocket connection sends a message."
                        , "on error: This is called when an error occurs related to the websocket connection."
                        , "on close: This is called when the websocket connection is closed.")
                .eventValue(WebSocket.class, "1.8", "The websocket object being controlled by this template.")
                .eventValue(String.class, "1.8", "In 'on message', this is the received message. In 'on close', this is the reason for closing.")
                .eventValue(Throwable.class, "1.8", "In 'on error', this is the error that occurred.")
                .eventValue(Number.class, "1.8", "In 'on close', this is the code for the closing.")
                .eventValue(Boolean.class, "1.8", "In 'on close', this is whether the closing was initiated remotely (true) or locally (false).");
        Registration.registerEvent("WebSocket Server", ScopeWebSocketServer.class, WebSocketEvent.class, "websocket server %string%")
                .document("WebSocket Server Template", "1.8", "Not an actual event, but rather a template for a websocket server, with the specified ID. "
                        + "Under the main \"event\" line you can have seven different sub-scopes that handle websocket events:"
                        , "on start: This is called when the websocket server is started."
                        , "on stop: This is called when the websocket server is stopped."
                        , "on handshake: This is called before 'on open', when a client has sent a request handshake, "
                        + "allowing you to modify the response handshake to be sent as well as verify that the client's request is valid, "
                        + "and refuse the request if you deem it to be invalid. Note that you can't send messages to the client at this point."
                        , "on open: This is called when a client opens a websocket connection with this websocket server."
                        , "on message: This is called when the other end of a websocket connection sends a message."
                        , "on error: This is called when an error occurs related to a websocket connection."
                        , "on close: This is called when a websocket connection is closed.")
                .eventValue(WebSocket.class, "1.8", "The websocket object associated with this particular connection, in 'on open', 'on message', 'on error', and 'on close'.")
                .eventValue(String.class, "1.8", "In 'on message', this is the received message. In 'on close', this is the reason for closing.")
                .eventValue(Throwable.class, "1.8", "In 'on error', this is the error that occurred.")
                .eventValue(Number.class, "1.8", "In 'on close', this is the code for the closing.")
                .eventValue(Boolean.class, "1.8", "In 'on close', this is whether the closing was initiated remotely (true) or locally (false).");

        Registration.registerEventValue(WebSocketEvent.class, WebSocket.class, event -> event.webSocket);
        Registration.registerEventValue(WebSocketMessageEvent.class, String.class, event -> event.message);
        Registration.registerEventValue(WebSocketErrorEvent.class, Throwable.class, event -> event.error);
        Registration.registerEventValue(WebSocketCloseEvent.class, Number.class, event -> event.code);
        Registration.registerEventValue(WebSocketCloseEvent.class, String.class, event -> event.reason);
        Registration.registerEventValue(WebSocketCloseEvent.class, Boolean.class, event -> event.remote);

        Registration.registerExpression(ExprNewWebSocket.class, WebSocket.class, ExpressionType.COMBINED, "[[a] new] websocket %string% connected to uri %string% [with (handshake|http) headers %-handshake%]")
                .document("New WebSocket", "1.8", "Creates a new websocket connection using the websocket client with the specified id, connecting to the specified URI."
                        + "Optionally, you can specify additional HTTP headers, which you can use to add additional information in the initial connection (ex. a password). "
                        + "A header is a mapping from one string (the name) to another (the value). Each header has a unique name."
                        + "You can specify headers by creating a new handshake, setting its headers, and then specifying it in the syntax here. "
                        + "Keep in mind that any other information stored in the handshake will be ignored by this expression.");
        Registration.registerExpression(ExprWebSocketServerPort.class, Number.class, ExpressionType.SIMPLE, "[the] websocket [server] port")
                .document("WebSocket Server Port", "1.8", "For use under 'websocket server %string%': An expression for the port on which this websocket server is open.");
        Registration.registerExpression(ExprAllWebSockets.class, WebSocket.class, ExpressionType.PROPERTY, "all [[of] the] websockets [of [the] server at port %-number%]")
                .document("All WebSockets of Server", "1.8", "An expression for all of the websocket connections of the websocket server at the specified port. "
                        + "When used under 'websocket server %string%', the port is optional, in which case it will return the websockets for the websocket server being controlled.");
        Registration.registerExpression(ExprWebSocketServerID.class, String.class, ExpressionType.PROPERTY, "[the] id of [the] websocket server at port %number%")
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
        Registration.registerExpression(ExprHandshake.class, Handshakedata.class, ExpressionType.SIMPLE, "[the] [websocket] request [handshake]", "[the] [websocket] response [handshake]", "[a] new [websocket] handshake")
                .document("Handshake Request/Response/New", "1.8", "An expression for some handshake:"
                        , "request: The handshake sent by a websocket client to a websocket server, "
                        + "used in the 'on open' section of a websocket server template or the 'on handshake' section of a websocket client or server template"
                        , "response: The handshake sent by a websocket server responding to a request by a websocket client, "
                        + "used in the 'on open' section of a websocket client template or the 'on handshake' section of a websocket client or server template"
                        , "new: A new handshake object, currently only useful for specifying additional http headers in the New Websocket expression");
        Registration.registerExpression(ExprHeader.class, String.class, ExpressionType.COMBINED, "[handshake] [http] header %string% of %handshake%")
                .document("HTTP Header of Handshake", "1.8", "An expression for the value of the HTTP header with the specified name of the specified handshake.");
        Registration.registerExpression(ExprHeaderNames.class, String.class, ExpressionType.PROPERTY, "[all [[of] the]] [handshake] [http] header names of %handshake%")
                .document("HTTP Header Names of Handshake", "1.8", "An expression for a list of the names of the HTTP headers of the specified handshake.");
        Registration.registerExpression(ExprContent.class, Number.class, ExpressionType.PROPERTY, "[the] handshake content of %handshake%")
                .document("Content of Handshake", "1.8", "An expression for the content (a byte array) stored in the specified handshake.");
        Registration.registerPropertyExpression(ExprHTTPStatus.class, Number.class, "handshake", "http status", "handshake http status")
                .document("HTTP Status of Handshake", "1.8", "An expression for the HTTP status of the specified handshake. "
                        + "This can only exist in handshakes sent by the server (ex. 'response' - see the Handshake Request/Response/New expression).");
        Registration.registerPropertyExpression(ExprHTTPStatusMessage.class, String.class, "handshake", "http status message", "handshake http status message")
                .document("HTTP Status Message of Handshake", "1.8", "An expression for the HTTP status message of the specified handshake. "
                        + "This can only exist in handshakes sent by the server (ex. 'response' - see the Handshake Request/Response/New expression).");
        Registration.registerPropertyExpression(ExprResourceDescriptor.class, String.class, "handshake", "resource descriptor", "handshake resource descriptor")
                .document("Resource Descriptor of Handshake", "1.8", "An expression for the resource descriptor of the specified handshake. "
                        + "This can only exist in handshakes sent by the client (ex. 'request' - see the Handshake Request/Response/New expression).");
        Registration.registerExpressionCondition(CondRequestIsAccepted.class, ExpressionType.SIMPLE, "[the] [websocket] request [handshake] is (0¦accepted|1¦refused)")
                .document("Request is Accepted", "1.8", "Used in the 'on handshake' section of a websocket server template. "
                        + "Checks whether the client's request was accepted or refused. Can be set.");
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
