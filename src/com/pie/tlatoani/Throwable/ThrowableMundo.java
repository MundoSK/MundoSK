package com.pie.tlatoani.Throwable;

import ch.njol.skript.lang.ExpressionType;
import com.pie.tlatoani.Util.MundoPropertyExpression;
import com.pie.tlatoani.Registration.Registration;

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
        Registration.registerPropertyExpression(ExprCause.class, Throwable.class, "throwable", "throwable cause");
        Registration.registerPropertyExpression(ExprDetails.class, String.class, "throwable", "details");
        Registration.registerPropertyExpression(ExprPropertyNameOfSTE.class, String.class, "stacktraceelement", "class name", "file name", "method name");
        Registration.registerPropertyExpression(ExprLineNumberOfSTE.class, Number.class, "stacktraceelement", "line number");
        Registration.registerExpression(ExprStackTrace.class,StackTraceElement.class,ExpressionType.PROPERTY,"stack trace of %throwable%", "%throwable%'s stack trace");
    }

    public static class ExprCause extends MundoPropertyExpression<Throwable, Throwable> {
        @Override
        public Throwable convert(Throwable throwable) {
            return throwable.getCause();
        }
    }

    public static class ExprDetails extends MundoPropertyExpression<Throwable, String> {
        @Override
        public String convert(Throwable throwable) {
            return throwable.getMessage();
        }
    }

    public static class ExprLineNumberOfSTE extends MundoPropertyExpression<StackTraceElement, Number> {
        @Override
        public Number convert(StackTraceElement stackTraceElement) {
            return stackTraceElement.getLineNumber();
        }
    }

    public static class ExprPropertyNameOfSTE extends MundoPropertyExpression<StackTraceElement, String> {
        @Override
        public String convert(StackTraceElement stackTraceElement) {
            switch (property) {
                case "class name": return stackTraceElement.getClassName();
                case "file name": return stackTraceElement.getFileName();
                case "method name": return stackTraceElement.getMethodName();
            };
            throw new IllegalStateException("Invalid property: " + property);
        }
    }

}
