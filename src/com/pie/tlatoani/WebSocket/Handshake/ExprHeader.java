package com.pie.tlatoani.WebSocket.Handshake;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import mundosk_libraries.java_websocket.handshake.HandshakeBuilder;
import mundosk_libraries.java_websocket.handshake.Handshakedata;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 12/29/17.
 */
public class ExprHeader extends SimpleExpression<String> {
    private Expression<Handshakedata> handshakeExpr;
    private Expression<String> nameExpr;

    @Override
    protected String[] get(Event event) {
        Handshakedata handshake = handshakeExpr.getSingle(event);
        String name = nameExpr.getSingle(event);
        if (handshake != null && name != null) {
            return new String[]{handshake.getFieldValue(name)};
        } else {
            return new String[0];
        }
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "handshake header " + nameExpr + " of " + handshakeExpr;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        nameExpr = (Expression<String>) expressions[0];
        handshakeExpr = (Expression<Handshakedata>) expressions[1];
        return true;
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        Handshakedata handshake = handshakeExpr.getSingle(event);
        String name = nameExpr.getSingle(event);
        String value = (String) delta[0];
        if (handshake instanceof HandshakeBuilder) {
            ((HandshakeBuilder) handshake).put(name, value);
        }
    }

    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) return CollectionUtils.array(String.class);
        return null;
    }
}
