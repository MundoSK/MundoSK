package com.pie.tlatoani.WebSocket;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.*;
import ch.njol.skript.log.SkriptLogger;
import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.Util.UtilScope;
import com.pie.tlatoani.WebSocket.Events.WebSocketCloseEvent;
import com.pie.tlatoani.WebSocket.Events.WebSocketErrorEvent;
import com.pie.tlatoani.WebSocket.Events.WebSocketMessageEvent;
import com.pie.tlatoani.WebSocket.Events.WebSocketOpenEvent;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 5/4/17.
 */
public class ScopeWebSocketClient extends SelfRegisteringSkriptEvent {
    private WebSocketClientFunctionality clientFunctionality;

    @Override
    public void register(Trigger trigger) {}

    @Override
    public void unregister(Trigger trigger) {
        clientFunctionality.clear();
    }

    @Override
    public void unregisterAll() {
        WebSocketManager.clearClientFunctionalities();
    }

    @Override
    public boolean init(Literal<?>[] literals, int i, SkriptParser.ParseResult parseResult) {
        clientFunctionality = WebSocketManager.getClientFunctionality(((Literal<String>) literals[0]).getSingle());
        Node[] nodes = UtilScope.getSection();
        try {
            if (!clientFunctionality.isEmpty()) {
                Skript.error("You cannot have two 'websocket client' instances with the same id!");
                return false;
            }
            if (nodes.length == 0) {
                Skript.error("This 'websocket client' is empty!");
                clientFunctionality.clear();
                return false;
            }
            for (Node node : nodes) {
                SkriptLogger.setNode(node);
                Mundo.debug(this, "Current node: " + node.getKey());
                if (!(node instanceof SectionNode)) {
                    Skript.error("'websocket client' should only have sections directly under it!");
                    clientFunctionality.clear();
                    return false;
                }
                SectionNode subNode = (SectionNode) node;
                if (subNode.isEmpty()) {
                    Skript.error("Empty section!");
                    clientFunctionality.clear();
                    return false;
                }
                if (subNode.getKey().equals("on open")) {
                    if (clientFunctionality.onOpen != null) {
                        Skript.error("You cannot have two 'on open' sections here!");
                        clientFunctionality.clear();
                        return false;
                    }
                    ScriptLoader.setCurrentEvent("WebSocketClientOpen", WebSocketOpenEvent.class);
                    clientFunctionality.onOpen = UtilScope.loadSectionNode(subNode, null);
                } else if (subNode.getKey().equals("on close")) {
                    if (clientFunctionality.onClose != null) {
                        Skript.error("You cannot have two 'on close' sections here!");
                        clientFunctionality.clear();
                        return false;
                    }
                    ScriptLoader.setCurrentEvent("WebSocketClientClose", WebSocketCloseEvent.class);
                    clientFunctionality.onClose = UtilScope.loadSectionNode(subNode, null);
                } else if (subNode.getKey().equals("on message")) {
                    if (clientFunctionality.onMessage != null) {
                        Skript.error("You cannot have two 'on message' sections here!");
                        clientFunctionality.clear();
                        return false;
                    }
                    ScriptLoader.setCurrentEvent("WebSocketClientMessage", WebSocketMessageEvent.class);
                    clientFunctionality.onMessage = UtilScope.loadSectionNode(subNode, null);
                } else if (subNode.getKey().equals("on error")) {
                    if (clientFunctionality.onError != null) {
                        Skript.error("You cannot have two 'on error' sections here!");
                        clientFunctionality.clear();
                        return false;
                    }
                    ScriptLoader.setCurrentEvent("WebSocketClientError", WebSocketErrorEvent.class);
                    clientFunctionality.onError = UtilScope.loadSectionNode(subNode, null);
                } else {
                    Skript.error("The only sections allowed under 'websocket client' are 'on open', 'on close', 'on message', and 'on error'!");
                    clientFunctionality.clear();
                    return false;
                }
            }
            clientFunctionality.debugTriggerItems();
            return true;
        } finally {
            UtilScope.removeNodes(nodes);
        }

    }

    @Override
    public String toString(Event event, boolean b) {
        return "websocket client \"" + clientFunctionality.id + "\"";
    }
}
