package com.pie.tlatoani.ProtocolLib;

import ch.njol.skript.lang.ExpressionType;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.pie.tlatoani.Util.Logging;
import com.pie.tlatoani.Util.Registration;
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
        Logging.info("You've discovered the amazing realm of ProtocolLib packet syntaxes!");
        String pLibVersion = Bukkit.getPluginManager().getPlugin("ProtocolLib").getDescription().getVersion();
        if (!pLibVersion.substring(0, 1).equals("4") || pLibVersion.substring(0, 3).equals("4.0")) {
            Logging.info("Your version of ProtocolLib is " + pLibVersion);
            Logging.info("MundoSK requires that you run at least version 4.1 of ProtocolLib");
            Logging.info("If you are running at least version 4.1 of ProtocolLib, please post a message on MundoSK's thread on forums.skunity.com");
        }
        Registration.registerEnum(PacketType.class, "packettype", new PacketType[0], PacketEvent.nametoptype.entrySet().toArray(new Map.Entry[0]));
        Registration.registerType(PacketContainer.class, "packet");
        Registration.registerEffect(EffSendPacket.class, "send packet %packet% to %player%", "send %player% packet %packet%");
        Registration.registerEffect(EffReceivePacket.class, "rec(ei|ie)ve packet %packet% from %player%"); //Included incorrect spelling to avoid wasted time
        Registration.registerEffect(EffPacketInfo.class, "packet info %packet%");
        Registration.registerEvent("Packet Event", EvtPacketEvent.class, PacketEvent.class, "packet event %packettypes%");
        Registration.registerEventValue(PacketEvent.class, PacketContainer.class, PacketEvent::getPacket);
        Registration.registerEventValue(PacketEvent.class, PacketType.class, PacketEvent::getPacketType);
        Registration.registerEventValue(PacketEvent.class, Player.class, PacketEvent::getPlayer);
        Registration.registerExpression(ExprTypeOfPacket.class, PacketType.class, ExpressionType.SIMPLE, "packettype of %packet%", "%packet%'s packettype");
        Registration.registerExpression(ExprNewPacket.class, PacketContainer.class, ExpressionType.PROPERTY, "new %packettype% packet");
        Registration.registerExpression(ExprJSONObjectOfPacket.class, JSONObject.class, ExpressionType.PROPERTY,
                "(%-string%" + ExprJSONObjectOfPacket.getConverterNamesPattern(true) + ") pjson %number% of %packet%",
                "(%-string%" + ExprJSONObjectOfPacket.getConverterNamesPattern(false) + ") array pjson %number% of %packet%");
        Registration.registerExpression(ExprObjectOfPacket.class, Object.class, ExpressionType.PROPERTY,
                "(0¦%-classinfo/string%" + ExprObjectOfPacket.getConverterNamesPattern(true) + ") pinfo %number% of %packet%",
                "(0¦%-classinfo/string%" + ExprObjectOfPacket.getConverterNamesPattern(false) + ") array pinfo %number% of %packet%");
        Registration.registerExpression(ExprPrimitiveOfPacket.class, Number.class, ExpressionType.PROPERTY, "(0¦byte|1¦short|2¦int|3¦long|4¦float|5¦double) pnum %number% of %packet%");
        Registration.registerExpression(ExprPrimitiveArrayOfPacket.class, Number.class, ExpressionType.PROPERTY, "(0¦int|1¦byte) array pnum %number% of %packet%");
        Registration.registerExpression(ExprEntityOfPacket.class, Entity.class, ExpressionType.PROPERTY,
                "%world% pentity %number% of %packet%",
                "%world% pentity array %number% of %packet%");
        Registration.registerExpression(ExprEnumOfPacket.class, String.class, ExpressionType.PROPERTY, "%string% penum %number% of %packet%");
    }
}
