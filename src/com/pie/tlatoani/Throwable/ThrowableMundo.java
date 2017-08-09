package com.pie.tlatoani.Throwable;

import ch.njol.skript.lang.ExpressionType;
import com.pie.tlatoani.Mundo;

/**
 * Created by Tlatoani on 8/8/17.
 */
public class ThrowableMundo {
    
    public static void load() {
        Mundo.registerType(Throwable.class, "throwable");
        Mundo.registerType(StackTraceElement.class, "stacktraceelement");
        Mundo.registerScope(ScopeTry.class, "try");
        Mundo.registerScope(ScopeCatch.class, "catch in %object%");
        Mundo.registerEffect(EffPrintStackTrace.class, "print stack trace of %throwable%");
        Mundo.registerExpression(ExprCause.class,Throwable.class, ExpressionType.PROPERTY,"throwable cause of %throwable%", "%throwable%'s throwable cause");
        Mundo.registerExpression(ExprDetails.class,String.class,ExpressionType.PROPERTY,"details of %throwable%", "%throwable%'s details");
        Mundo.registerExpression(ExprStackTrace.class,StackTraceElement.class,ExpressionType.PROPERTY,"stack trace of %throwable%", "%throwable%'s stack trace");
        Mundo.registerExpression(ExprPropertyNameOfSTE.class,String.class,ExpressionType.PROPERTY,"(0¦class|1¦file|2¦method) name of %stacktraceelement%", "%stacktraceelement%'s (0¦class|1¦file|2¦method) name");
        Mundo.registerExpression(ExprLineNumberOfSTE.class,Integer.class,ExpressionType.PROPERTY,"line number of %stacktraceelement%", "%stacktraceelement%'s line number");
    }
}
