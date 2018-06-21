package com.pie.tlatoani.Socket;

import ch.njol.skript.lang.ExpressionType;
import com.pie.tlatoani.Core.Registration.Registration;

/**
 * Created by Tlatoani on 8/8/17.
 */
public class SocketMundo {
    
    public static void load() {
        Registration.registerEffect(EffWriteToSocket.class, "write %strings% to socket with host %string% port %number% [with timeout %-timespan%] [to handle response through function %-string% with id %-string%]")
                .document("Write to Socket", "1.4.5", "Sends a message through a socket. "
                        + "The host can either be the address or the IP. "
                        + "The timeout part is how many seconds to wait before stopping a socket write function if it doesn't make a connection or error out. "
                        + "The last part is if you want to handle a response through a handler function.");
        Registration.registerEffect(EffOpenFunctionSocket.class, "open function socket at port %number% [with password %-string%] [through function %-string%]")
                .document("Open Function Socket", "1.4.4 or earlier", "Opens a function socket at the port specified. "
                        + "A function socket receives messages and executes a function, messaging back the function's return value if it has one. "
                        + "The port has to be a port that isn't already in use (Ex. Don't use your Minecraft server's port). "
                        + "The password is an optional string used for security, "
                        + "and the function is the handler function that will handle the messages received through the function socket");
        Registration.registerEffect(EffCloseFunctionSocket.class, "close function socket at port %number%")
                .document("Close Function Socket", "1.4.4 or earlier", "Closes the function socket at the port specified. See the Open Function Socket Effect for more info.");
        Registration.registerExpression(ExprPassOfFunctionSocket.class,String.class, ExpressionType.PROPERTY,"pass[word] of function socket at port %number%")
                .document("Password of Function Socket", "1.4.4 or earlier", "An expression for the password of the function socket at that port. "
                        + "**For clarification, this only gets the password from function sockets on your server, not other servers.");
        Registration.registerExpression(ExprHandlerOfFunctionSocket.class,String.class,ExpressionType.PROPERTY,"handler [function] of function socket at port %number%")
                .document("Handler of Function Socket", "1.4.4 or earlier", "An expression for the handler function of the function socket.");
        Registration.registerExpression(ExprMotdOfServer.class,String.class,ExpressionType.COMBINED,"motd of server with host %string% [port %-number%]")
                .document("MOTD of Server", "1.5.10", "An expression foro the MOTD of the server at the specified host and port. "
                        + "Make sure to get this expression asynchronously (under 'async', for example), as otherwise your server may lag.");
        Registration.registerExpression(ExprPlayerCountOfServer.class,Number.class,ExpressionType.COMBINED,"(1¦player count|0¦max player count) of server with host %string% [port %-number%]")
                .document("Player Count of Server", "1.5.10", "An expression for the player count or maximum allowed player count of the server at the specified host and port. "
                        + "Make sure to get this expression asynchronously (under 'async', for example), as otherwise your server may lag.");
        Registration.registerExpressionCondition(CondFunctionSocketIsOpen.class,ExpressionType.PROPERTY,"function socket is open at port %number%")
                .document("FunctionSocket Is Open", "1.4.9", "Checks whether there is a function socket open at the specified port. ");
        Registration.registerExpressionCondition(CondServerSocketIsOpen.class,ExpressionType.COMBINED,"server socket is open at host %string% port %number% [with timeout of %-timespan%]")
                .document("Server Socket Is Open", "1.4.9", "Checks whether there is a server socket open at the specified host and port. Optionally, you can specify a timeout for how long to try checking before giving up.");
    }
}
