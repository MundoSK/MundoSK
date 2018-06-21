package com.pie.tlatoani.CodeBlock;

import ch.njol.skript.lang.ExpressionType;
import com.pie.tlatoani.Core.Registration.Registration;

/**
 * Created by Tlatoani on 8/8/17.
 */
public class CodeBlockMundo {
    
    public static void load() {
        Registration.registerType(CodeBlock.class, "codeblock")
                .document("Code Block", "1.7", "A block of code that can be run to perform certain effects.");
        Registration.registerScope(ScopeSaveCodeBlock.class, "codeblock %object% [with (1¦constant|2¦constant %-object%|3¦constants %-objects%)] [:: %-strings%] [-> %-string%]")
                .document("Code Block Declaration", "1.7", "Puts a codeblock with the code under the scope in the specified variable (the first specified expression). "
                        + "Codeblocks have three special features:"
                        , "Constants - constants are variables which are shared between different executions of the same codeblock. "
                        + "Contants are specified using either the second or third specified expression, and can be accessed from within the codeblock using {_constant} or {_constant::*}. "
                        + "This can be used to make player specific codeblocks, world specific codeblocks, etc. "
                        , "Arguments - the syntax for codeblocks allows you to specify argument names (the fourth specified expression). "
                        + "When you do something like 'execute codeblock {_temp} with 1, 2, 3' instead of setting the variables {_1}, {_2}, and {_3}, it will set the variables using the specified argument names. "
                        , "Return values - This allows you to have your codeblocks return values. Currently, returned values cannot be gotten in Skript, instead they are right now only used in Java. "
                        + "In the future you will be able to access return values.");
        Registration.registerEffect(EffRunCodeBlock.class, "((run|execute) codeblock|codeblock (run|execute)) %codeblocks% [(2¦with %-objects%|3¦with variables %-objects%|4¦in a chain|5¦here|7¦with variables %-objects% in a chain)]")
                .document("Run Code Block", "1.7", "Runs the code of the specified codeblocks. "
                        + "By default the codeblocks are run without setting any temp variables. By specifying 'here', you can run them as if they were a part of the code that executed it. "
                        + "By specifying 'variables', you can specify a list variable whose index-value pairs will be used to set temporary variables for the code to be run. "
                        + "Specifying 'in a chain' when multiple codeblocks are specified means that rather than run each codeblock independently, the local variables from each run carries on to the next codeblock. "
                        + "Note that you can't write 'in a chain' with 'here' as 'here' does what 'in a chain' would do by definition.");
        Registration.registerExpression(ExprFunctionCodeBlock.class, CodeBlock.class, ExpressionType.PROPERTY, "[codeblock of] function %string%")
                .document("Code Block of Function", "1.7", "An expression for the codeblock form of the specified function.");
    }
}
