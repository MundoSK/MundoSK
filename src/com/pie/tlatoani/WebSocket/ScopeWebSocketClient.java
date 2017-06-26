package com.pie.tlatoani.WebSocket;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.*;
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
        for (Node node : UtilScope.getSection()) {
            if (!(node instanceof SectionNode)) {
                Skript.error("'client websocket' should only have sections directly under it!");
                return false;
            }
            SectionNode subNode = (SectionNode) node;
            if (subNode.isEmpty()) {
                Skript.error("Empty section!");
                return false;
            }
            if (subNode.getKey().equals("on open")) {
                if (clientFunctionality.onOpen != null) {
                    Skript.error("You cannot have two 'on open' sections here!");
                    return false;
                }
                ScriptLoader.setCurrentEvent("WebSocketClientOpen", WebSocketOpenEvent.class);
                clientFunctionality.onOpen = UtilScope.loadSectionNode(subNode, null);
            } else if (subNode.getKey().equals("on close")) {
                if (clientFunctionality.onClose != null) {
                    Skript.error("You cannot have two 'on close' sections here!");
                    return false;
                }
                ScriptLoader.setCurrentEvent("WebSocketClientClose", WebSocketCloseEvent.class);
                clientFunctionality.onClose = UtilScope.loadSectionNode(subNode, null);
            } else if (subNode.getKey().equals("on message")) {
                if (clientFunctionality.onMessage != null) {
                    Skript.error("You cannot have two 'on message' sections here!");
                    return false;
                }
                ScriptLoader.setCurrentEvent("WebSocketClientMessage", WebSocketMessageEvent.class);
                clientFunctionality.onMessage = UtilScope.loadSectionNode(subNode, null);
            } else if (subNode.getKey().equals("on error")) {
                if (clientFunctionality.onError != null) {
                    Skript.error("You cannot have two 'on error' sections here!");
                    return false;
                }
                ScriptLoader.setCurrentEvent("WebSocketClientError", WebSocketErrorEvent.class);
                clientFunctionality.onError = UtilScope.loadSectionNode(subNode, null);
            } else {
                Skript.error("The only sections allowed under 'websocket client' are 'on open', 'on close', 'on message', and 'on error'!");
                return false;
            }
        }
        clientFunctionality.debugTriggerItems();
        return true;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "websocket client \"" + clientFunctionality.id + "\"";
    }
}
