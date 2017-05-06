package com.pie.tlatoani.WebSocket;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.*;
import ch.njol.skript.log.SkriptLogger;
import com.pie.tlatoani.Util.UtilScope;
import com.pie.tlatoani.WebSocket.Events.*;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 5/4/17.
 */
public class ScopeWebSocketServer extends SelfRegisteringSkriptEvent {
    private WebSocketServerFunctionality serverFunctionality;

    @Override
    public void register(Trigger trigger) {}

    @Override
    public void unregister(Trigger trigger) {
        serverFunctionality.clear();
    }

    @Override
    public void unregisterAll() {
        WebSocketManager.clearServerFunctionalities();
    }

    @Override
    public boolean init(Literal<?>[] literals, int i, SkriptParser.ParseResult parseResult) {
        serverFunctionality = new WebSocketServerFunctionality(((Literal<String>) literals[0]).getSingle());
        SectionNode sectionNode = (SectionNode) SkriptLogger.getNode();
        for (Node node : sectionNode) {
            sectionNode.remove(node);
            if (!(node instanceof SectionNode)) {
                Skript.error("'server websocket' should only have sections directly under it!");
                return false;
            }
            SectionNode subNode = (SectionNode) node;
            if (subNode.isEmpty()) {
                Skript.error("Empty section!");
                return false;
            }
            if (subNode.getKey().equals("on start")) {
                if (serverFunctionality.onStart != null) {
                    Skript.error("You cannot have two 'on start' sections here!");
                    return false;
                }
                ScriptLoader.setCurrentEvent("WebSocketServerStart", WebSocketServerStartEvent.class);
                serverFunctionality.onStart = UtilScope.loadSectionNode(subNode, null);
            } else if (subNode.getKey().equals("on stop")) {
                if (serverFunctionality.onStop != null) {
                    Skript.error("You cannot have two 'on stop' sections here!");
                    return false;
                }
                ScriptLoader.setCurrentEvent("WebSocketServerStop", WebSocketServerStopEvent.class);
                serverFunctionality.onStop = UtilScope.loadSectionNode(subNode, null);
            } else if (subNode.getKey().equals("on open")) {
                if (serverFunctionality.onOpen != null) {
                    Skript.error("You cannot have two 'on open' sections here!");
                    return false;
                }
                ScriptLoader.setCurrentEvent("WebSocketServerOpen", WebSocketOpenEvent.Server.class);
                serverFunctionality.onOpen = UtilScope.loadSectionNode(subNode, null);
            } else if (subNode.getKey().equals("on close")) {
                if (serverFunctionality.onClose != null) {
                    Skript.error("You cannot have two 'on close' sections here!");
                    return false;
                }
                ScriptLoader.setCurrentEvent("WebSocketServerClose", WebSocketCloseEvent.Server.class);
                serverFunctionality.onClose = UtilScope.loadSectionNode(subNode, null);
            } else if (subNode.getKey().equals("on message")) {
                if (serverFunctionality.onMessage != null) {
                    Skript.error("You cannot have two 'on message' sections here!");
                    return false;
                }
                ScriptLoader.setCurrentEvent("WebSocketServerMessage", WebSocketMessageEvent.Server.class);
                serverFunctionality.onMessage = UtilScope.loadSectionNode(subNode, null);
            } else if (subNode.getKey().equals("on error")) {
                if (serverFunctionality.onError != null) {
                    Skript.error("You cannot have two 'on error' sections here!");
                    return false;
                }
                ScriptLoader.setCurrentEvent("WebSocketServerError", WebSocketErrorEvent.Server.class);
                serverFunctionality.onError = UtilScope.loadSectionNode(subNode, null);
            } else {
                Skript.error("The only sections allowed under 'websocket server' are 'on start', 'on stop', 'on open', 'on close', 'on message', and 'on error'!");
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "websocket server \"" + serverFunctionality.id + "\"";
    }
}
