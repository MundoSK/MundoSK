package com.pie.tlatoani.WebSocket.Handshake;

import ch.njol.skript.classes.Changer;
import com.pie.tlatoani.Core.Skript.ChangeablePropertyExpression;
import com.pie.tlatoani.Core.Static.OptionalUtil;
import mundosk_libraries.java_websocket.handshake.Handshakedata;
import mundosk_libraries.java_websocket.handshake.ServerHandshake;
import mundosk_libraries.java_websocket.handshake.ServerHandshakeBuilder;

/**
 * Created by Tlatoani on 12/29/17.
 */
public class ExprHTTPStatusMessage extends ChangeablePropertyExpression<Handshakedata, String> {

    @Override
    public void change(Handshakedata handshakedata, String s, Changer.ChangeMode changeMode) {
        if (handshakedata instanceof ServerHandshakeBuilder) {
            ((ServerHandshakeBuilder) handshakedata).setHttpStatusMessage(s);
        }
    }

    @Override
    public Changer.ChangeMode[] getChangeModes() {
        return new Changer.ChangeMode[]{Changer.ChangeMode.SET};
    }

    @Override
    public String convert(Handshakedata handshakedata) {
        return OptionalUtil.cast(handshakedata, ServerHandshake.class).map(ServerHandshake::getHttpStatusMessage).orElse(null);
    }
}
