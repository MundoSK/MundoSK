package com.pie.tlatoani.ProtocolLib.Alias;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.DefaultExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import ch.njol.util.Kleenean;
import com.comphenix.protocol.events.PacketContainer;
import com.pie.tlatoani.Core.Registration.ModifiableSyntaxElementInfo;
import com.pie.tlatoani.Core.Registration.Registration;
import com.pie.tlatoani.Util.Collections.GroupedList;
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
        Registration.registerEvent("Packet Info Alias", ScopePacketInfoAliases.class, PacketInfoAlias.ContainerEvent.class, "packet info aliases for %packettype%")
                .document("Packet Info Alias", "1.8.2", "Not an actual event, but rather a group of packet info aliases to be used with packets of the specified packettype. "
                        + "Packet info aliases are aliases for specific usages of packet info expressions. "
                        + "Under the main scope lines are written in the form '<new syntax> " + ScopePacketInfoAliases.SEPARATOR + " <old syntax>', "
                        + "where the old syntax is how you would normally write an expression for the desired packet info, and the new syntax is how you want to be able to write it. "
                        + "Note that the new syntax is essentially being registered as a Skript syntax, so you can write it with features of Skript syntax like optional parts enclosed in '[]', "
                        + "and multiple usages of '%packet%' are allowed in your syntax, though only one of them should be possible to use at a time since only one packet is used to evaluate the alias "
                        + "As of MundoSK 1.8.6, your syntax does not have to require or even allow the usage of '%packet%'. In this case, 'event-packet' will be used as the packet. "
                        + "A small addition to normal Skript syntax is that now, if you would like to have a group of different options for syntax but also have the whole thing be optional, instead of writing '[(a|b|...)]' you can write '[a|b|...]'.")
                .example("packet info aliases for play_server_world_border:"
                        , "\tborder action of %packet% = \"WorldBorderAction\" penum 0 of %packet%"
                        , "\tborder portal teleport boundary of %packet% = int pnum 0 of %packet%"
                        , "\tborder center x[-coord] of %packet% = double pnum 0 of %packet%"
                        , "\tborder center z[-coord] of %packet% = double pnum 1 of %packet%"
                        , "\tborder old radius of %packet% = double pnum 2 of %packet%"
                        , "\tborder radius of %packet% = double pnum 3 of %packet%"
                        , "\tborder speed of %packet% = long pnum 0 of %packet%"
                        , "\tborder warning time [span|length] of %packet% = int pnum 1 of %packet%"
                        , "\tborder warning (distance|blocks) of %packet% = int pnum 2 of %packet%"
                        , ""
                        , "on packet event play_server_world_border:"
                        , "\tbroadcast \"Border Action: %border action of event-packet%\"");

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
        return alias.get(packetExpression.getSingle(event));
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
        if (packetExpression == null) {
            DefaultExpression<PacketContainer> defaultExpression = Classes.getExactClassInfo(PacketContainer.class).getDefaultExpression();
            if (defaultExpression.init()) {
                packetExpression = defaultExpression;
            } else {
                return false;
            }
        }
        alias = aliases.get(i);
        return true;
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        alias.change(packetExpression.getSingle(event), delta, mode);
    }

    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        return alias.acceptChange(mode);
    }
}
