package com.pie.tlatoani.WebSocket;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.iterator.EmptyIterator;
import com.pie.tlatoani.Util.MundoUtil;
import com.pie.tlatoani.WebSocket.Events.WebSocketOpenEvent;
import org.bukkit.event.Event;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Tlatoani on 12/25/17.
 */
public class ExprHandshakeHeaderNames extends SimpleExpression<String> {

    @Override
    protected String[] get(Event event) {
        Iterator<String> iterator = iterator(event);
        List<String> list = new LinkedList<>();
        iterator.forEachRemaining(list::add);
        return list.toArray(new String[0]);
    }

    @Override
    public Iterator<String> iterator(Event event) {
        //return MundoUtil.cast(event, WebSocketOpenEvent.class).map(wsOpenEvent -> wsOpenEvent.handshake.iterateHttpFields()).orElse(new EmptyIterator<>());
        return null;
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "all request header names";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        if (!MundoUtil.isAssignableFromCurrentEvent(WebSocketOpenEvent.class)) {
            Skript.error("The 'all request headers' expression can only be used in the 'on open' section of a websocket client or server template!");
            return false;
        }
        return true;
    }
}
