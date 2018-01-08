package com.pie.tlatoani.ProtocolLib.Alias;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import ch.njol.util.Kleenean;
import com.comphenix.protocol.events.PacketContainer;
import com.pie.tlatoani.Registration.ModifiableSyntaxElementInfo;
import com.pie.tlatoani.Registration.Registration;
import com.pie.tlatoani.Util.GroupedList;
import org.bukkit.event.Event;

import java.util.Collection;

/**
 * Created by Tlatoani on 10/7/17.
 */
public class ExprPacketInfoAlias extends SimpleExpression<Object> {
    private static final ModifiableSyntaxElementInfo.Expression<ExprPacketInfoAlias, Object> syntaxElementInfo =
            new ModifiableSyntaxElementInfo.Expression<ExprPacketInfoAlias, Object>(ExprPacketInfoAlias.class, Object.class, ExpressionType.PROPERTY);
    private static final GroupedList<PacketInfoAlias> aliases = new GroupedList();
    private static boolean registered = false;

    private PacketInfoAlias alias;
    private Expression<PacketContainer> packetExpression;

    public static void registerNecessaryElements() {
        syntaxElementInfo.register();
        EventValues.registerEventValue(PacketInfoAlias.ContainerEvent.class, PacketContainer.class, new Getter<PacketContainer, PacketInfoAlias.ContainerEvent>() {
            @Override
            public PacketContainer get(PacketInfoAlias.ContainerEvent containerEvent) {
                return containerEvent.packet;
            }
        }, 0);
        Registration.registerEvent("Packet Info Alias", ScopePacketInfoAliases.class, PacketInfoAlias.ContainerEvent.class, "packet info aliases for %packettype%");

    }

    public static GroupedList.Key registerAliases(Collection<PacketInfoAlias> aliases) {
        if (!registered) {
            syntaxElementInfo.register();
        }
        GroupedList.Key key = ExprPacketInfoAlias.aliases.addGroup(aliases);
        setPatterns();
        return key;
    }

    private static void setPatterns() {
        syntaxElementInfo.setPatterns(aliases.stream().map(alias -> alias.alias).toArray(String[]::new));
    }

    public static void unregisterAliases(GroupedList.Key key) {
        aliases.removeGroup(key);
        setPatterns();
    }

    public static void unregisterAllAliases() {
        aliases.clear();
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
        alias = aliases.get(i);
        return true;
    }
}
