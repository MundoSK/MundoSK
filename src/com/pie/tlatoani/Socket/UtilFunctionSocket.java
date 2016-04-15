package com.pie.tlatoani.Socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;

import com.pie.tlatoani.Mundo;

import ch.njol.skript.lang.function.Functions;

public class UtilFunctionSocket implements Runnable {
	private Boolean status = false;
	private int port;
	private String password;
	private ServerSocket sock;
	private String handler;
	private static Map<Integer, UtilFunctionSocket> sockets = new HashMap<Integer, UtilFunctionSocket>();
	
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
			Bukkit.getServer().getScheduler().runTaskAsynchronously(Bukkit.getServer().getPluginManager().getPlugin("MundoSK"), this);
			debug("Function Socket on port " + port + " successfully initialized");
			return true;
		} catch (Exception e) {
			Mundo.reportException(this, e);
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
				if (status) Mundo.reportException(this, e);
			} finally {
				if (status) {
					Bukkit.getServer().getScheduler().runTaskAsynchronously(Bukkit.getServer().getPluginManager().getPlugin("MundoSK"), this);
					debug("At Function Socket on port " + port + ", running task to accept new connections");
				}
			}
			try {
				debug("At Function Socket on port " + port + ", about to read message");
				BufferedReader bread = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				List<String> list = new LinkedList<String>();
				if (password == null || bread.readLine().equals(password)) {
					String funcmsg = null;
					if (handler != null) funcmsg = handler;
					else funcmsg = bread.readLine();
					String line;
					while ((line = bread.readLine()) != null) {
						list.add(line);
						debug("At Function Socket on port " + port + ", line " + list.size() + "of an incoming message is " + line);
					}
					Object[][] args = new Object[2][1];
					args[0] = list.toArray();
					Object[] argsinfo = new Object[3];
					argsinfo[0] = new Integer(port);
					argsinfo[1] = socket.getInetAddress().getHostName();
					argsinfo[2] = new Integer(socket.getPort());
					args[1] = argsinfo;
					Object[] result = null;
					if (Functions.getFunction(funcmsg) != null) {
						result = Functions.getFunction(funcmsg).execute(args);
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
					
				}
			} catch(Exception e) {e.printStackTrace();} finally {
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
			e.printStackTrace();
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
		Mundo.classDebug(UtilFunctionSocket.class, msg);
	}

}
