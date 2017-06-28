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

import java.util.Optional;

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
        SectionNode topNode = (SectionNode) SkriptLogger.getNode();
        try {
            if (!clientFunctionality.isEmpty()) {
                Skript.error("You cannot have two 'websocket client' instances with the same id!");
                return false;
            }
            for (Node node : topNode) {
                SkriptLogger.setNode(node);
                Mundo.debug(this, "Current node: " + node.getKey());
                if (!(node instanceof SectionNode)) {
                    Skript.error("'websocket client' should only have sections directly under it!");
                    clientFunctionality.clear();
                    return false;
                }
                SectionNode subNode = (SectionNode) node;
                if (subNode.getKey().equals("on open")) {
                    if (clientFunctionality.onOpen.isPresent()) {
                        Skript.error("You cannot have two 'on open' sections here!");
                        clientFunctionality.clear();
                        return false;
                    }
                    ScriptLoader.setCurrentEvent("WebSocketClientOpen", WebSocketOpenEvent.class);
                    clientFunctionality.onOpen = Optional.of(UtilScope.loadSectionNodeOrDummy(subNode, null));
                } else if (subNode.getKey().equals("on close")) {
                    if (clientFunctionality.onClose.isPresent()) {
                        Skript.error("You cannot have two 'on close' sections here!");
                        clientFunctionality.clear();
                        return false;
                    }
                    ScriptLoader.setCurrentEvent("WebSocketClientClose", WebSocketCloseEvent.class);
                    clientFunctionality.onClose = Optional.of(UtilScope.loadSectionNodeOrDummy(subNode, null));
                } else if (subNode.getKey().equals("on message")) {
                    if (clientFunctionality.onMessage.isPresent()) {
                        Skript.error("You cannot have two 'on message' sections here!");
                        clientFunctionality.clear();
                        return false;
                    }
                    ScriptLoader.setCurrentEvent("WebSocketClientMessage", WebSocketMessageEvent.class);
                    clientFunctionality.onMessage = Optional.of(UtilScope.loadSectionNodeOrDummy(subNode, null));
                } else if (subNode.getKey().equals("on error")) {
                    if (clientFunctionality.onError.isPresent()) {
                        Skript.error("You cannot have two 'on error' sections here!");
                        clientFunctionality.clear();
                        return false;
                    }
                    ScriptLoader.setCurrentEvent("WebSocketClientError", WebSocketErrorEvent.class);
                    clientFunctionality.onError = Optional.of(UtilScope.loadSectionNodeOrDummy(subNode, null));
                } else {
                    Skript.error("The only sections allowed under 'websocket client' are 'on open', 'on close', 'on message', and 'on error'!");
                    clientFunctionality.clear();
                    return false;
                }
            }
            clientFunctionality.debugTriggerItems();
            return true;
        } finally {
            UtilScope.removeSubNodes(topNode);
        }

    }

    @Override
    public String toString(Event event, boolean b) {
        return "websocket client \"" + clientFunctionality.id + "\"";
    }
}
