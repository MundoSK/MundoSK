package com.pie.tlatoani.CodeBlock;

import ch.njol.skript.lang.ExpressionType;
import com.pie.tlatoani.Mundo;

/**
 * Created by Tlatoani on 8/8/17.
 */
public class CodeBlockMundo {
    
    public static void load() {
        Mundo.registerType(CodeBlock.class, "codeblock");
        Mundo.registerScope(ScopeSaveCodeBlock.class, "codeblock %object% [with (1¦constant|2¦constant %-object%|3¦constants %-objects%)] [:: %-strings%] [-> %-string%]");
        Mundo.registerEffect(EffRunCodeBlock.class, "((run|execute) codeblock|codeblock (run|execute)) %codeblocks% [(2¦with %-objects%|3¦with variables %-objects%|4¦in a chain|5¦here|7¦with variables %-objects% in a chain)]");
        Mundo.registerExpression(ExprFunctionCodeBlock.class, CodeBlock.class, ExpressionType.PROPERTY, "[codeblock of] function %string%");
    }
}
