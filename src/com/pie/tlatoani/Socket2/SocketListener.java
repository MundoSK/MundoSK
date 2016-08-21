package com.pie.tlatoani.Socket2;

import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.Util.BaseEvent;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tlatoani on 8/19/16.
 */
public class SocketListener implements Runnable {
    private int port;
    private ServerSocket serverSocket = null;
    private boolean listening;

    private static Map<Integer, SocketListener> listenerMap = new HashMap<>();

    //API

    public boolean isListeningOnPort(int port) {
        SocketListener listener = listenerMap.get(port);
        return listener == null ? false : listener.listening;
    }

    public void setListeningOnPort(int port, boolean whether) {
        if (whether) {
            SocketListener socketListener = listenerMap.get(port);
            if (socketListener == null) {
                socketListener = new SocketListener(port);
                listenerMap.put(port, socketListener);
            }
            socketListener.start();
        } else {
            SocketListener socketListener = listenerMap.get(port);
            if (socketListener != null) {
                socketListener.stop();
                listenerMap.remove(port);
            }
        }
    }

    public static class ConnectEvent extends BaseEvent {
        public final Socket2 socket2;

        public ConnectEvent(Socket2 socket2) {
            this.socket2 = socket2;
        }
    }

    //Private

    private SocketListener(int port) {
        this.port = port;
    }

    private void start() {
        if (!listening) {
            listening = true;
            Mundo.scheduler.runTaskAsynchronously(Mundo.instance, this);
        }

    }

    private void stop() {
        if (listening) {
            listening = false;
            try {
                serverSocket.close();
            } catch (IOException e) {
                Mundo.reportException(this, e);
            }
        }
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);
            while (listening) {
                try {
                    Socket socket = serverSocket.accept();
                    onSocketConnection(new Socket2(socket));
                } catch (SocketException e) {
                    if (listening) {
                        Mundo.reportException(SocketListener.class, e);
                    }
                }
            }
            serverSocket = null;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void onSocketConnection(Socket2 socket2) {
        ConnectEvent event = new ConnectEvent(socket2);
        Bukkit.getServer().getPluginManager().callEvent(event);
    }
}
