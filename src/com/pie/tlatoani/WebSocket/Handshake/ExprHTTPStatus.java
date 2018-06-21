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
public class ExprHTTPStatus extends ChangeablePropertyExpression<Handshakedata, Number> {

    @Override
    public void change(Handshakedata handshakedata, Number number, Changer.ChangeMode changeMode) {
        if (handshakedata instanceof ServerHandshakeBuilder) {
            ((ServerHandshakeBuilder) handshakedata).setHttpStatus(number.shortValue());
        }
    }

    @Override
    public Changer.ChangeMode[] getChangeModes() {
        return new Changer.ChangeMode[]{Changer.ChangeMode.SET};
    }

    @Override
    public Number convert(Handshakedata handshakedata) {
        return OptionalUtil.cast(handshakedata, ServerHandshake.class).map(ServerHandshake::getHttpStatus).orElse(null);
    }
}
