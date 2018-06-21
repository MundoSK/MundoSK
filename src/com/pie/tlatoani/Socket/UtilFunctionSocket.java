package com.pie.tlatoani.Socket;

import ch.njol.skript.lang.function.Function;
import ch.njol.skript.lang.function.Functions;
import com.pie.tlatoani.Core.Static.Logging;
import com.pie.tlatoani.Core.Static.Scheduling;
import com.pie.tlatoani.Util.SyncGetter;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class UtilFunctionSocket implements Runnable {
	private Boolean status = false;
	private int port;
	private String password;
	private ServerSocket sock;
	private String handler;
	private static Map<Integer, UtilFunctionSocket> sockets = new HashMap<>();
	
	private UtilFunctionSocket(int portarg, String passarg, String handlerarg) {
		port = portarg;
		password = passarg;
		handler = handlerarg;
		debug("Function Socket on port " + port + " successfully created");
	}
	
	private Boolean init() {
		status = true;
		try {
			sock = new ServerSocket(port);
			sock.setReuseAddress(true);
			Scheduling.async(this);
			debug("Function Socket on port " + port + " successfully initialized");
			return true;
		} catch (Exception e) {
			Logging.reportException(this, e);
			return false;
		}
	}

	@Override
	public void run() {
		if (status) {
			Socket socket = null;
			try {
				debug("Waiting for connection on Function Socket on port " + port);
				socket = sock.accept();
				debug("New connection accepted on Function Socket on port " + port);
			} catch (IOException e) {
				if (status) Logging.reportException(this, e);
			} finally {
				if (status) {
					Scheduling.async(this);
					debug("At Function Socket on port " + port + ", running task to accept new connections");
				}
			}
			if (status) try {
				debug("At Function Socket on port " + port + ", about to read message");
				BufferedReader bread = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				List<String> list = new LinkedList<String>();
				Boolean continu = false;
				if (password == null) continu = true;
				else {
					String firstline = bread.readLine();
					continu = (firstline != null || password.equals(firstline));
				}
				if (continu) {
					String funcmsg = null;
					if (handler != null) funcmsg = handler;
					else funcmsg = bread.readLine();
					String line;
					while ((line = bread.readLine()) != null) {
						list.add(line);
						debug("At Function Socket on port " + port + ", line " + list.size() + " of an incoming message is " + line);
					}
					Object[][] args = new Object[2][1];
					args[0] = list.toArray();
					Object[] argsinfo = new Object[3];
					argsinfo[0] = new Integer(port);
					argsinfo[1] = socket.getInetAddress().getHostName();
					argsinfo[2] = new Integer(socket.getPort());
					args[1] = argsinfo;
					Object[] result = null;
					Function function = Functions.getFunction(funcmsg);
					if (function != null) {
						result = (new SyncGetter<Object[]>() {
							@Override
							protected Object[] getRaw() {
								return function.execute(args);
							}
						}).getSync();
						debug("At Function Socket on port " + port + ", the function " + funcmsg + "was successfully found");
					} else debug("At Function Socket on port " + port + ", the function " + funcmsg + "was not found");
					if (result != null) {
						debug("At Function Socket on port " + port + ", the function " + funcmsg + "returned a value");
						BufferedWriter bright = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
						for (int b = 0; b < result.length; b++) {
							bright.write(result[b].toString());
							bright.newLine();
							debug("At Function Socket on port " + port + ", line " + (b + 1) + "of an outgoing message is " + result[b].toString());
						}
						bright.flush();
						socket.close();
						debug("At Function Socket on port " + port + ", a connection was successfully closed");
					} else debug("At Function Socket on port " + port + ", the function " + funcmsg + "did not return a value or was not found");
				} else {
					debug("At Function Socket on port " + port + ", the password was incorrect, or the socket closed early.");
				}
			} catch(Exception e) {
				Logging.reportException(UtilFunctionSocket.class, e);
			} finally {
				try {
					socket.close();
				} catch(Exception e) {}
			}
			
		}
	}
	
	public static void openFunctionSocket(int portarg, String passarg, String handlerarg) {
		if (sockets.containsKey(portarg) == false) {
			debug("Function Socket on port " + portarg + " being created");
			UtilFunctionSocket socket = new UtilFunctionSocket(portarg, passarg, handlerarg);
			if (socket.init()) sockets.put(portarg, socket);
		}
	}
	
	public static void closeFunctionSocket(int portarg) {
		if (sockets.containsKey(portarg) == true) {
			debug("Function Socket on port " + portarg + " being closed");
			sockets.get(portarg).closeFunctionSocket();
			sockets.remove(portarg);
		}
	}
	
	private void closeFunctionSocket() {
		status = false;
		try {
			sock.close();
			debug("Function Socket on port " + port + " successfully closed");
		} catch (Exception e) {
			Logging.reportException(UtilFunctionSocket.class, e);
		} 
	}
	
	public static Boolean getStatusOfFunctionSocket(int portarg) {
		return sockets.containsKey(portarg);
	}
	
	public static String getPassOfFunctionSocket(int portarg) {
		if (sockets.containsKey(portarg) == true) return sockets.get(portarg).getPass();
		else return null;
	}
	
	private String getPass() {
		return password;
	}
	
	public static String getHandlerOfFunctionSocket(int portarg) {
		if (sockets.containsKey(portarg) == true) return sockets.get(portarg).getHandler();
		else return null;
	}
	
	private String getHandler() {
		return handler;
	}
	
	private static void debug(String msg) {
		Logging.debug(UtilFunctionSocket.class, msg);
	}

	public static void onDisable() {
		sockets.forEach((portarg, utilFunctionSocket) -> {
            debug("Function Socket on port " + portarg + " being closed (Special Case - onDisable)");
            sockets.get(portarg).closeFunctionSocket();
        });
	}

}
