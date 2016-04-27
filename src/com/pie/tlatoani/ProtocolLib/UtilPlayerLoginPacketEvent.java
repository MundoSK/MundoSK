package com.pie.tlatoani.ProtocolLib;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.pie.tlatoani.Mundo;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.Bukkit;

/**
 * Created by Tlatoani on 4/27/16.
 */
public class UtilPlayerLoginPacketEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private PacketEvent packet;
    private Boolean hardcorestyle = false;

    static {
        if (Bukkit.getServer().getPluginManager().getPlugin("ProtocolLib") != null) {
            ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Mundo.instance, ListenerPriority.NORMAL, PacketType.Play.Server.LOGIN) {
            	@Override
            	public void onPacketSending(PacketEvent event) {
                    UtilPlayerLoginPacketEvent evt = new UtilPlayerLoginPacketEvent(event);
                    Bukkit.getServer().getPluginManager().callEvent(evt);
            	}
            });
        }
    }

    public UtilPlayerLoginPacketEvent(PacketEvent packet) {
        this.packet = packet;
    }

    public void setHardCoreStyle() {
        packet.getPacket().getBooleans().write(0, true);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
