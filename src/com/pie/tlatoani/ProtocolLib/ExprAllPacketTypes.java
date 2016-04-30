package com.pie.tlatoani.ProtocolLib;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.reflect.ObjectEnum;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Created by Tlatoani on 4/29/16.
 */
public class ExprAllPacketTypes extends SimpleExpression<PacketType> {
    private static List<PacketType> listToConvert = new ArrayList<PacketType>();
    private static PacketType[] returnArray;
    private static Map<String, PacketType> nametoptype = new HashMap<String, PacketType>();
    private static Map<PacketType, String> ptypetoname = new HashMap<PacketType, String>();

    static {
        addPacketTypes(PacketType.Play.Server.getInstance(), "play_server");
        addPacketTypes(PacketType.Play.Client.getInstance(), "play_client");
        addPacketTypes(PacketType.Handshake.Server.getInstance(), "handshake_server");
        addPacketTypes(PacketType.Handshake.Client.getInstance(), "handshake_client");
        addPacketTypes(PacketType.Login.Server.getInstance(), "login_server");
        addPacketTypes(PacketType.Login.Client.getInstance(), "login_client");
        addPacketTypes(PacketType.Status.Server.getInstance(), "status_server");
        addPacketTypes(PacketType.Status.Client.getInstance(), "status_client");
        returnArray = listToConvert.toArray(new PacketType[0]);
    }

    private static void addPacketTypes(ObjectEnum<PacketType> packetTypes, String prefix) {
        Iterator<PacketType> packetTypeIterator = packetTypes.iterator();
        while (packetTypeIterator.hasNext()) {
            PacketType current = packetTypeIterator.next();
            listToConvert.add(current);
            String fullname = prefix + "_" + current.name().toLowerCase();
            nametoptype.put(fullname, current);
            ptypetoname.put(current, fullname);
        }
    }

    public static PacketType fromString(String s) {
        return nametoptype.get(s);
    }

    public static String PacketTypeToString(PacketType ptype) {
        return ptypetoname.get(ptype);
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
