package com.pie.tlatoani.ProtocolLib;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.pie.tlatoani.Mundo;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tlatoani on 4/29/16.
 */
public class UtilPacketSendingEvent extends Event{
    private static final HandlerList handlers = new HandlerList();
    private static List<PacketType> listeners = new ArrayList<PacketType>();
    private PacketEvent packet;
    private PacketType packetType;
    private static ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

    public static void addListener(PacketType[] packettypes) {
        List<PacketType> cleanlist = new ArrayList<PacketType>();
        for (int i = 0; i < packettypes.length; i++) {
            if (!listeners.contains(packettypes[i])) {
                listeners.add(packettypes[i]);
                cleanlist.add(packettypes[i]);
            }
        }
        PacketType[] packettypearray = cleanlist.toArray(new PacketType[0]);
        protocolManager.addPacketListener(new PacketAdapter(Mundo.instance, ListenerPriority.NORMAL, packettypearray) {
            @Override
            public void onPacketSending(PacketEvent event) {
                UtilPacketArriveEvent evt = new UtilPacketArriveEvent(event);
                Bukkit.getServer().getPluginManager().callEvent(evt);
            }
        });
    }

    public UtilPacketSendingEvent(PacketEvent packet) {
        this.packet = packet;
        this.packetType = packet.getPacketType();
    }

    public PacketEvent getPacketEvent() {
        return this.packet;
    }

    public PacketType getPacketType() {
        return this.packetType;
    }

    public Player getPlayer() {
        return this.packet.getPlayer();
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}