package com.pie.tlatoani.Socket;

import ch.njol.skript.lang.ExpressionType;
import com.pie.tlatoani.Util.Registration;

/**
 * Created by Tlatoani on 8/8/17.
 */
public class SocketMundo {
    
    public static void load() {
        Registration.registerEffect(EffWriteToSocket.class, "write %strings% to socket with host %string% port %number% [with timeout %-timespan%] [to handle response through function %-string% with id %-string%]");
        Registration.registerEffect(EffOpenFunctionSocket.class, "open function socket at port %number% [with password %-string%] [through function %-string%]");
        Registration.registerEffect(EffCloseFunctionSocket.class, "close function socket at port %number%");
        Registration.registerExpression(ExprPassOfFunctionSocket.class,String.class, ExpressionType.PROPERTY,"pass[word] of function socket at port %number%");
        Registration.registerExpression(ExprHandlerOfFunctionSocket.class,String.class,ExpressionType.PROPERTY,"handler [function] of function socket at port %number%");
        Registration.registerExpression(ExprFunctionSocketIsOpen.class,Boolean.class,ExpressionType.PROPERTY,"function socket is open at port %number%");
        Registration.registerExpression(ExprServerSocketIsOpen.class,Boolean.class,ExpressionType.COMBINED,"server socket is open at host %string% port %number% [with timeout of %-timespan%]");
        Registration.registerExpression(ExprMotdOfServer.class,String.class,ExpressionType.COMBINED,"motd of server with host %string% [port %-number%]");
        Registration.registerExpression(ExprPlayerCountOfServer.class,Number.class,ExpressionType.COMBINED,"(1¦player count|0¦max player count) of server with host %string% [port %-number%]");
    }
}
