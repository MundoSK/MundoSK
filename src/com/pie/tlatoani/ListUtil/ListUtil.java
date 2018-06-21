package com.pie.tlatoani.ListUtil;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import com.pie.tlatoani.Core.Registration.DocumentationBuilder;
import com.pie.tlatoani.Core.Static.Logging;
import com.pie.tlatoani.Core.Static.MathUtil;
import com.pie.tlatoani.Core.Registration.Registration;
import com.pie.tlatoani.Core.Registration.ModifiableSyntaxElementInfo;

import java.util.*;

/**
 * Created by Tlatoani on 6/10/16.
 */
public final class ListUtil {
    public static final String TRANSFORMER_PATTERN_ID = "%listutil%";
    public static final String POSSESSOR_CLASS_CODE_NAME_ID = "%possessor%";

    private static final List<TransformerUserInfo> TRANSFORMER_USER_INFOS = new ArrayList<>();
    private static final List<TransformerInfo> transformerInfos = new ArrayList<>();

    public static void load() {
        Registration.registerEffect(EffMoveElements.class, "move %objects% (-1¦front|-1¦forward[s]|1¦back[ward[s]]) %number%")
                .document("Move Elements of List", "1.6.8", "Uses either the Element of List or Some Elements of List expression (both are ListUtil expressions) "
                        + "as the first specified expression, and moves them forward or backward the specified amount in their specified list.")
                .example("move pages 3 to 6 of player's tool back 4")
                .example("move last 5 pages of player's tool front 20");

        registerTransformer(TransDefault.class, Object.class,"objects", "elem", "element")
                .document("ListUtil General", "1.6.8", "ListUtil is a general set of expressions and effects used for manipulating lists. "
                        + "Each ListUtil effect/expression provides a certain functionality for lists in general, "
                        + "and contains '%listutil%' somewhere in its syntax - listutil isn't an actual type, but instead allows you to input "
                        + "a sort of \"specifier\" as to how you want to provide the list that is going to be manipulated. ListUtil effects/expressions "
                        + "also have an '%objects%' in their syntax from which the list is going to be gotten. "
                        + "The listutil specifier described by the above syntax is used for manipulating all lists, "
                        + "meaning you can write 'elem' where '%listutil%' is and input any list in '%objects%' to manipulate it. "
                        + "For example, using the Element of List expression, you could write 'elem 3 of {_list::*}' and that would be the third element of the list variable."
                        + "See the Book ListUtil expression as another example");

        registerTransformerUser(new ModifiableSyntaxElementInfo.Effect(EffInsertElements.class),
                "(add|insert) %objects% (1¦before|0¦after) (" + TRANSFORMER_PATTERN_ID + " %-number%|last " + TRANSFORMER_PATTERN_ID + ") (of|in) " + POSSESSOR_CLASS_CODE_NAME_ID)
                .document("Add Elements to List", "1.6.8", "A ListUtil effect (see the ListUtil expression for more info). "
                        + "Adds the specified elements before or after the specified index or the end of the specified list.");
        registerTransformerUser(new ModifiableSyntaxElementInfo.Expression(ExprElement.class, Object.class, ExpressionType.PROPERTY),
                "(" + TRANSFORMER_PATTERN_ID + " %-number%|last " + TRANSFORMER_PATTERN_ID + ") (of|in) " + POSSESSOR_CLASS_CODE_NAME_ID)
                .document("Element of List", "1.6.8", "A ListUtil expression (see the ListUtil expression for more info) "
                        + "for the element at the specified index or the last element in the specified list.");
        registerTransformerUser(new ModifiableSyntaxElementInfo.Expression(ExprElements.class, Object.class, ExpressionType.PROPERTY),
                TRANSFORMER_PATTERN_ID + "s (of|in) " + POSSESSOR_CLASS_CODE_NAME_ID)
                .document("All Elements of List", "1.6.8", "A ListUtil expression (see the ListUtil expression for more info) for all of the elements in the specified list.");
        registerTransformerUser(new ModifiableSyntaxElementInfo.Expression(ExprSomeElements.class, Object.class, ExpressionType.PROPERTY),
                TRANSFORMER_PATTERN_ID + "s %number% to (%-number%|last) (of|in) " + POSSESSOR_CLASS_CODE_NAME_ID)
                .document("Some Elements of List", "1.6.8", "A ListUtil expression (see the ListUtil expression for more info) "
                        + "for the sublist of the specified list from the first specified index to the second specified index or the end of the list.");
        registerTransformerUser(new ModifiableSyntaxElementInfo.Expression(ExprElementCount.class, Object.class, ExpressionType.PROPERTY),
                "(" + TRANSFORMER_PATTERN_ID + " count|amount of " + TRANSFORMER_PATTERN_ID + "s) (of|in) " + POSSESSOR_CLASS_CODE_NAME_ID)
                .document("Amount of Elements in List", "1.6.8", "A ListUtil expression (see the ListUtil expression for more info) for the size of the specified list.");
    }

    public static class TransformerInfo {
        public final Class<? extends Transformer> transformerClass;
        public final String possessorClassCodeName;
        public final String[] patterns;
        public final String unifiedPattern;

        public TransformerInfo(Class<? extends Transformer> transformerClass, String[] patterns, String possessorClassCodeName) {
            this.transformerClass = transformerClass;
            this.patterns = patterns;
            this.possessorClassCodeName = possessorClassCodeName;
            if (patterns.length == 1) {
                unifiedPattern = patterns[0];
            } else {
                unifiedPattern = "(" + String.join("|", patterns) + ")";
            }
        }
    }

    public static class TransformerUserInfo {
        public final ModifiableSyntaxElementInfo syntaxElementInfo;
        public final String prototypePattern;

        public TransformerUserInfo(ModifiableSyntaxElementInfo syntaxElementInfo, String prototypePattern) {
            this.syntaxElementInfo = syntaxElementInfo;
            this.prototypePattern = prototypePattern;
        }

        public String formatPrototypePattern(TransformerInfo transformerInfo) {
            return prototypePattern
                    .replace(TRANSFORMER_PATTERN_ID, transformerInfo.unifiedPattern)
                    .replace(POSSESSOR_CLASS_CODE_NAME_ID, "%" + transformerInfo.possessorClassCodeName + "%");
        }
    }

    public static String getApplicablePattern(int index) {
        if (!MathUtil.isInRange(0, index, transformerInfos.size() - 1)) {
            throw new IllegalArgumentException("The index " + index + " is out of range");
        }
        TransformerInfo transformerInfo = transformerInfos.get(index);
        return transformerInfo.patterns[0];
    }

    public static Transformer getTransformer(int index, Expression possessor) {
        if (!MathUtil.isInRange(0, index, transformerInfos.size() - 1)) {
            throw new IllegalArgumentException("The index " + index + " is out of range");
        }
        TransformerInfo transformerInfo = transformerInfos.get(index);
        try {
            Transformer transformer = transformerInfo.transformerClass.newInstance();
            return transformer.init(possessor) ? transformer : null;
        } catch (InstantiationException | IllegalAccessException e) {
            Logging.reportException(ListUtil.class, e);
            return null;
        }
    }

    public static <T> DocumentationBuilder.Expression registerTransformer(Class<? extends Transformer<T>> transformerClass, Class<T> type, String possessorClassCodeName, String... patterns) {
        if (patterns.length == 0) {
            throw new IllegalArgumentException("Every transformer must have at least one pattern!");
        }
        TransformerInfo transformerInfo = new TransformerInfo(transformerClass, patterns, possessorClassCodeName);
        transformerInfos.add(transformerInfo);
        for (TransformerUserInfo userInfo : TRANSFORMER_USER_INFOS) {
            String formattedWrapperPattern = userInfo.formatPrototypePattern(transformerInfo);
            userInfo.syntaxElementInfo.addPattern(formattedWrapperPattern);
        }
        return new DocumentationBuilder.Expression(Registration.getCurrentCategory(), patterns, type, null).requiredPlugins(Registration.getCurrentRequiredPlugins());
    }

    public static DocumentationBuilder registerTransformerUser(ModifiableSyntaxElementInfo syntaxElementInfo, String prototypePattern) {
        TransformerUserInfo userInfo = new TransformerUserInfo(syntaxElementInfo, prototypePattern);
        TRANSFORMER_USER_INFOS.add(userInfo);
        String[] patterns = new String[transformerInfos.size()];
        for (int i = 0; i < patterns.length; i++) {
            String formattedWrapperPattern = userInfo.formatPrototypePattern(transformerInfos.get(i));
            patterns[i] = formattedWrapperPattern;
        }
        userInfo.syntaxElementInfo.setPatterns(patterns);
        userInfo.syntaxElementInfo.register();
        if (syntaxElementInfo instanceof ModifiableSyntaxElementInfo.Effect) {
            return new DocumentationBuilder.Effect(Registration.getCurrentCategory(), new String[]{prototypePattern}).requiredPlugins(Registration.getCurrentRequiredPlugins());
        } else if (syntaxElementInfo instanceof ModifiableSyntaxElementInfo.Expression) {
            ModifiableSyntaxElementInfo.Expression<?, ?> exprInfo = (ModifiableSyntaxElementInfo.Expression) syntaxElementInfo;
            if (exprInfo.syntaxElementInfo.returnType == Boolean.class) {
                return new DocumentationBuilder.Condition(Registration.getCurrentCategory(), new String[]{prototypePattern}, null).requiredPlugins(Registration.getCurrentRequiredPlugins());
            } else {
                return new DocumentationBuilder.Expression(Registration.getCurrentCategory(), new String[]{prototypePattern}, exprInfo.syntaxElementInfo.returnType, null).requiredPlugins(Registration.getCurrentRequiredPlugins());
            }
        }
        return null;
    }

}
