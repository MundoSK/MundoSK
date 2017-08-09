package com.pie.tlatoani.Socket;

import ch.njol.skript.lang.ExpressionType;
import com.pie.tlatoani.Mundo;

/**
 * Created by Tlatoani on 8/8/17.
 */
public class SocketMundo {
    
    public static void load() {
        Mundo.registerEffect(EffWriteToSocket.class, "write %strings% to socket with host %string% port %number% [with timeout %-timespan%] [to handle response through function %-string% with id %-string%]");
        Mundo.registerEffect(EffOpenFunctionSocket.class, "open function socket at port %number% [with password %-string%] [through function %-string%]");
        Mundo.registerEffect(EffCloseFunctionSocket.class, "close function socket at port %number%");
        Mundo.registerExpression(ExprPassOfFunctionSocket.class,String.class, ExpressionType.PROPERTY,"pass[word] of function socket at port %number%");
        Mundo.registerExpression(ExprHandlerOfFunctionSocket.class,String.class,ExpressionType.PROPERTY,"handler [function] of function socket at port %number%");
        Mundo.registerExpression(ExprFunctionSocketIsOpen.class,Boolean.class,ExpressionType.PROPERTY,"function socket is open at port %number%");
        Mundo.registerExpression(ExprServerSocketIsOpen.class,Boolean.class,ExpressionType.COMBINED,"server socket is open at host %string% port %number% [with timeout of %-timespan%]");
        Mundo.registerExpression(ExprMotdOfServer.class,String.class,ExpressionType.COMBINED,"motd of server with host %string% [port %-number%]");
        Mundo.registerExpression(ExprPlayerCountOfServer.class,Number.class,ExpressionType.COMBINED,"(1¦player count|0¦max player count) of server with host %string% [port %-number%]");
    }
}
