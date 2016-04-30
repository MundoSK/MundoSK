package com.pie.tlatoani.ProtocolLib;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser;
import com.comphenix.protocol.PacketType;
import org.bukkit.event.Event;

import java.util.*;

/**
 * Created by Tlatoani on 4/29/16.
 */
public class EvtPacketArrive extends SkriptEvent {
    private List<PacketType> packetTypes;

    @Override
    public boolean init(Literal<?>[] literals, int i, SkriptParser.ParseResult parseResult) {
        PacketType[] packetTypeArray = ((Literal<PacketType>) literals[0]).getAll();
        Boolean ok = true;
        for (int j = 0; j < packetTypeArray.length; j++) {
            if (ExprAllPacketTypes.isServer(packetTypeArray[j])) {
                Skript.error("The packettype " + ExprAllPacketTypes.PacketTypeToString(packetTypeArray[j]) + " is not a client side packettype!");
                ok = false;
            }
        }
        if (!ok) {
            return false;
        }
        UtilPacketArriveEvent.addListener(packetTypeArray);
        packetTypes = Arrays.asList(packetTypeArray);
        return true;
    }

    @Override
    public boolean check(Event arg0) {
        if (arg0 instanceof UtilPacketArriveEvent) {
            if (packetTypes.contains(((UtilPacketArriveEvent) arg0).getPacketType())) return true;
            else return false;
        } else return false;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "packet arrive of %packettypes%";
    }
}
