package com.pie.tlatoani.Socket2;

import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.Util.Notification;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;

/**
 * Created by Tlatoani on 8/19/16.
 */
public class Socket2 {
    private Socket socket;
    private volatile State state = State.NEW;
    private BufferedWriter writer = null;

    private Notification.Query<CloseNotification> closeQuery = new Notification.Query<>();
    private HashMap<String, Notification.Query<ReadNotification>> readQueries = new HashMap<>();

    public enum State {
        NEW,
        CONNECTING,
        CONNECTED,
        CLOSED,
        TIMEOUT_FAILURE,
        INCORRECT_PASSWORD_FAILURE,
        INCOMPATIBLE_SOCKET_FAILURE,
        UNKNOWN_FAILURE
    }

    //Runnables

    private class Connector implements Runnable {
        Runnable whenDone;
        String host;
        int port;
        int timeout;
        String password;

        Connector(Runnable whenDone, String host, int port, int timeout, String password) {
            this.whenDone = whenDone;
            this.host = host;
            this.port = port;
            this.timeout = timeout;
            this.password = password;
        }

        @Override
        public void run() {
            Socket2.this.socket = new Socket();
            try {
                socket.connect(new InetSocketAddress(host, port), timeout);
                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer.write("MundoSK_1.7.3:" + password);
                String verification = reader.readLine();
                if ("PASSWORD_CORRECT".equals(verification)) {
                    state = State.CONNECTED;
                    Mundo.sync(whenDone);
                    (new Reader(reader)).run();
                } else if ("PASSWORD_INCORRECT".equals(verification)) {
                    state = State.INCORRECT_PASSWORD_FAILURE;
                    Mundo.sync(whenDone);
                } else {
                    state = State.INCOMPATIBLE_SOCKET_FAILURE;
                    Mundo.sync(whenDone);
                }
            } catch (SocketTimeoutException e) {
                state = State.TIMEOUT_FAILURE;
            } catch (IOException e) {
                state = State.UNKNOWN_FAILURE;
                Mundo.reportException(this, e);
            }
        }
    }

    private class Reader implements Runnable {
        final String password;
        final BufferedReader reader;
        final Runnable whenDone;

        Reader(String password, Runnable whenDone) {
            this.password = password;
            this.reader = null;
            this.whenDone = whenDone;
        }

        Reader(BufferedReader reader) {
            this.password = null;
            this.reader = reader;
            this.whenDone = null;
        }

        @Override
        public void run() {
            try {
                String line;
                if (reader == null) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    Socket2.this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    line = reader.readLine();
                    if (line == null || !line.startsWith("MundoSK_1.7.3:")) {
                        Socket2.this.state = State.INCOMPATIBLE_SOCKET_FAILURE;
                        if (line != null) {
                            socket.close();
                        }
                        Mundo.sync(whenDone);
                    } else if (password.substring(14).equals(line)) {
                        Socket2.this.state = State.CONNECTED;
                        writer.write("PASSWORD_CORRECT");
                        writer.flush();
                        Mundo.sync(whenDone);
                    } else {
                        Socket2.this.state = State.INCORRECT_PASSWORD_FAILURE;
                        writer.write("PASSWORD_INCORRECT");
                        writer.flush();
                        socket.close();
                        Mundo.sync(whenDone);
                        return;
                    }
                }
                while ((line = reader.readLine()) != null) {
                    int colon = line.indexOf(':');
                    if (colon >= 0) {
                        onRead(line.substring(0, colon), line.substring(colon + 1));
                    }
                }
                state = State.CLOSED;
                onClose();
            } catch (IOException e) {
                Mundo.reportException(this, e);
            }
        }
    }

    private class Writer implements Runnable {
        final String message;

        Writer(String message) {
            this.message = message;
        }

        @Override
        public void run() {
            try {
                writer.newLine();
                writer.write(message);
                writer.flush();
            } catch (IOException e) {
                Mundo.reportException(this, e);
            }
        }
    }

    private class Closer implements Runnable {

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

    public void connect(String host, int port, int timeout, String password, Runnable whenDone) throws Exception {
        if (state != State.NEW) {
            throw new Exception("You can only connect a new socket!");
        }
        this.state = State.CONNECTING;
        Mundo.async(new Connector(whenDone, host, port, timeout, password));

    }

    public void use(Socket socket, String password, Runnable whenDone) throws Exception {
        if (state != State.NEW) {
            throw new Exception("You can only connect a new socket!");
        }
        if (!socket.isConnected()) {
            throw new IllegalArgumentException("The socket " + socket + " should have been already connected prior to use with the Socket2(Socket socket) constructor!");
        }
        this.socket = socket;
        Mundo.async(new Reader(password, whenDone));
    }

    public void write(String title, String message) {
        if (state == State.CONNECTED) {
            if (title.indexOf(':') == -1) {
                Mundo.async(new Writer(title + ":" + message));
            }
        }
    }

    public void close() throws Exception {
        if (state == State.CONNECTED) {
            state = State.CLOSED;
            Mundo.async(new Closer());
        }
    }

    //Notification

    public void setCloseHandler(Notification.Handler<CloseNotification> handler) {
        closeQuery.setHandler(handler);
    }

    public void setReadHandler(String title, Notification.Handler<ReadNotification> handler) {
        getReadQuery(title).setHandler(handler);
    }

    public static class CloseNotification extends Notification {}

    public static class ReadNotification extends Notification {
        public final String message;

        private ReadNotification(String message) {
            this.message = message;
        }
    }

    private void onClose() {
        Mundo.sync(new Runnable() {
            @Override
            public void run() {
                closeQuery.notify(new CloseNotification());
            }
        });
    }

    private void onRead(String title, String message) {
        Mundo.sync(new Runnable() {
            @Override
            public void run() {
                getReadQuery(title).notify(new ReadNotification(message));
            }
        });
    }

    private Notification.Query<ReadNotification> getReadQuery(String title) {
        if (!readQueries.containsKey(title)) {
            readQueries.put(title, new Notification.Query<ReadNotification>());
        }
        return readQueries.get(title);
    }

}
