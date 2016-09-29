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
    private String password;

    private static Map<Integer, SocketListener> listenerMap = new HashMap<>();

    //API

    public boolean isListeningOnPort(int port) {
        SocketListener listener = listenerMap.get(port);
        return listener == null ? false : listener.listening;
    }

    public String getPasswordOfPort(int port) {
        SocketListener listener = listenerMap.get(port);
        return listener == null ? null : listener.password;
    }

    public void startListeningOnPort(int port, String password) {
        if (password != null) {
            SocketListener socketListener = listenerMap.get(port);
            if (socketListener == null) {
                socketListener = new SocketListener(port);
                listenerMap.put(port, socketListener);
            }
            socketListener.password = password;
            socketListener.start();
        }
    }

    public void stopListeningOnPort(int port) {
        SocketListener socketListener = listenerMap.get(port);
        if (socketListener != null) {
            socketListener.stop();
            listenerMap.remove(port);
        }
    }

    public void setPasswordOfPort(int port, String password) {
        if (password != null && isListeningOnPort(port)) {
            listenerMap.get(port).password = password;
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
                    onSocketConnection(socket);
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

    private void onSocketConnection(Socket socket) {
        Mundo.sync(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket2 socket2 = new Socket2();
                    socket2.use(socket, password, new Runnable() {
                        @Override
                        public void run() {
                            if (socket2.getState() == Socket2.State.CONNECTED) {
                                ConnectEvent event = new ConnectEvent(socket2);
                                Bukkit.getServer().getPluginManager().callEvent(event);
                            }
                        }
                    });
                } catch (Exception e) {
                    Mundo.reportException(SocketListener.class, e);
                }

            }
        });
    }
}
