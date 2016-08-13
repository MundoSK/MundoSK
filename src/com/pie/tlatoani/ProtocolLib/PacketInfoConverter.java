package com.pie.tlatoani.ProtocolLib;

import com.comphenix.protocol.events.PacketContainer;

/**
 * Created by Tlatoani on 8/13/16.
 */
public interface PacketInfoConverter<T> {

    T get(PacketContainer packet, Integer index);

    void set(PacketContainer packet, Integer index, T value);
}
