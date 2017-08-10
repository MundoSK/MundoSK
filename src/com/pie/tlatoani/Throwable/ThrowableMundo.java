package com.pie.tlatoani.Throwable;

import ch.njol.skript.lang.ExpressionType;
import com.pie.tlatoani.Util.Registration;

/**
 * Created by Tlatoani on 8/8/17.
 */
public class ThrowableMundo {
    
    public static void load() {
        Registration.registerType(Throwable.class, "throwable");
        Registration.registerType(StackTraceElement.class, "stacktraceelement");
        Registration.registerScope(ScopeTry.class, "try");
        Registration.registerScope(ScopeCatch.class, "catch in %object%");
        Registration.registerEffect(EffPrintStackTrace.class, "print stack trace of %throwable%");
        Registration.registerExpression(ExprCause.class,Throwable.class, ExpressionType.PROPERTY,"throwable cause of %throwable%", "%throwable%'s throwable cause");
        Registration.registerExpression(ExprDetails.class,String.class,ExpressionType.PROPERTY,"details of %throwable%", "%throwable%'s details");
        Registration.registerExpression(ExprStackTrace.class,StackTraceElement.class,ExpressionType.PROPERTY,"stack trace of %throwable%", "%throwable%'s stack trace");
        Registration.registerExpression(ExprPropertyNameOfSTE.class,String.class,ExpressionType.PROPERTY,"(0¦class|1¦file|2¦method) name of %stacktraceelement%", "%stacktraceelement%'s (0¦class|1¦file|2¦method) name");
        Registration.registerExpression(ExprLineNumberOfSTE.class,Integer.class,ExpressionType.PROPERTY,"line number of %stacktraceelement%", "%stacktraceelement%'s line number");
    }
}
