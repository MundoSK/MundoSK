package mundosk_libraries.java_websocket.server;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.List;

import mundosk_libraries.java_websocket.WebSocketAdapter;
import mundosk_libraries.java_websocket.WebSocketImpl;
import mundosk_libraries.java_websocket.drafts.Draft;
import mundosk_libraries.java_websocket.server.WebSocketServer.WebSocketServerFactory;

public class DefaultWebSocketServerFactory implements WebSocketServerFactory {
	@Override
	public WebSocketImpl createWebSocket(WebSocketAdapter a, Draft d) {
		return new WebSocketImpl( a, d );
	}
	@Override
	public WebSocketImpl createWebSocket( WebSocketAdapter a, List<Draft> d) {
		return new WebSocketImpl( a, d );
	}
	@Override
	public SocketChannel wrapChannel( SocketChannel channel, SelectionKey key ) {
		return channel;
	}
	@Override
	public void close() {
	}
}