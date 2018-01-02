package com.pie.tlatoani.Registration;

import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.Registration.DocumentationElement.*;
import com.pie.tlatoani.Util.Logging;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created by Tlatoani on 1/1/18.
 * Adapted from the SyntaxInfo and JsonFile classes of Tuke_Nuke's TuSKe
 * This is used to generate a documentation file to be imported into Skript Hub
 */
public class DocumentationFileGenerator {
    public static String FILENAME = "docs.json";

    public static void generateDocumentationFile() {
        try {
            File file = getDocumentationFile();
            JSONObject docsJSON = new JSONObject();
            docsJSON.put("events", jsonArrayOfDocElems(Documentation.getEvents()));
            docsJSON.put("conditions", jsonArrayOfDocElems(Documentation.getConditions()));
            docsJSON.put("effects", jsonArrayOfDocElems(Documentation.getEffects(), Documentation.getScopes()));
            docsJSON.put("expression", jsonArrayOfDocElems(Documentation.getExpressions()));
            docsJSON.put("types", jsonArrayOfDocElems(Documentation.getTypes()));
            new FileWriter(file).write(docsJSON.toJSONString());
        } catch (IOException e) {
            Logging.reportException(DocumentationFileGenerator.class, e);
        }
    }

    public static File getDocumentationFile() throws IOException {
        File result = new File(Mundo.INSTANCE.getDataFolder().getAbsolutePath() + File.separator + FILENAME);
        if (!result.exists()) {
            result.createNewFile();
        }
        return result;
    }

    public static JSONArray jsonArrayOfDocElems(List<? extends DocumentationElement>... docElemLists) {
        JSONArray result = new JSONArray();
        for (List<? extends DocumentationElement> docElems : docElemLists) {
            for (DocumentationElement docElem : docElems) {
                result.add(jsonOfDocElem(docElem));
            }
        }
        return result;
    }

    public static JSONObject jsonOfDocElem(DocumentationElement docElem) {
        JSONObject result = new JSONObject();
        result.put("name", docElem.name);
        result.put("id", idFromName(docElem.name));
        result.put("since", docElem.originVersion);
        if (docElem instanceof Expression) {
            result.put("return type", ((Expression) docElem).type.getDocName());
        }
        JSONArray descJSON = fromStringArray(docElem.description);
        if (docElem instanceof Scope) {
            descJSON.set(0, "Not an effect, but rather a scope (written with a colon at the end with a section of indented code under it, like an if statement or loop). ");
        }
        result.put("description", fromStringArray(docElem.description));
        result.put("patterns", fromStringArray(docElem.syntaxes));
        if (docElem instanceof Type) {
            Type typeDocElem = (Type) docElem;
            if (typeDocElem.usages.length > 0) {
                result.put("usage", fromStringArray(typeDocElem.usages));
            }
        }
        if (docElem instanceof Expression) {
            Expression exprDocElem = (Expression) docElem;
            if (exprDocElem.changers.length > 0) {
                JSONArray changers = new JSONArray();
                for (Changer changer : exprDocElem.changers) {
                    String mode = changer.mode.name().toLowerCase().replace('_', ' ');
                    if (!changers.contains(mode)) {
                        changers.add(mode);
                    }
                }
                result.put("changers", changers);
            }
        } else if (docElem instanceof Condition) {
            Condition condDocElem = (Condition) docElem;
            if (condDocElem.changers.length > 0) {
                JSONArray changers = new JSONArray();
                for (Changer changer : condDocElem.changers) {
                    String mode = changer.mode.name().toLowerCase().replace('_', ' ');
                    if (!changers.contains(mode)) {
                        changers.add(mode);
                    }
                }
                result.put("changers", changers);
            }
        }
        if (docElem instanceof Event) {
            Event eventDocElem = (Event) docElem;
            if (eventDocElem.eventValues.length > 0) {
                JSONArray eventValues = new JSONArray();
                for (EventValue eventValue : eventDocElem.eventValues) {
                    eventValues.add("event-" + eventValue.type.getCodeName());
                }
                result.put("event values", eventValues);
            }
            result.put("cancellable", eventDocElem.cancellable);
        }
        return result;
    }

    public static String idFromName(String name) {
        return name.toLowerCase().replace(' ', '_');
    }

    public static JSONArray fromStringArray(String[] array) {
        JSONArray jsonArray = new JSONArray();
        for (String elem : array) {
            jsonArray.add(elem);
        }
        return jsonArray;
    }
}
