package com.pie.tlatoani.ProtocolLib;

import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser;
import com.comphenix.protocol.PacketType;
import org.bukkit.event.Event;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Tlatoani on 4/29/16.
 */
public class EvtPacketEvent extends SkriptEvent {
    private List<PacketType> packetTypes;
    private String packetTypesToString;

    @Override
    public boolean init(Literal<?>[] literals, int i, SkriptParser.ParseResult parseResult) {
        PacketType[] packetTypeArray = ((Literal<PacketType>) literals[0]).getAll();
        packetTypesToString = literals[0].toString();
        MundoPacketEvent.addListener(packetTypeArray);
        packetTypes = Arrays.asList(packetTypeArray);
        return true;
    }

    @Override
    public boolean check(Event event) {
        if (event instanceof MundoPacketEvent) {
            if (packetTypes.contains(((MundoPacketEvent) event).getPacketType())) {
                return true;
            }
            else return false;
        } else return false;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "packet event " + packetTypesToString;
    }
}
