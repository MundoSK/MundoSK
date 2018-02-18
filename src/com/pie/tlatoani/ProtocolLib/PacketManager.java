package com.pie.tlatoani.ProtocolLib;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionInfo;
import ch.njol.skript.lang.ExpressionType;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.ProtocolLib.Alias.ExprPacketInfoAlias;
import com.pie.tlatoani.Registration.DocumentationBuilder;
import com.pie.tlatoani.Registration.Registration;
import com.pie.tlatoani.Util.Logging;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Consumer;

/**
 * Created by Tlatoani on 8/8/17.
 */
public class PacketManager {
    private static Map<String, PacketType> packetTypesByName;
    private static List<ExpressionInfo<?, ?>> packetInfoExpressionInfos = new ArrayList<>();
    
    public static void load() {
        Logging.info("You've discovered the amazing realm of ProtocolLib packet syntaxes!");
        String pLibVersion = Bukkit.getPluginManager().getPlugin("ProtocolLib").getDescription().getVersion();
        if (!pLibVersion.startsWith("4") || pLibVersion.startsWith("4.0")) {
            Logging.info("Your version of ProtocolLib is " + pLibVersion);
            Logging.info("MundoSK requires that you run at least version 4.1 of ProtocolLib");
            Logging.info("If you are running at least version 4.1 of ProtocolLib, please post a message on MundoSK's thread on forums.skunity.com");
        }
        packetTypesByName = createNameToPacketTypeMap();
        ExprObjectOfPacket.registerConverters();
        ExprJSONObjectOfPacket.registerConverters();
        Registration.registerEnum(PacketType.class, "packettype", packetTypesByName)
                .document("PacketType", "1.8", "A type of a packet. The ones that are available for you depend on your Minecraft version. "
                        + "If you would like to see them, do '/mundosk doc packettype' in your console. "
                        + "Alternatively, use the All Packettypes expression, loop through it, and print them.")
                .example("command /allpackettypes [<string>]:"
                        , "\tpermission: admin"
                        , "\tusage: /allpackettypes [filter]"
                        , "\ttrigger:"
                        , "\t\tif string-arg is set:"
                        , "\t\t\tmessage \"&2Messaging all packettypes that contain &6%string-arg%&2!\""
                        , "\t\telse:"
                        , "\t\t\tmessage \"&2Messaging all packettypes!\""
                        , "\t\tloop all packettypes:"
                        , "\t\t\tif string-arg is set:"
                        , "\t\t\t\tif \"%loop-value%\" contains string-arg:"
                        , "\t\t\t\t\tmessage \"&a%loop-value%\""
                        , "\t\t\telse:"
                        , "\t\t\t\tmessage \"&a%loop-value%\"");
        Registration.registerType(PacketContainer.class, "packet")
                .document("Packet", "1.8", "A packet. Packets are used by the Minecraft client and server to transmit information, "
                        + "and can be intercepted, read, and modified in order to gain information and modify the behavior of your server "
                        + "in certain ways that are not possible through Bukkit.");
        Registration.registerEffect(EffSendPacket.class, "send packet[s] %packets% to %players%", "send %players% packet[s] %packets%")
                .document("Send Packet", "1.8", "Sends the specified packet(s) to the specified player(s).");
        Registration.registerEffect(EffReceivePacket.class, "rec(ei|ie)ve packet[s] %packets% from %players%") //Included incorrect spelling to avoid wasted time
                .document("Receive Packet", "1.8", "Makes the server simulate receiving the specified packet(s) from the specified player(s)");
        Registration.registerEffect(EffPacketInfo.class, "packet info %packet%");
        Registration.registerEvent("Packet Event", EvtPacketEvent.class, MundoPacketEvent.class, "packet event %packettypes%")
                .document("Packet Event", "1.8", "Called when a packet of one of the specified types is being sent or received.")
                .eventValue(PacketContainer.class, "1.8", "The packet being sent or received.")
                .eventValue(PacketType.class, "1.8", "The packettype of the packet being sent or received. Equivalent to 'event packet's packettype'.")
                .eventValue(Player.class, "1.8", "The player sending or receiving the packet.");
        Registration.registerEventValue(MundoPacketEvent.class, PacketContainer.class, MundoPacketEvent::getPacket);
        Registration.registerEventValue(MundoPacketEvent.class, PacketType.class, MundoPacketEvent::getPacketType);
        Registration.registerEventValue(MundoPacketEvent.class, Player.class, MundoPacketEvent::getPlayer);
        Registration.registerPropertyExpression(ExprTypeOfPacket.class, PacketType.class, "packet", "packettype")
                .document("Type of Packet", "1.8", "An expression for the packettype of the specified packet.");
        registerPacketInfoExpression(ExprNewPacket.class, PacketContainer.class, "new %packettype% packet")
                .document("New Packet", "1.8", "An expression for a new packet of the specified type.");
        registerPacketInfoExpression(ExprJSONObjectOfPacket.class, JSONObject.class,
                "(%-string%" + ExprJSONObjectOfPacket.getConverterNamesPattern(true) + ") pjson %number% of %packet%",
                "(%-string%" + ExprJSONObjectOfPacket.getConverterNamesPattern(false) + ") array pjson %number% of %packet%")
                .document("JSON Field of Packet", "1.8", "An expression for certain fields of packets (first see the Packet Info expression for a more general explanation) "
                        + "that don't have equivalent types in Skript, and thus must be represented in the form of a jsonobject. "
                        + "The names of the fields can be written as strings but don't have to be as of MundoSK 1.8. "
                        + "Current accept JSON infos: 'chatcomponent', 'serverping', 'datawatcher', 'watchablecollection', 'gameprofile', 'nbt', "
                        + "'chatcomponent' array, 'playerinfodata' array.");
        registerPacketInfoExpression(ExprObjectOfPacket.class, Object.class,
                "(0¦%-classinfo/string%" + ExprObjectOfPacket.getConverterNamesPattern(true) + ") pinfo %number% of %packet%",
                "(0¦%-classinfo/string%" + ExprObjectOfPacket.getConverterNamesPattern(false) + ") array pinfo %number% of %packet%")
                .document("Field of Packet", "1.8", "An expression for the packet field of either the specified type or referred to by the specified string, "
                        + "with the specified index, of the specified packet. For example, 'string' can be used as the specified type to get a string field "
                        + "in the specified packet. 'array' must be included in the syntax when the fields are plural (ex. 'string array'). "
                        + "Use 'object' in order to access all fields of the packet. However, some fields will have objects in raw forms "
                        + "that cannot be easily used in Skript without the use of addons such as skript-mirror. Many of these fields can be converted into Skript "
                        + "types; most of these fields must be referred to by a certain string rather than a type: "
                        + "\"uuid\", \"material\", \"blockdata\", \"collection\" array, \"bytebuffer\" array (The last two are plural and must have array included in the syntax.");
        registerPacketInfoExpression(ExprPrimitiveOfPacket.class, Number.class, "(0¦byte|1¦short|2¦int|3¦long|4¦float|5¦double) pnum %number% of %packet%")
                .document("Number Field of Packet", "1.8", "An expression for different kinds of number fields of packets. "
                        + "First see the Packet Info expression for a more general explanation of packet fields.");
        registerPacketInfoExpression(ExprPrimitiveArrayOfPacket.class, Number.class, "(0¦int|1¦byte) array pnum %number% of %packet%")
                .document("Number Array Field of Packet", "1.8", "An expression for int array and byte array fields of packets. "
                        + "First see the Packet Info expression for a more general explanation of packet fields.");
        registerPacketInfoExpression(ExprEntityOfPacket.class, Entity.class,
                "%world% pentity %number% of %packet%",
                "%world% pentity array %number% of %packet%")
                .document("Entity Field of Packet", "1.8", "An expression for an entity field of a packet "
                        + "(first see the Packet Info expression for a more general explanation of packet fields). "
                        + "The specified world is used to determine which world to get the entity from, and thus should be specified "
                        + "as whichever world the player sending/receiving the packet is in.");
        registerPacketInfoExpression(ExprEnumOfPacket.class, String.class, "(arbitrary|%-string%) penum %number% of %packet%")
                .document("Enum Field of Packet", "1.8", "An expression for an enum field of a packet "
                        + "(first see the Packet Info o expression for a more general explanation of packet fields). "
                        + "The specified string is the name of the enum you are getting/setting. "
                        + "Using arbitrary gives you access to all enum fields, rather than just one particular type, "
                        + "and allows you to access certain enums that are NMS types rather than ProtocolLib and thus "
                        + "can't be accessed by their name.");

        ExprPacketInfoAlias.registerNecessaryElements();
    }

    private static <E extends Expression<T>, T> DocumentationBuilder registerPacketInfoExpression(Class<E> exprClass, Class<T> returnType, String... patterns) {
        packetInfoExpressionInfos.add(new ExpressionInfo<>(patterns, returnType, exprClass));
        return Registration.registerExpression(exprClass, returnType, ExpressionType.COMBINED, patterns);
    }

    public static Iterator<ExpressionInfo<?, ?>> packetInfoExpressionInfoIterator() {
        Logging.debug(PacketManager.class, "pIEI = " + packetInfoExpressionInfos);
        return packetInfoExpressionInfos.iterator();
    }

    public PacketType getPacketTypeFromName(String name) {
        return packetTypesByName.get(name.toLowerCase());
    }

    public static void onPacketEvent(PacketType packetType, Consumer<PacketEvent> handler) {
        onPacketEvent(packetType, ListenerPriority.NORMAL, handler);
    }

    public static void onPacketEvent(PacketType packetType, ListenerPriority priority, Consumer<PacketEvent> handler) {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Mundo.get(), priority, packetType) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                handler.accept(event);
            }

            @Override
            public void onPacketSending(PacketEvent event) {
                handler.accept(event);
            }
        });
    }

    public static void onPacketEvent(PacketType[] packetTypes, Consumer<PacketEvent> handler) {
        onPacketEvent(packetTypes, ListenerPriority.NORMAL, handler);
    }

    public static void onPacketEvent(PacketType[] packetTypes, ListenerPriority priority, Consumer<PacketEvent> handler) {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Mundo.get(), priority, packetTypes) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                handler.accept(event);
            }

            @Override
            public void onPacketSending(PacketEvent event) {
                handler.accept(event);
            }
        });
    }
    
    public static void sendPacket(PacketContainer packet, Object exceptLoc, Player player) {
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
        } catch (InvocationTargetException e) {
            Logging.reportException(exceptLoc, e);
        }
    }
    
    public static void sendPacket(PacketContainer packet, Object exceptLoc, Player[] players) {
        try {
            for (Player player : players) {
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
            }
        } catch (InvocationTargetException e) {
            Logging.reportException(exceptLoc, e);
        }
    }

    public static void sendPacket(PacketContainer packet, Object exceptLoc, Iterable<Player> players) {
        try {
            for (Player player : players) {
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
            }
        } catch (InvocationTargetException e) {
            Logging.reportException(exceptLoc, e);
        }
    }

    public static Map<String, PacketType> createNameToPacketTypeMap() {
        Map<String, PacketType> packetTypesByName = new HashMap<>();
        addPacketTypes(packetTypesByName, PacketType.Play.Server.getInstance().iterator(), "PLAY", true);
        addPacketTypes(packetTypesByName, PacketType.Play.Client.getInstance().iterator(), "PLAY", false);
        addPacketTypes(packetTypesByName, PacketType.Handshake.Server.getInstance().iterator(), "HANDSHAKE", true);
        addPacketTypes(packetTypesByName, PacketType.Handshake.Client.getInstance().iterator(), "HANDSHAKE", false);
        addPacketTypes(packetTypesByName, PacketType.Login.Server.getInstance().iterator(), "LOGIN", true);
        addPacketTypes(packetTypesByName, PacketType.Login.Client.getInstance().iterator(), "LOGIN", false);
        addPacketTypes(packetTypesByName, PacketType.Status.Server.getInstance().iterator(), "STATUS", true);
        addPacketTypes(packetTypesByName, PacketType.Status.Client.getInstance().iterator(), "STATUS", false);
        return packetTypesByName;
    }

    public static void addPacketTypes(Map<String, PacketType> map, Iterator<PacketType> packetTypeIterator, String prefix, Boolean isServer) {
        while (packetTypeIterator.hasNext()) {
            PacketType current = packetTypeIterator.next();
            String fullname = prefix + "_" + (isServer ? "SERVER" : "CLIENT") + "_" + current.name().toUpperCase();
            map.put(fullname, current);
        }
    }


}
