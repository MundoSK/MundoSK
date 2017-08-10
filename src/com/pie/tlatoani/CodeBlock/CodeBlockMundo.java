package com.pie.tlatoani.CodeBlock;

import ch.njol.skript.lang.ExpressionType;
import com.pie.tlatoani.Util.Registration;

/**
 * Created by Tlatoani on 8/8/17.
 */
public class CodeBlockMundo {
    
    public static void load() {
        Registration.registerType(CodeBlock.class, "codeblock");
        Registration.registerScope(ScopeSaveCodeBlock.class, "codeblock %object% [with (1¦constant|2¦constant %-object%|3¦constants %-objects%)] [:: %-strings%] [-> %-string%]");
        Registration.registerEffect(EffRunCodeBlock.class, "((run|execute) codeblock|codeblock (run|execute)) %codeblocks% [(2¦with %-objects%|3¦with variables %-objects%|4¦in a chain|5¦here|7¦with variables %-objects% in a chain)]");
        Registration.registerExpression(ExprFunctionCodeBlock.class, CodeBlock.class, ExpressionType.PROPERTY, "[codeblock of] function %string%");
    }
}
