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
    private WebSocketClientFunctionality.Nebula nebula;

    @Override
    public void register(Trigger trigger) {
        clientFunctionality.load(nebula);
        clientFunctionality.debugTriggerItems();
    }

    @Override
    public void unregister(Trigger trigger) {
        clientFunctionality.unload();
    }

    @Override
    public void unregisterAll() {
        WebSocketManager.clearClientFunctionalities();
    }

    @Override
    public boolean init(Literal<?>[] literals, int i, SkriptParser.ParseResult parseResult) {
        clientFunctionality = WebSocketManager.getClientFunctionality(((Literal<String>) literals[0]).getSingle());
        nebula = new WebSocketClientFunctionality.Nebula();
        SectionNode topNode = (SectionNode) SkriptLogger.getNode();
        try {
            if (clientFunctionality.isLoaded()) {
                Skript.error("You cannot have two 'websocket client' instances with the same id!");
                return false;
            }
            for (Node node : topNode) {
                SkriptLogger.setNode(node);
                Mundo.debug(this, "Current node: " + node.getKey());
                if (!(node instanceof SectionNode)) {
                    Skript.error("'websocket client' should only have sections directly under it!");
                    return false;
                }
                SectionNode subNode = (SectionNode) node;
                if (subNode.getKey().equals("on open")) {
                    if (nebula.onOpen.isPresent()) {
                        Skript.error("You cannot have two 'on open' sections here!");
                        return false;
                    }
                    nebula.onOpen = Optional.of(subNode);
                } else if (subNode.getKey().equals("on close")) {
                    if (nebula.onClose.isPresent()) {
                        Skript.error("You cannot have two 'on close' sections here!");
                        return false;
                    }
                    nebula.onClose = Optional.of(subNode);
                } else if (subNode.getKey().equals("on message")) {
                    if (nebula.onMessage.isPresent()) {
                        Skript.error("You cannot have two 'on message' sections here!");
                        return false;
                    }
                    nebula.onMessage = Optional.of(subNode);
                } else if (subNode.getKey().equals("on error")) {
                    if (nebula.onError.isPresent()) {
                        Skript.error("You cannot have two 'on error' sections here!");
                        return false;
                    }
                    nebula.onError = Optional.of(subNode);
                } else {
                    Skript.error("The only sections allowed under 'websocket client' are 'on open', 'on close', 'on message', and 'on error'!");
                    return false;
                }
            }
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
