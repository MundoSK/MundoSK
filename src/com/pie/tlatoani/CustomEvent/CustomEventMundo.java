package com.pie.tlatoani.CustomEvent;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.registrations.Classes;
import com.pie.tlatoani.Core.Static.Logging;
import com.pie.tlatoani.Core.Static.Reflection;
import com.pie.tlatoani.Core.Registration.Registration;

import java.util.List;

/**
 * Created by Tlatoani on 8/8/17.
 */
public class CustomEventMundo {
    
    public static void load() {
        Registration.registerEffect(EffCallCustomEvent.class, "(0¦call|1¦async call|1¦call async) custom event %strings% [to] [det[ail]s %-objects%] [arg[ument]s %-objects%]")
                .document("Call Custom Event", "1.6.7", "Calls a custom event with the specified id and optionally the specified details and/or arguments. "
                        + "Details are used in events as event-type. For example, if you had a detail 3426, event-number would equal 3426. "
                        + "Details may be of any type that is in Skript by default (number, string, player, world, etc.) as well as of any type added in MundoSK (creator, achievement, difficulty, etc.), and possibly other addons, depending on what loads in what order. "
                        + "You can't have two or more details of the same type. If you try to do this, only the detail that comes last of that type will be used. "
                        + "Arguments are like details, except that you may have multiple arguments of the same type, and they can be of any type from any addon. "
                        + "Arguments are accessed from within the event using the Args of Custom Event expression. "
                        + "For both details and arguments, you have to put them in a list variable and then call the custom event using the list variable in the syntax, otherwise you'll get an internal error. "
                        + "In MundoSK 1.8, two new features have been introduced (these will not work in previous versions):"
                        + "First, you can specify that the custom event is being called asynchronously. When code running in async is calling a custom event, this should be specified in order to prevent errors and corruption. "
                        + "Second, you can specify multiple custom event ids. This allows users of your custom events to choose from a variety of possible ids to list for custom events. "
                        + "Note that you must specify at least one id, otherwise no event will be called.");
        Registration.registerEvent("Custom Event", EvtCustomEvent.class, SkriptCustomEvent.class, "ev[en]t %strings%")
                .document("Custom Event", "1.6.7", "Called when the Call Custom Event effect is used with the specified id or one of the specified ids. "
                        + "This is used as a way for Skripters to create their own \"events\". See the Call Custom Event effect for more info.");
        Registration.registerExpression(ExprIDOfCustomEvent.class, String.class, ExpressionType.SIMPLE, "(0¦id|1¦ids) of custom event", "custom event's (0¦id|1¦ids)")
                .document("ID of Custom Event", "1.6.7", "An expression, used in the Custom Event event, for either the primary id, or all ids (MundoSK 1.8+), of the custom event that was called. "
                        + "The primary id means the one that was listed first when calling the custom event. See the Call Custom Event effect for more info.");
        Registration.registerExpression(ExprArgsOfCustomEvent.class, Object.class, ExpressionType.SIMPLE,"args of custom event", "custom event's args")
                .document("Args of Custom Event", "1.6.7", "An expression, used in the Custom Event event, for a list of the arguments, if any, "
                        + "that were specified for this particular custom event call. See the Call Custom Event effect for more info.");
        Registration.registerExpressionCondition(CondLastCustomEventCancelled.class, ExpressionType.SIMPLE, "last [called] custom event (0¦was|1¦wasn't) cancelled")
                .document("Last Called Custom Event was Cancelled", "1.8", "Checks whether the last custom event that was called in this trigger was or wasn't cancelled. "
                        + "This expression/condition is unaffected by whether another section of code calls a custom event. See the Call Custom Event effect for more info about custom events.");

        try {
            List<ClassInfo<?>> classes = (List<ClassInfo<?>>) Reflection.getStaticField(Classes.class, "tempClassInfos");
            for (int i = 0; i < classes.size(); i++) {
                registerCustomEventValue(classes.get(i));
            }
        } catch (Exception e1) {
            Logging.reportException(CustomEventMundo.class, e1);
        }
    }

    public static <T> void registerCustomEventValue(ClassInfo<T> type) {
        Registration.registerEventValue(SkriptCustomEvent.class, type.getC(), e -> (T) e.getDetail(type));
    }
}
