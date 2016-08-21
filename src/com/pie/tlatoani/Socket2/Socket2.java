package com.pie.tlatoani.Socket2;

import com.pie.tlatoani.Mundo;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

/**
 * Created by Tlatoani on 8/19/16.
 */
public class Socket2 {
    private Socket socket;
    private volatile State state;
    private BufferedWriter writer = null;
    private ArrayList<String> waitingWrites = new ArrayList<>();

    public Socket2(String host, int port, int timeout) {
        this.socket = new Socket();
        this.state = State.CONNECTING;
        Mundo.scheduler.runTaskAsynchronously(Mundo.instance, new SocketConnector(host, port, timeout));
    }

    //Should only be used with already connected sockets (They can be already closed, but why would you need that?)
    public Socket2(Socket socket) {
        if (!socket.isConnected()) {
            throw new IllegalArgumentException("The socket " + socket + " should have been already connected prior to use with the Socket2(Socket socket) constructor!");
        }
        this.socket = socket;
        this.state = State.CONNECTED;
        Mundo.scheduler.runTaskAsynchronously(Mundo.instance, new SocketReader());
    }

    //State

    public enum State {
        CONNECTING,
        CONNECTED,
        CLOSED,
        TIMEOUT,
        FAILURE
    }

    //Classes

    private class SocketConnector implements Runnable {
        String host;
        int port;
        int timeout;

        SocketConnector(String host, int port, int timeout) {
            this.host = host;
            this.port = port;
            this.timeout = timeout;
        }

        @Override
        public void run() {
            try {
                socket.connect(new InetSocketAddress(host, port), timeout);
                onConnect();
            } catch (SocketTimeoutException e) {
                onTimeout();
            } catch (IOException e) {
                onFailure();
                Mundo.reportException(this, e);
            }
        }
    }

    private class SocketReader implements Runnable {

        @Override
        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    onRead(line);
                }
                onClose();
            } catch (IOException e) {
                Mundo.reportException(this, e);
            }
        }
    }

    private class SocketWriter implements Runnable {
        String string;

        SocketWriter(String string) {
            this.string = string;
        }

        @Override
        public void run() {
            try {
                writer.write(string);
                writer.newLine();
                writer.flush();
            } catch (IOException e) {
                Mundo.reportException(this, e);
            }
        }
    }

    private class SocketCloser implements Runnable {

        @Override
        public void run() {
            try {
                socket.close();
            } catch (IOException e) {
                Mundo.reportException(this, e);
            }
        }
    }

    //Expressions

    public String getLocalHost() {
        return socket.getLocalAddress().getHostName();
    }

    public int getLocalPort() {
        return socket.getLocalPort();
    }

    public String getExternalHost() {
        return socket.getInetAddress().getHostName();
    }

    public int getExternalPort() {
        return socket.getPort();
    }

    public State getState() {
        return state;
    }

    //Effects

    public void write(String string) {
        if (state == State.CONNECTING) {
            waitingWrites.add(string);
        }
        else if (state == State.CONNECTED) {
            if (writer == null) {
                try {
                    writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                } catch (IOException e) {
                    Mundo.reportException(this, e);
                }
            }
            Mundo.scheduler.runTaskAsynchronously(Mundo.instance, new SocketWriter(string));
        }
    }

    public void close() {
        Mundo.scheduler.runTaskAsynchronously(Mundo.instance, new SocketCloser());
    }

    //Events

    private void onRead(String string) {
        //Here put some kind of thing to do stuff
    }

    private void onConnect() {
        state = State.CONNECTED;
        Mundo.scheduler.runTask(Mundo.instance, new Runnable() {
            @Override
            public void run() {
                for (String string : waitingWrites) {
                    write(string);
                }
            }
        });
        Mundo.scheduler.runTaskAsynchronously(Mundo.instance, new SocketReader());
    }

    private void onTimeout() {
        state = State.TIMEOUT;
    }

    private void onFailure() {
        state = State.FAILURE;
    }

    private void onClose() {
        state = State.CLOSED;
        //Here put some kind of thing to do stuff
    }
}
