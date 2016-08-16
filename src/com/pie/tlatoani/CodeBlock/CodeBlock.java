package com.pie.tlatoani.CodeBlock;

import ch.njol.skript.lang.TriggerItem;
import com.pie.tlatoani.Mundo;
import org.bukkit.event.Event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Tlatoani on 6/5/16.
 */
public interface CodeBlock {

    Object execute(Event event, boolean preserveOldValues);

    Object execute(Object[] args);
}
