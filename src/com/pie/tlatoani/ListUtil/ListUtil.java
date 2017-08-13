package com.pie.tlatoani.ListUtil;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import com.pie.tlatoani.Util.Logging;
import com.pie.tlatoani.Util.MathUtil;
import com.pie.tlatoani.Util.Registration;
import com.pie.tlatoani.ZExperimental.ModifiableSyntaxElementInfo;
import org.bukkit.event.Event;

import java.util.*;

/**
 * Created by Tlatoani on 6/10/16.
 */
public final class ListUtil {
    private final static Map<String, Class<? extends Transformer>> dictionary = new HashMap<String, Class<? extends Transformer>>();
    private final static ArrayList<String> patternlist = new ArrayList<>();
    private final static ArrayList<String> possessorList = new ArrayList<>();

    //Cannot be instantiated
    private ListUtil() {}

    //public static void registerTransformer(Class<? extends Transformer> transformer, String... patterns) {
     //   registerTransformer("objects", transformer, patterns);
    //}

    public static void registerTransformer(String possessorClassInfo, Class<? extends Transformer> transformer, String... patterns) {
        if (possessorClassInfo == null)
            possessorClassInfo = "objects";
        for (int i = 0; i < patterns.length; i++) {
            if (patterns[i] != null) {
                dictionary.putIfAbsent(patterns[i], transformer);
                patternlist.add(patterns[i]);
                possessorList.add(possessorClassInfo);
            }
        }
    }

    public static String retrievePattern(int index) {
        return patternlist.size() > index ? patternlist.get(index) : null;
    }

    public static Transformer retrieveTransformer(String pattern, Expression expression) {
        Transformer transformer = null;
        if (dictionary.containsKey(pattern)) {
            try {
                transformer = dictionary.get(pattern).newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
                return transformer = new TransDefault();
            }
        } else {
            transformer = new TransDefault();
        }
        return transformer.init(expression) ? transformer : null;
    }

    //Returns the transformer options, ex. "0¦(elem|element|item)|1¦page"
    public static String getTransformerOptions() {
        String[] withIndexes = new String[patternlist.size()];
        for (int i = 0; i < patternlist.size(); i++) {
            withIndexes[i] = i + "¦" + patternlist.get(i);
        }
        return String.join("|", withIndexes);
    }

    public static void register() {
        List<String> insertItem = new ArrayList<>();
        List<String> item = new ArrayList<>();
        List<String> items = new ArrayList<>();
        List<String> someItems = new ArrayList<>();
        List<String> itemCount = new ArrayList<>();
        for (int i = 0; i < patternlist.size(); i++) {
            String j = patternlist.get(i);
            String possessorClassInfo = possessorList.get(i);
            insertItem.add("(add|insert) %objects% (1¦before|0¦after) (" + j + " %-number%|last " + j + ") (of|in) %" + possessorClassInfo + "%");
            item.add("(" + j + " %-number%|last " + j + ") (of|in) %" + possessorClassInfo + "%");
            items.add(j + "s (of|in) %" + possessorClassInfo + "%");
            someItems.add(j + "s %number% to (%-number%|last) (of|in) %" + possessorClassInfo + "%");
            itemCount.add(j + " count (of|in) %" + possessorClassInfo + "%");
        }
        String j = "(element|elem|item)";
        String possessorClassInfo = "objects";
        insertItem.add("(add|insert) %objects% (1¦before|0¦after) (" + j + " %-number%|last " + j + ") (of|in) %" + possessorClassInfo + "%");
        item.add("(" + j + " %-number%|last " + j + ") (of|in) %" + possessorClassInfo + "%");
        someItems.add(j + "s %number% to (%-number%|last) (of|in) %" + possessorClassInfo + "%");
        itemCount.add(j + " count (of|in) %" + possessorClassInfo + "%");
        Registration.registerEffect(EffInsertElements.class,insertItem.toArray(new String[0]));
        Registration.registerExpression(ExprElement.class,Object.class, ExpressionType.PROPERTY,item.toArray(new String[0]));
        Registration.registerExpression(ExprElements.class,Object.class,ExpressionType.PROPERTY,items.toArray(new String[0]));
        Registration.registerExpression(ExprSomeElements.class,Object.class,ExpressionType.PROPERTY,someItems.toArray(new String[0]));
        Registration.registerExpression(ExprElementCount.class,Number.class,ExpressionType.PROPERTY,itemCount.toArray(new String[0]));

        Registration.registerEffect(EffMoveElements.class, "move %objects% (-1¦front|-1¦forward[s]|1¦back[ward[s]]) %number%");
    }

    public interface TransformerUser {

        Transformer getTransformer();
    }

    public interface Moveable {

        void move(Event event, Integer movement);

        Boolean isMoveable();
    }

    //ListUtil slight rewrite

    public static final String TRANSFORMER_PATTERN_ID = "%listutil%";
    public static final String POSSESSOR_CLASS_CODE_NAME_ID = "%possessor%";

    private static final List<TransformerWrapperInfo> transformerWrapperInfos = new ArrayList<>();
    private static final List<TransformerInfo> transformerInfos = new ArrayList<>();

    public static void load() {
        Registration.registerEffect(EffMoveElements.class, "move %objects% (-1¦front|-1¦forward[s]|1¦back[ward[s]]) %number%");

        registerTransformer(TransDefault.class, "objects", "elem", "element");

        registerTransformerWrapper(new ModifiableSyntaxElementInfo.Effect(EffInsertElements.class),
                "(add|insert) %objects% (1¦before|0¦after) (" + TRANSFORMER_PATTERN_ID + " %-number%|last " + TRANSFORMER_PATTERN_ID + ") (of|in) " + POSSESSOR_CLASS_CODE_NAME_ID);
        registerTransformerWrapper(new ModifiableSyntaxElementInfo.Expression(ExprElement.class, Object.class, ExpressionType.PROPERTY),
                "(" + TRANSFORMER_PATTERN_ID + " %-number%|last " + TRANSFORMER_PATTERN_ID + ") (of|in) " + POSSESSOR_CLASS_CODE_NAME_ID);
        registerTransformerWrapper(new ModifiableSyntaxElementInfo.Expression(ExprElements.class, Object.class, ExpressionType.PROPERTY),
                TRANSFORMER_PATTERN_ID + "s (of|in) " + POSSESSOR_CLASS_CODE_NAME_ID);
        registerTransformerWrapper(new ModifiableSyntaxElementInfo.Expression(ExprSomeElements.class, Object.class, ExpressionType.PROPERTY),
                TRANSFORMER_PATTERN_ID + "s %number% to (%-number%|last) (of|in) " + POSSESSOR_CLASS_CODE_NAME_ID);
        registerTransformerWrapper(new ModifiableSyntaxElementInfo.Expression(ExprElementCount.class, Object.class, ExpressionType.PROPERTY),
                "(" + TRANSFORMER_PATTERN_ID + " count|amount of " + TRANSFORMER_PATTERN_ID + "s) (of|in) " + POSSESSOR_CLASS_CODE_NAME_ID);
    }

    public static class TransformerInfo {
        public final Class<? extends Transformer> transformerClass;
        public final String possessorClassCodeName;
        public final String[] patterns;

        public TransformerInfo(Class<? extends Transformer> transformerClass, String[] patterns, String possessorClassCodeName) {
            this.transformerClass = transformerClass;
            this.patterns = patterns;
            this.possessorClassCodeName = possessorClassCodeName;
        }
    }

    public static class TransformerWrapperInfo {
        public final ModifiableSyntaxElementInfo syntaxElementInfo;
        public final String prototypePattern;

        public TransformerWrapperInfo(ModifiableSyntaxElementInfo syntaxElementInfo, String prototypePattern) {
            this.syntaxElementInfo = syntaxElementInfo;
            this.prototypePattern = prototypePattern;
        }

        public String formatPrototypePattern(TransformerInfo transformerInfo) {
            String unifiedTransformerPattern;
            if (transformerInfo.patterns.length == 1) {
                unifiedTransformerPattern = transformerInfo.patterns[0];
            } else {
                unifiedTransformerPattern = "(" + String.join("|", transformerInfo.patterns) + ")";
            }
            return prototypePattern
                    .replace(TRANSFORMER_PATTERN_ID, unifiedTransformerPattern)
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
        for (TransformerWrapperInfo wrapperInfo : transformerWrapperInfos) {
            String formattedWrapperPattern = wrapperInfo.formatPrototypePattern(transformerInfo);
            wrapperInfo.syntaxElementInfo.addPattern(formattedWrapperPattern);
        }
    }

    public static void registerTransformerWrapper(ModifiableSyntaxElementInfo syntaxElementInfo, String prototypePattern) {
        TransformerWrapperInfo wrapperInfo = new TransformerWrapperInfo(syntaxElementInfo, prototypePattern);
        transformerWrapperInfos.add(wrapperInfo);
        String[] patterns = new String[transformerInfos.size()];
        for (int i = 0; i < patterns.length; i++) {
            String formattedWrapperPattern = wrapperInfo.formatPrototypePattern(transformerInfos.get(i));
            patterns[i] = formattedWrapperPattern;
        }
        wrapperInfo.syntaxElementInfo.setPatterns(patterns);
        wrapperInfo.syntaxElementInfo.register();
    }

}
