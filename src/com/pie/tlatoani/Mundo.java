package com.pie.tlatoani;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.StreamCorruptedException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.classes.Serializer;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import ch.njol.skript.lang.*;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Checker;
import ch.njol.util.Kleenean;
import ch.njol.yggdrasil.Fields;

import com.pie.tlatoani.Achievement.AchievementMundo;
import com.pie.tlatoani.Book.*;
import com.pie.tlatoani.Chunk.*;
import com.pie.tlatoani.CodeBlock.*;
import com.pie.tlatoani.CustomEvent.*;
import com.pie.tlatoani.EnchantedBook.*;
import com.pie.tlatoani.Generator.*;
import com.pie.tlatoani.Json.*;
import com.pie.tlatoani.ListUtil.*;
import com.pie.tlatoani.Miscellaneous.*;
import com.pie.tlatoani.NoteBlock.*;
import com.pie.tlatoani.Probability.*;
import com.pie.tlatoani.ProtocolLib.*;
import com.pie.tlatoani.Skin.*;
import com.pie.tlatoani.Socket.*;
import com.pie.tlatoani.Tablist.*;
import com.pie.tlatoani.TerrainControl.*;
import com.pie.tlatoani.Throwable.*;
import com.pie.tlatoani.Util.*;
import com.pie.tlatoani.WebSocket.*;
import com.pie.tlatoani.WorldBorder.*;
import com.pie.tlatoani.WorldCreator.*;
import com.pie.tlatoani.WorldManagement.*;
import com.pie.tlatoani.WorldManagement.WorldLoader.*;
import com.pie.tlatoani.Metrics.*;
import com.pie.tlatoani.ZExperimental.ZExperimentalMundo;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class Mundo extends JavaPlugin {
	public static Mundo instance;
    public static String pluginFolder;
    public static List<String> debugPackages;
    //public static Boolean debugMode;
    public static Boolean implementPacketStuff;
    public static int tablistRemoveTabDelaySpawn;
    public static int tablistRemoveTabDelayRespawn;
    public static String hexDigits = "0123456789abcdef";
    public static BukkitScheduler scheduler;

    public static ArrayList<Object[]> ena = new ArrayList<>();
    public static ArrayList<String> enumNames = new ArrayList<>();
    public static ArrayList<Class<?>> enumClasses = new ArrayList<>();

    @Override
	public void onEnable() {
        pluginFolder = getDataFolder().getAbsolutePath();
        FileConfiguration config = getConfig();
        config.addDefault("debug", Arrays.asList(new String[0]));
        //config.addDefault("debug_mode", false);
        config.addDefault("enable_custom_skin_and_tablist", true);
        config.addDefault("tablist_remove_tab_delay_spawn", 5);
        config.addDefault("tablist_remove_tab_delay_respawn", 5);
        config.options().copyDefaults(true);
        debugPackages = config.getStringList("debug");
        //debugMode = config.getBoolean("debug_mode");
        implementPacketStuff = config.getBoolean("enable_custom_skin_and_tablist");
        tablistRemoveTabDelaySpawn = config.getInt("tablist_remove_tab_delay_spawn");
        tablistRemoveTabDelayRespawn = config.getInt("tablist_remove_tab_delay_respawn");
        saveConfig();
        instance = this;
        UtilWorldLoader.load();
		Skript.registerAddon(this);
        scheduler = Bukkit.getScheduler();
        info("Pie is awesome :D");
        if (getDescription().getVersion().toUpperCase().contains("BETA")) {
            info("You are currently running a BETA version of MundoSK");
            info("You should only run BETA versions of MundoSK on test servers unless Tlatoani or another reliable source has recommended otherwise");
        }
        if (!debugPackages.isEmpty()) {
            info("You have enabled debug for certain packages in MundoSK config");
            info("Debug should only be enabled when you are trying to fix a bug or assist someone else with fixing a bug in MundoSK");
            info("By having debug enabled, you will have tons of random annoying spam in your console");
            info("If you would like to disable debug, simply go to your 'plugins' folder, go to the 'MundoSK' folder, open 'config.yml', and where it says 'debug', remove all following text");
        }

        //Allow MundoSK 'conditions' to work in absence of SkQuery, which provides a condition like the below
        if (!serverHasPlugin("SkQuery")) {
            registerCondition(CondBoolean.class, "%boolean%");
        }
		BookMundo.load();
        ChunkMundo.load();
        CodeBlockMundo.load();
        CustomEventMundo.load();
        EnchantedBookMundo.load();
		GeneratorMundo.load();
        JSONMundo.load();
        MiscMundo.load();
        NoteBlockMundo.load();
        ProbabilityMundo.load();
        SocketMundo.load();
        ThrowableMundo.load();
        WebSocketManager.load();
        WorldBorderMundo.load();
        WorldCreatorMundo.load();
        WorldManagementMundo.load();
		if (serverHasPlugin("ProtocolLib")) {
		    PacketManager.load();
		    if (implementPacketStuff) {
		        SkinMundo.load();
		        TablistMundo.load();
            }
		}
		if (serverHasPlugin("TerrainControl")) {
		    TerrainControlMundo.load();
		}
        if (Bukkit.getVersion().contains("1.8") || Bukkit.getVersion().contains("1.9") || Bukkit.getVersion().contains("1.10") || Bukkit.getVersion().contains("1.11")) {
            AchievementMundo.load();
        }
        //ZExperimental ~ The Z is for mystery (it's so that it appears last in the package list)
        ZExperimentalMundo.load();
        //
        ArrayList<String> patterns = new ArrayList<>();
        for (String s : enumNames) {
            patterns.add("[all] " + s + "s");
        }
        Skript.registerExpression(ExprEnumValues.class, Object.class, ExpressionType.SIMPLE, patterns.toArray(new String[0]));
		try {
			Field classinfos = Classes.class.getDeclaredField("tempClassInfos");
			classinfos.setAccessible(true);
			@SuppressWarnings("unchecked")
			List<ClassInfo<?>> classes = (List<ClassInfo<?>>) classinfos.get(null);
			for (int i = 0; i < classes.size(); i++)
				registerCustomEventValue(classes.get(i));
		} catch (Exception e1) {
			reportException(this, e1);
		}
        ListUtil.register();
        ExprEventSpecificValue.register();
		info("Awesome syntaxes have been registered!");
        sync(Mundo::enableMetrics);
	}

    @Override
    public void onDisable() {
        UtilFunctionSocket.onDisable();
        info("Closed all function sockets (if any were open)");
        WebSocketManager.stopAllServers(0);
        info("Stopped all WebSocket servers (if any were open)");
        try {
            UtilWorldLoader.save();
            info("Successfully saved all (if any) world loaders");
        } catch (IOException e) {
            info("A problem occurred while saving world loaders");
            reportException(this, e);
        }
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String unusedWorldName, String id) {
        return SkriptGeneratorManager.getSkriptGenerator(id);
    }
    
    //Registration
    
    public static void registerEffect(Class<? extends Effect> effectClass, String... patterns) {
        Skript.registerEffect(effectClass, patterns);
    }
    
    public static <T> void registerExpression(Class<? extends Expression<T>> expressionClass, Class<T> type, ExpressionType expressionType, String... patterns) {
        Skript.registerExpression(expressionClass, type, expressionType, patterns);
    }

    public static void registerCondition(Class<? extends Condition> conditionClass, String... patterns) {
        Skript.registerCondition(conditionClass, patterns);
    }
    
    public static void registerEvent(String name, Class<? extends SkriptEvent> eventClass, Class<? extends Event> eventType, String... patterns) {
        Skript.registerEvent(name, eventClass, eventType, patterns);
    }

    public static void registerScope(Class<? extends CustomScope> conditionClass, String... patterns) {
        Skript.registerCondition(conditionClass, patterns);
    }

    public static <E extends Event, R> void registerEventValue(Class<E> tClass, Class<R> rClass, Function<E, R> function) {
	    EventValues.registerEventValue(tClass, rClass, new Getter<R, E>() {
            @Override
            public R get(E event) {
                return function.apply(event);
            }
        }, 0);
    }

    public static <T> ClassInfo<T> registerType(Class<T> type, String name, String... alternateNames) {
        ArrayList<String> names = new ArrayList<String>(Arrays.asList(alternateNames));
        names.add(0, name);
        ClassInfo<T> result = new ClassInfo<T>(type, name).user(names.toArray(new String[0])).name(name).parser(new SimpleParser<T>() {
            @Override
            public T parse(String s, ParseContext parseContext) {
                return null;
            }
        });
        if (classInfoSafe(type, name)) {
            Classes.registerClass(result);
        }
        return result;
    }

    //Default pairing string names should be in uppercase
    public static <E> void registerEnum(Class<E> enumClass, String name, E[] values, Map.Entry<String, E>... defaultPairings) {
        if (!classInfoSafe(enumClass, name)) return;
        Classes.registerClass(new ClassInfo<E>(enumClass, name).user(new String[]{name}).name(name).parser(new Parser<E>() {
            private E[] enumValues = values;
            private Map.Entry<String, E>[] additionalPairings = defaultPairings;

            @Override
            public E parse(String s, ParseContext parseContext) {
                String upperCase = s.toUpperCase();
                for (int i = 0; i < additionalPairings.length; i++) {
                    if (additionalPairings[i].getKey().equals(upperCase)) {
                        return additionalPairings[i].getValue();
                    }
                }
                for (int i = 0; i < values.length; i++) {
                    if (values[i].toString().equals(upperCase)) {
                        return values[i];
                    }
                }
                return null;
            }

            @Override
            public String toString(E e, int useless) {
                for (int i = 0; i < additionalPairings.length; i++) {
                    if (additionalPairings[i].getValue() == e) {
                        return additionalPairings[i].getKey().toLowerCase();
                    }
                }
                for (int i = 0; i < values.length; i++) {
                    if (values[i] == e) {
                        return values[i].toString().toLowerCase();
                    }
                }
                return null;
            }

            @Override
            public String toVariableNameString(E e) {
                return toString(e, 0);
            }

            @Override
            public String getVariableNamePattern() {
                return ".+";
            }
        }).serializer(new Serializer<E>() {
            private E[] enumValues = values;
            private Map.Entry<String, E>[] additionalPairings = defaultPairings;

            public E parse(String s) {
                String upperCase = s.toUpperCase();
                for (int i = 0; i < additionalPairings.length; i++) {
                    if (additionalPairings[i].getKey().equals(upperCase)) {
                        return additionalPairings[i].getValue();
                    }
                }
                for (int i = 0; i < values.length; i++) {
                    if (values[i].toString().equals(upperCase)) {
                        return values[i];
                    }
                }
                return null;
            }

            public String toString(E e) {
                for (int i = 0; i < additionalPairings.length; i++) {
                    if (additionalPairings[i].getValue() == e) {
                        return additionalPairings[i].getKey().toLowerCase();
                    }
                }
                for (int i = 0; i < values.length; i++) {
                    if (values[i] == e) {
                        return values[i].toString().toLowerCase();
                    }
                }
                return null;
            }

            @Override
            public Fields serialize(E e) throws NotSerializableException {
                Fields fields = new Fields();
                fields.putObject("value", toString(e));
                return null;
            }

            @Override
            public void deserialize(E e, Fields fields) throws StreamCorruptedException, NotSerializableException {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean mustSyncDeserialization() {
                return false;
            }

            @Override
            protected boolean canBeInstantiated() {
                return false;
            }

            @Override
            public E deserialize(Fields fields) throws StreamCorruptedException {
                return parse((String) fields.getObject("value"));
            }
        }));
        Set<E> allValues = new HashSet<E>();
        allValues.addAll(Arrays.asList(values));
        for (Map.Entry<String, E> entry : defaultPairings) {
            allValues.add(entry.getValue());
        }
        ena.add(allValues.toArray(new Object[0]));
        enumNames.add(name);
        enumClasses.add(enumClass);
    }

    public static class ExprEnumValues extends SimpleExpression<Object> {
        private int whichEnum;

        @Override
        protected Object[] get(Event event) {
            return Mundo.ena.get(whichEnum);
        }

        @Override
        public boolean isSingle() {
            return false;
        }

        @Override
        public Class<? extends Object> getReturnType() {
            return enumClasses.get(whichEnum);
        }

        @Override
        public String toString(Event event, boolean b) {
            return "all " + enumNames.get(whichEnum) + "s";
        }

        @Override
        public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
            whichEnum = i;
            return true;
        }
    }

    public static abstract class SimpleParser<T> extends Parser<T> {

        @Override
        public String toString(T t, int flags) {
            return t.toString();
        }

        @Override
        public String toVariableNameString(T t) {
            return toString(t, 0);
        }

        @Override
        public String getVariableNamePattern() {
            return ".+";
        }
    }

    //Metrics Util

    public static void enableMetrics() {
        try {
            Metrics metrics = new Metrics(instance);
            //Skript Version
            Graph skriptVersion = metrics.createGraph("Skript Version");
            skriptVersion.addPlotter(new Metrics.Plotter(Bukkit.getServer().getPluginManager().getPlugin("Skript").getDescription().getVersion()){
                @Override
                public int getValue() {
                    return 1;
                }
            });

            Graph addons = metrics.createGraph("Skript Addons");
            SkriptAddon[] addonlist = Skript.getAddons().toArray(new SkriptAddon[0]);
            for (int i = 0; i < addonlist.length; i++) {
                addons.addPlotter(new Metrics.Plotter((addonlist[i]).getName()) {

                    @Override
                    public int getValue() {
                        return 1;
                    }
                });
            }

            Graph plugins = metrics.createGraph("Plugins");
            Plugin[] pluginlist = Bukkit.getPluginManager().getPlugins();
            for (int i = 0; i < pluginlist.length; i++) {
                plugins.addPlotter(new Metrics.Plotter((pluginlist[i]).getName()) {

                    @Override
                    public int getValue() {
                        return 1;
                    }
                });
            }

            if (serverHasPlugin("ProtocolLib")) {
                Graph protocolLibVersion = metrics.createGraph("ProtocolLib Version");
                protocolLibVersion.addPlotter(new Plotter(Bukkit.getPluginManager().getPlugin("ProtocolLib").getDescription().getVersion()) {
                    @Override
                    public int getValue() {
                        return 1;
                    }
                });
            }

            metrics.start();
            info("Metrics have been enabled!");
        } catch (Exception e) {
            info("Metrics failed to enable");
            Mundo.reportException(Mundo.class, e);
        }
    }

    //Checker Util

    public static <T> boolean check(Expression<T> expression, Event event, Function<T, Boolean> function) {
	    return expression.check(event, new Checker<T>() {
            @Override
            public boolean check(T t) {
                return function.apply(t);
            }
        });
    }

    //Logging Util

    public static boolean classDebugs(Class c) {
        return debugPackages.contains(c.getName().split("\\.")[3]);
    }

    public static void info(String s) {
        Mundo.instance.getLogger().info(s);
    }
	
	public static void reportException(Object obj, Exception e) {
		info("An exception has occured within MundoSK");
		info("Please report this to the MundoSK page on forums.skunity.com");
		info("Bukkit/Spigot version: " + Bukkit.getVersion());
		info("Skript version: " + Skript.getVersion());
		info("MundoSK version: " + instance.getDescription().getVersion());
		info("Exception at " + (obj instanceof Class ? (Class) obj : obj.getClass()).getSimpleName());
		e.printStackTrace();
	}
	
	public static void debug(Object obj, String msg) {
        Class debugClass = obj instanceof Class ? (Class) obj : obj.getClass();
        if (classDebugs(debugClass)) {
            info("DEBUG " + debugClass.getSimpleName() + ": " + msg);
        }
	}

    public static void debug(Object obj, Exception e) {
        Class debugClass = obj instanceof Class ? (Class) obj : obj.getClass();
		if (classDebugs(debugClass)) {
            info("DEBUG");
            info("An exception was reported for debugging while debug_mode was activated in the config");
            info("If you were told to activate debug_mode to help fix bugs in MundoSK on forums.skunity.com, then please copy and paste this message along with the full stack trace of the following error to hastebin.com and give the hastebin link to whoever is helping you fix this bug");
            info("If you are trying to fix a problem in MundoSK yourself, good luck :)");
            info("Otherwise, if you do not know why you are seeing this error here, go to the MundoSK config, set debug_mode to false, and restart your server");
            info("For help, go to the MundoSK page on forums.skunity.com");
            info("Bukkit/Spigot version: " + Bukkit.getVersion());
            info("Skript version: " + Skript.getVersion());
            info("MundoSK version: " + instance.getDescription().getVersion());
            info("Exception debugged at " + debugClass.getSimpleName());
            e.printStackTrace();
		}
	}

    //Custom Event Util
	
	public static <T> void registerCustomEventValue(ClassInfo<T> type) {
		EventValues.registerEventValue(UtilCustomEvent.class, type.getC(), new Getter<T, UtilCustomEvent>() {
			@SuppressWarnings("unchecked")
			@Override
			public T get(UtilCustomEvent e) {
				return (T) e.getDetail(type);
			}
		}, 0);
	}

    public static Boolean classInfoSafe(Class c, String name) {
        return Classes.getExactClassInfo(c) == null && Classes.getClassInfoNoError(name) == null;
    }

    //Math Util

    public static int intMod(int number, int mod) {
        return (number % mod) + (number >= 0 ? 0 : mod);
    }

    public static int limitToRange(int min, int num, int max) {
        if (num > max) return max;
        if (num < min) return min;
        return num;
    }

    public static boolean isInRange(double min, double num, double max) {
        return !(num > max || num < min);
    }

    public static char toHexDigit(int num) {
        return hexDigits.charAt(num % 16);
    }

    public static int divideNoRemainder(int dividend, int divisor) {
        return (dividend - (dividend % divisor)) / divisor;
    }

    public static int digitsInBase(int num, int base) {
        int result = 0;
        while (num > 0) {
            num /= base;
            result++;
        }
        return result;
    }

    //ListVariable Util

    public static TreeMap<String, Object> listVariableFromArray(Object[] array) {
        TreeMap<String, Object> result = new TreeMap<>();
        for (int i = 1; i <= array.length; i++) {
            if (array[i] instanceof Object[]) {
                result.put(i + "::*", listVariableFromArray((Object[]) array[i]));
            } else if (array[i] instanceof TreeMap) {
                result.put(i + "::*", array[i]);
            } else {
                result.put(i + "", array[i]);
            }
        }
        return result;
    }

    public static void setListVariable(String varname, TreeMap<String, Object> value, Event event, boolean isLocal) {
        value.forEach(new BiConsumer<String, Object>() {
            @Override
            public void accept(String s, Object o) {
                if (o instanceof TreeMap) {
                    setListVariable(varname + "::" + s, (TreeMap<String, Object>) o, event, isLocal);
                } else {
                    Variables.setVariable(varname + "::" + s, o, event, isLocal);
                }
            }
        });
    }

    //Scheduler Util

    public static void sync(Runnable runnable) {
        scheduler.runTask(instance, runnable);
    }

    public static void async(Runnable runnable) {
        scheduler.runTaskAsynchronously(instance, runnable);
    }

    public static void syncDelay(int ticks, Runnable runnable) {
        scheduler.runTaskLater(Mundo.instance, runnable, ticks);
    }

    public static void asyncDelay(int ticks, Runnable runnable) {
        scheduler.runTaskLaterAsynchronously(Mundo.instance, runnable, ticks);
    }

    //Miscellanous

    public static boolean serverHasPlugin(String pluginName) {
        return Bukkit.getPluginManager().getPlugin(pluginName) != null;
    }

    public static boolean classesCompatible(Class c1, Class c2) {
        return c1.isAssignableFrom(c2) || c2.isAssignableFrom(c1);
    }

    public static Class commonSuperClass(Class... classes) {
	    switch (classes.length) {
            case 0: return Object.class;
            case 1: return classes[0];
            case 2: {
                while (!classes[0].isAssignableFrom(classes[1])) {
                    classes[0] = classes[0].getSuperclass();
                }
                return classes[0];
            }
        }
        Class[] classesTail = new Class[classes.length - 1];
	    System.arraycopy(classes, 0, classesTail, 0, classes.length - 1);
        return commonSuperClass(classes[0], commonSuperClass(classesTail));
    }

    public static boolean settableTo(Expression settee) {return false;}

    //Reflection Util

    public static Object getStaticField(Class<?> location, String name) throws NoSuchFieldException, IllegalAccessException {
	    Field field = location.getField(name);
	    field.setAccessible(true);
	    return field.get(null);
    }

    public static boolean classExists(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean methodExists(Class c, String methodName, Class... params) {
        try {
            c.getMethod(methodName, params);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    //Functional Util

    public static <T, R> R[] mapArray(Function<T, R> function, T[] input) {
        return (R[]) Stream.of(input).map(function).collect(Collectors.toList()).toArray();
    }
	
}
