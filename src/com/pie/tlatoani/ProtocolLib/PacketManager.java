package com.pie.tlatoani.ProtocolLib;

import ch.njol.skript.lang.ExpressionType;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.pie.tlatoani.Mundo;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.util.Map;

/**
 * Created by Tlatoani on 8/8/17.
 */
public class PacketManager {
    
    public static void load() {
        Mundo.info("You've discovered the amazing realm of ProtocolLib packet syntaxes!");
        String pLibVersion = Bukkit.getPluginManager().getPlugin("ProtocolLib").getDescription().getVersion();
        if (!pLibVersion.substring(0, 1).equals("4") || pLibVersion.substring(0, 3).equals("4.0")) {
            Mundo.info("Your version of ProtocolLib is " + pLibVersion);
            Mundo.info("MundoSK requires that you run at least version 4.1 of ProtocolLib");
            Mundo.info("If you are running at least version 4.1 of ProtocolLib, please post a message on MundoSK's thread on forums.skunity.com");
        }
        Mundo.registerEnum(PacketType.class, "packettype", new PacketType[0], UtilPacketEvent.nametoptype.entrySet().toArray(new Map.Entry[0]));
        Mundo.registerType(PacketContainer.class, "packet");
        Mundo.registerEffect(EffSendPacket.class, "send packet %packet% to %player%", "send %player% packet %packet%");
        Mundo.registerEffect(EffReceivePacket.class, "rec(ei|ie)ve packet %packet% from %player%"); //Included incorrect spelling to avoid wasted time
        Mundo.registerEffect(EffPacketInfo.class, "packet info %packet%");
        Mundo.registerEvent("Packet Event", EvtPacketEvent.class, UtilPacketEvent.class, "packet event %packettypes%");
        Mundo.registerEventValue(UtilPacketEvent.class, PacketContainer.class, UtilPacketEvent::getPacket);
        Mundo.registerEventValue(UtilPacketEvent.class, PacketType.class, UtilPacketEvent::getPacketType);
        Mundo.registerEventValue(UtilPacketEvent.class, Player.class, UtilPacketEvent::getPlayer);
        Mundo.registerExpression(ExprTypeOfPacket.class, PacketType.class, ExpressionType.SIMPLE, "packettype of %packet%", "%packet%'s packettype");
        Mundo.registerExpression(ExprNewPacket.class, PacketContainer.class, ExpressionType.PROPERTY, "new %packettype% packet");
        Mundo.registerExpression(ExprJSONObjectOfPacket.class, JSONObject.class, ExpressionType.PROPERTY,
                "(%-string%" + ExprJSONObjectOfPacket.getConverterNamesPattern(true) + ") pjson %number% of %packet%",
                "(%-string%" + ExprJSONObjectOfPacket.getConverterNamesPattern(false) + ") array pjson %number% of %packet%");
        Mundo.registerExpression(ExprObjectOfPacket.class, Object.class, ExpressionType.PROPERTY,
                "(0¦%-classinfo/string%" + ExprObjectOfPacket.getConverterNamesPattern(true) + ") pinfo %number% of %packet%",
                "(0¦%-classinfo/string%" + ExprObjectOfPacket.getConverterNamesPattern(false) + ") array pinfo %number% of %packet%");
        Mundo.registerExpression(ExprPrimitiveOfPacket.class, Number.class, ExpressionType.PROPERTY, "(0¦byte|1¦short|2¦int|3¦long|4¦float|5¦double) pnum %number% of %packet%");
        Mundo.registerExpression(ExprPrimitiveArrayOfPacket.class, Number.class, ExpressionType.PROPERTY, "(0¦int|1¦byte) array pnum %number% of %packet%");
        Mundo.registerExpression(ExprEntityOfPacket.class, Entity.class, ExpressionType.PROPERTY,
                "%world% pentity %number% of %packet%",
                "%world% pentity array %number% of %packet%");
        Mundo.registerExpression(ExprEnumOfPacket.class, String.class, ExpressionType.PROPERTY, "%string% penum %number% of %packet%");
    }
}
