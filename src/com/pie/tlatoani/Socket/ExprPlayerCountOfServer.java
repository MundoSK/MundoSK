package com.pie.tlatoani.Socket;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Core.Static.Logging;
import org.bukkit.event.Event;

import javax.annotation.Nullable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ExprPlayerCountOfServer extends SimpleExpression<Number>{
	private Expression<String> host;
	private Expression<Number> port;
	private int mark;

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends Number> getReturnType() {
		return Number.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		host = (Expression<String>) exprs[0];
		port = (Expression<Number>) exprs[1];
		mark = parseResult.mark;
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "raw information of server";
	}

	@Override
	@Nullable
	protected Number[] get(Event event) {
		String host = this.host.getSingle(event);
		Integer port = (this.port != null ? this.port.getSingle(event) : 25565).intValue();
		Number playercount = 0;
		try {
			Socket sock = new Socket(host, port);
			DataOutputStream out = new DataOutputStream(sock.getOutputStream());
			DataInputStream in = new DataInputStream(sock.getInputStream());
			out.write(0xFE);
			boolean a = true;
			int b;
			in.read();
			in.read();
			in.read();
			in.read();
			List<Integer> listofint = new ArrayList<Integer>();
			while ((b = in.read()) != -1) {
				if (a) listofint.add(b);
				debug("b: " + b);
				a = !a;
			}
			int j = listofint.size();
			int l = 0;
			String rawcount = "";
			Boolean partofstring = mark == 0;
			while (l < 2) {
				j--;
				Integer k = listofint.get(j);
				debug("k: " + k);
				if (k.equals(167)) {
					debug("Found 167");
					l++;
					if (l == mark) {
						partofstring = true;
					} else {
						partofstring = false;
					}
					
				} else if (partofstring) {
					rawcount = ((char) k.intValue()) + rawcount;
				}
			}
			debug(rawcount);
			playercount = Integer.parseInt(rawcount);
			sock.close();
		} catch (Exception e) {
			Logging.reportException(this, e);
		}
		return new Number[]{playercount};
	}
	
	private static void debug(String msg) {
		Logging.debug(ExprPlayerCountOfServer.class, msg);
	}

}