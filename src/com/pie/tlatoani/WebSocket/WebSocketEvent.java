package com.pie.tlatoani.WebSocket;

import com.pie.tlatoani.Util.BaseEvent;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 5/4/17.
 */
public interface WebSocketEvent {

    //WebSocket getWebSocket();

    class Open extends BaseEvent implements WebSocketEvent {

    }

    class Close extends BaseEvent implements WebSocketEvent {

    }

    class Message extends BaseEvent implements WebSocketEvent {
        public final String message;

        public Message(String message) {
            this.message = message;
        }
    }

    class Error extends BaseEvent implements WebSocketEvent {
        public final Throwable error;

        public Error(Throwable error) {
            this.error = error;
        }
    }

    interface Server extends WebSocketEvent {

        //WebSocketServer getWebSocketServer();

    }

    class ServerStart extends BaseEvent implements Server {

    }

    class ServerStop extends BaseEvent implements Server {

    }

    class ServerOpen extends Open implements Server {

    }

    class ServerClose extends Close implements Server {

    }

    class ServerMessage extends Message implements Server {
        public ServerMessage(String message) {
            super(message);
        }
    }

    class ServerError extends Error implements Server {
        public ServerError(Throwable error) {
            super(error);
        }
    }
}
