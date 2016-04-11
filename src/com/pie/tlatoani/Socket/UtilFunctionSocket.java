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
	}
	
	private Boolean init() {
		status = true;
		try {
			sock = new ServerSocket(port);
			sock.setReuseAddress(true);
			Bukkit.getServer().getScheduler().runTaskAsynchronously(Bukkit.getServer().getPluginManager().getPlugin("MundoSK"), this);
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
				socket = sock.accept();
			} catch (IOException e) {
				if (status) Mundo.reportException(this, e);
			} finally {
				Bukkit.getServer().getScheduler().runTaskAsynchronously(Bukkit.getServer().getPluginManager().getPlugin("MundoSK"), this);
			}
			try {
				BufferedReader bread = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				List<String> list = new LinkedList<String>();
				String line;
				if (password == null || bread.readLine().equals(password)) {
					String funcmsg = null;
					if (handler != null) funcmsg = handler;
					else funcmsg = bread.readLine();
					while ((line = bread.readLine()) != null) {
						list.add(line);
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
					}
					if (result != null) {
						BufferedWriter bright = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
						for (int b = 0; b < result.length; b++) {
							Mundo.info("FunctionSocket: Line to be sent - " + result[b].toString());
							bright.write(result[b].toString());
							bright.newLine();
						}
						bright.flush();
						socket.close();
					}
					
				}
			} catch(Exception e) {} finally {
				try {
					socket.close();
				} catch(Exception e) {}
			}
			
		}
	}
	
	public static void openFunctionSocket(int portarg, String passarg, String handlerarg) {
		if (sockets.containsKey(portarg) == false) {
			UtilFunctionSocket socket = new UtilFunctionSocket(portarg, passarg, handlerarg);
			if (socket.init()) sockets.put(portarg, socket);
		}
	}
	
	public static void closeFunctionSocket(int portarg) {
		if (sockets.containsKey(portarg) == true) {
			sockets.get(portarg).closeFunctionSocket();
			sockets.remove(portarg);
		}
	}
	
	private void closeFunctionSocket() {
		status = false;
		try {
			sock.close();
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

}
