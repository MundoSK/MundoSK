package com.pie.tlatoani.ProtocolLib;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import ch.njol.util.Kleenean;
import com.comphenix.protocol.events.PacketContainer;
import com.pie.tlatoani.Registration.ModifiableSyntaxElementInfo;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by Tlatoani on 10/7/17.
 */
public class ExprPacketInfoAlias extends SimpleExpression<Object> {
    private static final ModifiableSyntaxElementInfo.Expression<ExprPacketInfoAlias, Object> syntaxElementInfo =
            new ModifiableSyntaxElementInfo.Expression<ExprPacketInfoAlias, Object>(ExprPacketInfoAlias.class, Object.class, ExpressionType.PROPERTY);
    private static final List<PacketInfoAlias> aliasList = new ArrayList<>();
    private static boolean registered = false;

    private PacketInfoAlias alias;
    private Expression<PacketContainer> packetExpression;

    public static void registerAliases(PacketInfoAlias... aliases) {
        if (!registered) {
            syntaxElementInfo.register();
            EventValues.registerEventValue(PacketInfoAlias.ContainerEvent.class, PacketContainer.class, new Getter<PacketContainer, PacketInfoAlias.ContainerEvent>() {
                @Override
                public PacketContainer get(PacketInfoAlias.ContainerEvent containerEvent) {
                    return containerEvent.packet;
                }
            }, 0);
        }
        int result = aliasList.size();
        aliasList.addAll(Arrays.asList(aliases));
        syntaxElementInfo.addPatterns(Stream.of(aliases).map(alias -> alias.alias).toArray(String[]::new));
        //return result;
    }

    public static void unregisterAliases(int index, int amount) {
        for (int i = 0; i < amount; i++) {
            aliasList.remove(index);
        }
        //aliasList.
        String[] newPatterns = new String[syntaxElementInfo.getPatterns().length - amount];
        System.arraycopy(syntaxElementInfo.getPatterns(), 0, newPatterns, 0, index);
        System.arraycopy(syntaxElementInfo.getPatterns(), index + amount, newPatterns, index, syntaxElementInfo.getPatterns().length - amount - index);
        syntaxElementInfo.setPatterns(newPatterns);
    }

    public static void unregisterAllAliases() {
        aliasList.clear();
        syntaxElementInfo.setPatterns();
    }

    @Override
    protected Object[] get(Event event) {
        return alias.evaluate(packetExpression.getSingle(event));
    }

    @Override
    public boolean isSingle() {
        return alias.expression.isSingle();
    }

    @Override
    public Class<?> getReturnType() {
        return alias.expression.getReturnType();
    }

    @Override
    public String toString(Event event, boolean b) {
        return alias.toString(packetExpression, event, b);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        for (Expression<?> expression : expressions) {
            if (expression != null) {
                packetExpression = (Expression<PacketContainer>) expression;
                break;
            }
        }
        alias = aliasList.get(i);
        return true;
    }
}
