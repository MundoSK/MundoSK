package com.pie.tlatoani.Socket;

import javax.annotation.Nullable;

import ch.njol.skript.util.Timespan;
import com.pie.tlatoani.Mundo;
import org.bukkit.event.Event;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

import java.io.IOException;
import java.net.InetSocketAddress;

public class ExprMotdOfModernServer extends SimpleExpression<String>{
    private Expression<String> host;
    private Expression<Number> port;
    private Expression<Timespan> timeout;

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean arg2, ParseResult arg3) {
        host = (Expression<String>) exprs[0];
        port = (Expression<Number>) exprs[1];
        timeout = (Expression<Timespan>) exprs[2];
        return true;
    }

    @Override
    public String toString(@Nullable Event arg0, boolean arg1) {
        return "motd of modern server";
    }

    @Override
    @Nullable
    protected String[] get(Event arg0) {
        UtilPingServer ping = new UtilPingServer();
        ping.setAddress(new InetSocketAddress(host.getSingle(arg0), (port != null ? port.getSingle(arg0) : 25565).intValue()));
        if (timeout != null) {
            ping.setTimeout((new Long(timeout.getSingle(arg0).getMilliSeconds())).intValue());
        }
        String result = null;
        try {
            result = ping.fetchData();
        } catch (IOException e) {
            Mundo.debug(this, e);
        }
        Mundo.debug(this, result);
        return new String[]{result};
    }

}