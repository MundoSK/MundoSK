package com.pie.tlatoani.Util;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UtilCustomEvent extends Event{
	private static final HandlerList handlers = new HandlerList();
	private String id;
	private Object[] args;
	private Number num;
	private String str;
	private Boolean boo;
	
	public UtilCustomEvent(String id, Number num, String str, Boolean boo, Object[] args) {
		this.id = id;
		this.num = num;
		this.str = str;
		this.args = args;
		this.boo = boo;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	public String getID() {
		return id;
	}
	
	public Object[] getArgs() {
		return args;
	}
	
	public Number getNum() {
		return num;
	}
	
	public String getStr() {
		return str;
	}
	
	public Boolean getBoo() {
		return boo;
	}

}
