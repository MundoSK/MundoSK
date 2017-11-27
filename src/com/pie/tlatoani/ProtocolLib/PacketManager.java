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
import com.pie.tlatoani.ProtocolLib.Alias.PacketInfoAlias;
import com.pie.tlatoani.ProtocolLib.Alias.ScopePacketInfoAliases;
import com.pie.tlatoani.Registration.DocumentationBuilder;
import com.pie.tlatoani.Util.Logging;
import com.pie.tlatoani.Registration.Registration;
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
        if (!pLibVersion.substring(0, 1).equals("4") || pLibVersion.substring(0, 3).equals("4.0")) {
            Logging.info("Your version of ProtocolLib is " + pLibVersion);
            Logging.info("MundoSK requires that you run at least version 4.1 of ProtocolLib");
            Logging.info("If you are running at least version 4.1 of ProtocolLib, please post a message on MundoSK's thread on forums.skunity.com");
        }
        packetTypesByName = createNameToPacketTypeMap();
        Registration.registerEnum(PacketType.class, "packettype", packetTypesByName);
        Registration.registerType(PacketContainer.class, "packet");
        Registration.registerEffect(EffSendPacket.class, "send packet[s] %packets% to %players%", "send %players% packet[s] %packets%");
        Registration.registerEffect(EffReceivePacket.class, "rec(ei|ie)ve packet[s] %packets% from %players%"); //Included incorrect spelling to avoid wasted time
        Registration.registerEffect(EffPacketInfo.class, "packet info %packet%");
        Registration.registerEvent("Packet Event", EvtPacketEvent.class, MundoPacketEvent.class, "packet event %packettypes%");
        Registration.registerEventValue(MundoPacketEvent.class, PacketContainer.class, MundoPacketEvent::getPacket);
        Registration.registerEventValue(MundoPacketEvent.class, PacketType.class, MundoPacketEvent::getPacketType);
        Registration.registerEventValue(MundoPacketEvent.class, Player.class, MundoPacketEvent::getPlayer);
        Registration.registerPropertyExpression(ExprTypeOfPacket.class, PacketType.class, "packet", "packettype");
        registerPacketInfoExpression(ExprNewPacket.class, PacketContainer.class, "new %packettype% packet");
        registerPacketInfoExpression(ExprJSONObjectOfPacket.class, JSONObject.class,
                "(%-string%" + ExprJSONObjectOfPacket.getConverterNamesPattern(true) + ") pjson %number% of %packet%",
                "(%-string%" + ExprJSONObjectOfPacket.getConverterNamesPattern(false) + ") array pjson %number% of %packet%");
        registerPacketInfoExpression(ExprObjectOfPacket.class, Object.class,
                "(0¦%-classinfo/string%" + ExprObjectOfPacket.getConverterNamesPattern(true) + ") pinfo %number% of %packet%",
                "(0¦%-classinfo/string%" + ExprObjectOfPacket.getConverterNamesPattern(false) + ") array pinfo %number% of %packet%");
        registerPacketInfoExpression(ExprPrimitiveOfPacket.class, Number.class, "(0¦byte|1¦short|2¦int|3¦long|4¦float|5¦double) pnum %number% of %packet%");
        registerPacketInfoExpression(ExprPrimitiveArrayOfPacket.class, Number.class, "(0¦int|1¦byte) array pnum %number% of %packet%");
        registerPacketInfoExpression(ExprEntityOfPacket.class, Entity.class,
                "%world% pentity %number% of %packet%",
                "%world% pentity array %number% of %packet%");
        registerPacketInfoExpression(ExprEnumOfPacket.class, String.class, "%string% penum %number% of %packet%");

        ExprPacketInfoAlias.registerNecessaryElements();
    }

    private static <E extends Expression<T>, T> DocumentationBuilder.Expression registerPacketInfoExpression(Class<E> exprClass, Class<T> returnType, String... patterns) {
        packetInfoExpressionInfos.add(new ExpressionInfo<>(patterns, returnType, exprClass));
        return Registration.registerExpression(exprClass, returnType, ExpressionType.COMBINED, patterns);
    }

    public static Iterator<ExpressionInfo<?, ?>> packetInfoExpressionInfoIterator() {
        return packetInfoExpressionInfos.iterator();
    }

    public PacketType getPacketTypeFromName(String name) {
        return packetTypesByName.get(name.toLowerCase());
    }

    public static void onPacketEvent(PacketType packetType, Consumer<PacketEvent> handler) {
        onPacketEvent(packetType, ListenerPriority.NORMAL, handler);
    }

    public static void onPacketEvent(PacketType packetType, ListenerPriority priority, Consumer<PacketEvent> handler) {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Mundo.INSTANCE, priority, packetType) {
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
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Mundo.INSTANCE, priority, packetTypes) {
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
