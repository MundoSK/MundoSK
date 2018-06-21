package com.pie.tlatoani.Probability;

import ch.njol.skript.lang.ExpressionType;
import com.pie.tlatoani.Core.Registration.Registration;

/**
 * Created by Tlatoani on 8/8/17.
 */
public class ProbabilityMundo {
    
    public static void load() {
        Registration.registerScope(ScopeProbability.class, "prob[ability]", "random chance")
                .document("Random Chance", "1.5.3 or earlier", "The probability scope is used to perform different actions based on given probabilities. "
                        + "Under the scope, you write different probability values using the syntax '%number% prob[ability]'. "
                        + "These can be written as scopes or just simple lines of code. When the probability scope is run, all of these "
                        + "probabilities are added up and then, using their values, one is chosen at random and the code starts to proceed "
                        + "from there. First, if the chosen probability is a scope, its section of code is run. Then, the rest of the lines of code "
                        + "after the chosen probability are run. If another probability is encountered, it is ignored and the section of code under it "
                        + "(if it is a scope) is also ignored, but the lines of code that come after it are still run.")
                .example("command /probability:"
                        , "\ttrigger:"
                        , "\t\tprobability:"
                        , "\t\t\t1 prob"
                        , "\t\t\tbroadcast \"&aProb of 1\""
                        , "\t\t\tbroadcast \"&aThis happens for 1\""
                        , "\t\t\t2 prob:"
                        , "\t\t\t\tbroadcast \"&2Prob of 2\""
                        , "\t\t\tbroadcast \"&2This happens for 1 and 2\""
                        , "\t\t\t3 prob:"
                        , "\t\t\t\tbroadcast \"&3Prob of 3\""
                        , "\t\t\tbroadcast \"&3This happens for 1, 2, and 3\""
                        , "\t\t\tfalse"
                        , "\t\t\t4 prob"
                        , "\t\t\tbroadcast \"&cProb of 4\""
                        , "\t\t\tbroadcast \"&cThis happens for 4\""
                        , "\t\t\t5 prob:"
                        , "\t\t\t\tbroadcast \"&5Prob of 5\""
                        , "\t\t\tbroadcast \"&5This happens for 4 and 5\""
                        , "\t\tbroadcast \"&6Done!\"");
        Registration.registerCondition(CondProbabilityValue.class, "%number% prob[ability]");
        Registration.registerExpression(ExprRandomIndex.class,String.class, ExpressionType.PROPERTY,"random from %numbers% prob[abilitie]s")
                .document("Random Index from List Variable", "1.5.3 or earlier", "An expression that takes a list variable containing numbers, "
                        + "then adds all of the numbers up, assigns each index a probability based on its number value divided by the total sum, "
                        + "and finally chooses one index at random using the probabilities and returns it.")
                .example("set {_p::one} to 1"
                        , "set {_p::two} to 2"
                        , "set {_p::three} to 3"
                        , "set {_p::four} to 4"
                        , "set {_p::five} to 5"
                        , "set {_word} to random from {_p::*} probs"
                        , "#{_word} has a 1/15 chance of being \"one\", 2/15 chance of being \"two\", etc.");
        Registration.registerExpression(ExprRandomNumberIndex.class,Integer.class,ExpressionType.PROPERTY,"random number from %numbers% prob[abilitie]s")
                .document("Random Index from Numbers", "1.5.3 or earlier", "An expression that takes a list of numbers, "
                        + "then adds all of the numbers up, assigns each index (here, index means the position of a number in the list, ex. the first number has an index of 1) "
                        + "a probability based on its number value divided by the total sum, "
                        + "and finally chooses one index at random using the probabilities and returns it.")
                .example("set {_p::*} to 1, 2, 3, 4, 5"
                        , "set {_num} to random number from {_p::*} probs"
                        , "#{_num} has a 1/15 chance of being 1, 2/15 chance of being 2, etc.")
                .example("set {_p::*} to 1, 1, 2, 3, 5, 8"
                        , "set {_num} to random number from {_p::*} probs"
                        , "#{_num} has a 1/20 chance of being 1, 1/20 chance of being 2, 2/20 chance of being 3,"
                        , "#3/20 chance of being 4, 5/20 chance of being 5, and 8/20 chance of being 6");
    }
}
