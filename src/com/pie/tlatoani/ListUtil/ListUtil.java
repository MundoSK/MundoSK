package com.pie.tlatoani.ListUtil;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import com.pie.tlatoani.Util.Logging;
import com.pie.tlatoani.Util.MathUtil;
import com.pie.tlatoani.Util.Registration;
import com.pie.tlatoani.Util.ModifiableSyntaxElementInfo;

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
        Registration.registerEffect(EffMoveElements.class, "move %objects% (-1¦front|-1¦forward[s]|1¦back[ward[s]]) %number%");

        registerTransformer(TransDefault.class, "objects", "elem", "element");

        registerTransformerUser(new ModifiableSyntaxElementInfo.Effect(EffInsertElements.class),
                "(add|insert) %objects% (1¦before|0¦after) (" + TRANSFORMER_PATTERN_ID + " %-number%|last " + TRANSFORMER_PATTERN_ID + ") (of|in) " + POSSESSOR_CLASS_CODE_NAME_ID);
        registerTransformerUser(new ModifiableSyntaxElementInfo.Expression(ExprElement.class, Object.class, ExpressionType.PROPERTY),
                "(" + TRANSFORMER_PATTERN_ID + " %-number%|last " + TRANSFORMER_PATTERN_ID + ") (of|in) " + POSSESSOR_CLASS_CODE_NAME_ID);
        registerTransformerUser(new ModifiableSyntaxElementInfo.Expression(ExprElements.class, Object.class, ExpressionType.PROPERTY),
                TRANSFORMER_PATTERN_ID + "s (of|in) " + POSSESSOR_CLASS_CODE_NAME_ID);
        registerTransformerUser(new ModifiableSyntaxElementInfo.Expression(ExprSomeElements.class, Object.class, ExpressionType.PROPERTY),
                TRANSFORMER_PATTERN_ID + "s %number% to (%-number%|last) (of|in) " + POSSESSOR_CLASS_CODE_NAME_ID);
        registerTransformerUser(new ModifiableSyntaxElementInfo.Expression(ExprElementCount.class, Object.class, ExpressionType.PROPERTY),
                "(" + TRANSFORMER_PATTERN_ID + " count|amount of " + TRANSFORMER_PATTERN_ID + "s) (of|in) " + POSSESSOR_CLASS_CODE_NAME_ID);
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

    public static void registerTransformer(Class<? extends Transformer> transformerClass, String possessorClassCodeName, String... patterns) {
        if (patterns.length == 0) {
            throw new IllegalArgumentException("Every transformer must have at least one pattern!");
        }
        TransformerInfo transformerInfo = new TransformerInfo(transformerClass, patterns, possessorClassCodeName);
        transformerInfos.add(transformerInfo);
        for (TransformerUserInfo userInfo : TRANSFORMER_USER_INFOS) {
            String formattedWrapperPattern = userInfo.formatPrototypePattern(transformerInfo);
            userInfo.syntaxElementInfo.addPattern(formattedWrapperPattern);
        }
    }

    public static void registerTransformerUser(ModifiableSyntaxElementInfo syntaxElementInfo, String prototypePattern) {
        TransformerUserInfo userInfo = new TransformerUserInfo(syntaxElementInfo, prototypePattern);
        TRANSFORMER_USER_INFOS.add(userInfo);
        String[] patterns = new String[transformerInfos.size()];
        for (int i = 0; i < patterns.length; i++) {
            String formattedWrapperPattern = userInfo.formatPrototypePattern(transformerInfos.get(i));
            patterns[i] = formattedWrapperPattern;
        }
        userInfo.syntaxElementInfo.setPatterns(patterns);
        userInfo.syntaxElementInfo.register();
    }

}
