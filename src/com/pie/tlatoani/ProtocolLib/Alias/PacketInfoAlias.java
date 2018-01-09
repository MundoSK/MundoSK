package com.pie.tlatoani.ProtocolLib.Alias;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.log.ParseLogHandler;
import ch.njol.skript.log.SkriptLogger;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.pie.tlatoani.ProtocolLib.PacketManager;
import com.pie.tlatoani.Util.BaseEvent;
import com.pie.tlatoani.Util.Logging;
import com.pie.tlatoani.Util.Reflection;
import org.bukkit.event.Event;

import java.util.Iterator;
import java.util.Optional;

/**
 * Created by Tlatoani on 10/15/17.
 */
public class PacketInfoAlias {
    public final PacketType packetType;
    public final String alias;
    public final String original;
    public final Expression<?> expression;

    public static final Reflection.MethodInvoker SKRIPT_PARSER_PARSE = Reflection.getMethod(SkriptParser.class, "parse", Iterator.class);

    public static class ContainerEvent extends BaseEvent {
        public final PacketContainer packet;

        public ContainerEvent(PacketContainer packet) {
            this.packet = packet;
        }
    }

    private PacketInfoAlias(PacketType packetType, String alias, String original, Expression<?> expression) {
        this.packetType = packetType;
        this.alias = alias;
        this.expression = expression;
        this.original = original;
    }

    public static Optional<PacketInfoAlias> create(PacketType packetType, String syntax, String original) {
        String resultSyntax = original.replace("%packet%", "event-packet");
        String currentEventName = ScriptLoader.getCurrentEventName();
        Class<? extends Event>[] currentEvents = ScriptLoader.getCurrentEvents();
        ScriptLoader.setCurrentEvent("ExprPacketInfoAliasPacketEvent", ContainerEvent.class);
        Logging.debug(PacketInfoAlias.class, "packetType = " + packetType + ", syntax = " + syntax + ", original = " + original + ", resultSyntax = " + resultSyntax);
        //Expression<?> expression = SkriptParser.parseStatic(resultSyntax, PacketManager.packetInfoExpressionInfoIterator(), "'" + original + "' is not a valid packet info expression");
        Expression<?> expression = parsePacketInfoExpression(resultSyntax, "'" + original + "' is not a valid packet info expression");
        Logging.debug(PacketInfoAlias.class, "expr = " + expression);
        ScriptLoader.setCurrentEvent(currentEventName, currentEvents);
        if (expression == null) {
            return Optional.empty();
        }
        return Optional.of(new PacketInfoAlias(packetType, syntax, original, expression));
    }

    //Method adapted from the parseStatic() method of SkriptParser in Skript
    private static Expression<?> parsePacketInfoExpression(String syntax, String defaultError) {
        final ParseLogHandler log = SkriptLogger.startParseLogHandler();
        final Expression<?> result;
        try {
            SkriptParser parser = new SkriptParser(syntax, SkriptParser.ALL_FLAGS);
            result = (Expression<?>) SKRIPT_PARSER_PARSE.invoke(parser, PacketManager.packetInfoExpressionInfoIterator());
            if (result != null) {
                log.printLog();
                return result;
            }
            log.printError(defaultError);
            return null;
        } catch (Exception e) {
            Logging.reportException(PacketInfoAlias.class, e);
        } finally {
            log.stop();
        }
        return null;
    }

    public String toString(Expression<PacketContainer> packetExpression, Event event, boolean b) {
        return alias.replace("%packet%", packetExpression.toString(event, b));
    }

    public Object[] evaluate(PacketContainer packet) {
        if (packet.getType() == packetType) {
            return expression.getArray(new PacketInfoAlias.ContainerEvent(packet));
        } else {
            return new Object[0];
        }
    }
}
