package com.pie.tlatoani.CustomEvent;

import ch.njol.skript.lang.ExpressionType;
import com.pie.tlatoani.Mundo;

/**
 * Created by Tlatoani on 8/8/17.
 */
public class CustomEventMundo {
    
    public static void load() {
        Mundo.registerEffect(EffCallCustomEvent.class, "call custom event %string% [to] [det[ail]s %-objects%] [arg[ument]s %-objects%]");
        Mundo.registerEvent("Custom Event", EvtCustomEvent.class, UtilCustomEvent.class, "ev[en]t %strings%");
        Mundo.registerExpression(ExprIDOfCustomEvent.class,String.class, ExpressionType.PROPERTY,"id of custom event", "custom event's id");
        Mundo.registerExpression(ExprArgsOfCustomEvent.class,Object.class,ExpressionType.PROPERTY,"args of custom event", "custom event's args");
        Mundo.registerExpression(ExprLastCustomEventCancelled.class, Boolean.class, ExpressionType.SIMPLE, "last [called] custom event (0¦was|1¦wasn't) cancelled");
    }
}
