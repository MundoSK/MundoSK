package com.pie.tlatoani.ProtocolLib;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.comphenix.protocol.PacketType;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Tlatoani on 4/29/16.
 */
public class ExprAllPacketTypes extends SimpleExpression<PacketType> {
    private static List<PacketType> listToConvert;
    private static PacketType[] returnArray;

    static {
        listToConvert = new ArrayList<PacketType>();
        Iterator<PacketType> tempIterator = PacketType.values().iterator();
        while (tempIterator.hasNext()) {
            listToConvert.add(tempIterator.next());
        }
        returnArray = listToConvert.toArray(new PacketType[0]);
    }

    @Override
    public PacketType[] get(Event event) {
        return returnArray;
    }

    @Override
    public Iterator<PacketType> iterator(Event event) {
        return listToConvert.iterator();
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends PacketType> getReturnType() {
        return PacketType.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "all packettypes";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        return true;
    }
}
