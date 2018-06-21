package com.pie.tlatoani.WebSocket.Handshake;

import ch.njol.skript.classes.Changer;
import com.pie.tlatoani.Core.Skript.ChangeablePropertyExpression;
import com.pie.tlatoani.Core.Static.OptionalUtil;
import mundosk_libraries.java_websocket.handshake.ClientHandshake;
import mundosk_libraries.java_websocket.handshake.ClientHandshakeBuilder;
import mundosk_libraries.java_websocket.handshake.Handshakedata;

/**
 * Created by Tlatoani on 12/29/17.
 */
public class ExprResourceDescriptor extends ChangeablePropertyExpression<Handshakedata, String> {
    @Override
    public void change(Handshakedata handshakedata, String s, Changer.ChangeMode changeMode) {
        if (handshakedata instanceof ClientHandshakeBuilder) {
            ((ClientHandshakeBuilder) handshakedata).setResourceDescriptor(s);
        }
    }

    @Override
    public Changer.ChangeMode[] getChangeModes() {
        return new Changer.ChangeMode[]{Changer.ChangeMode.SET};
    }

    @Override
    public String convert(Handshakedata handshakedata) {
        return OptionalUtil.cast(handshakedata, ClientHandshake.class).map(ClientHandshake::getResourceDescriptor).orElse(null);
    }
}
