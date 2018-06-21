package com.pie.tlatoani.Throwable;

import ch.njol.skript.lang.ExpressionType;
import com.pie.tlatoani.Core.Skript.MundoPropertyExpression;
import com.pie.tlatoani.Core.Registration.Registration;

/**
 * Created by Tlatoani on 8/8/17.
 */
public class ThrowableMundo {
    
    public static void load() {
        Registration.registerType(Throwable.class, "throwable")
                .document("Throwable", "1.5", "An exception or error.");
        Registration.registerType(StackTraceElement.class, "stacktraceelement")
                .document("Stack Trace Element", "1.5.1", "An element (line) of a stack trace. "
                        + "The stack trace is the huge blob of text you get in your console when an uncaught exception occurs on your server.");
        Registration.registerScope(ScopeTry.class, "try")
                .document("Try", "1.5", "A scope used for running code that may throw a throwable (error/exception). "
                        + "If a throwable is thrown, the rest of the code under try is not run. "
                        + "If there is a 'catch in %object%' scope following the 'try' scope, the code under the catch scope is run with the thrown throwable being stored in the specified variable. "
                        + "The rest of the code outside of the try and catch scopes proceeds as normal afterwards.")
                .example("try:"
                        , "\tbroadcast \"%page 3 of player's tool%\""
                        , "catch in {_e}:"
                        , "\tbroadcast \"An exception was caught\""
                        , "\tbroadcast \"Details: %details of {_e}%\"");
        Registration.registerScope(ScopeCatch.class, "catch in %object%")
                .document("Catch", "1.6", "A scope that is used following a 'try' scope to process a throwable (error/exception) if it is thrown. "
                        + "The thrown throwable is stored in the specified variable and then the code under the scope is run.");
        Registration.registerEffect(EffPrintStackTrace.class, "print stack trace of %throwable%")
                .document("Print Stack Trace", "1.5", "Prints the stack trace of the specified throwable (error/exception).");
        Registration.registerPropertyExpression(ExprCause.class, Throwable.class, "throwable", "throwable cause")
                .document("Cause of Throwable", "1.5", "An expression for the cause, if any, of the specified throwable (error/exception).");
        Registration.registerPropertyExpression(ExprDetails.class, String.class, "throwable", "details")
                .document("Details of Throwable", "1.5", "An expression for the details of the specified throwable (error/exception).");
        Registration.registerPropertyExpression(ExprPropertyNameOfSTE.class, String.class, "stacktraceelement", "class name", "file name", "method name")
                .document("Property Name of Stack Trace Element", "1.5.1", "An expression for the name of the specified stacktraceelement's class, file, or method.");
        Registration.registerPropertyExpression(ExprLineNumberOfSTE.class, Number.class, "stacktraceelement", "line number")
                .document("Line Number of Stack Trace Element", "1.5.1", "An expression for the line number of the line of code described by the specified stacktraceelement.");
        Registration.registerExpression(ExprStackTrace.class,StackTraceElement.class,ExpressionType.PROPERTY,"stack trace of %throwable%", "%throwable%'s stack trace")
                .document("Stack Trace of Throwable", "1.5.1", "An expression for the stack trace (as a list of stack trace elements) of the specified throwable (error/exception). "
                        + "The stack trace is the huge blob of text you get in your console when an uncaught exception occurs on your server.");
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
