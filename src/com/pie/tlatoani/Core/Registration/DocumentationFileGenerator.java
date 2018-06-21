package com.pie.tlatoani.Core.Registration;

import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.Core.Registration.DocumentationElement.*;
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

    public static void generateDocumentationFile() throws IOException {
        File file = getDocumentationFile();
        JSONObject docsJSON = new JSONObject();
        docsJSON.put("events", jsonArrayOfDocElems(Documentation.getEvents()));
        docsJSON.put("conditions", jsonArrayOfDocElems(Documentation.getConditions()));
        docsJSON.put("effects", jsonArrayOfDocElems(Documentation.getEffects(), Documentation.getScopes()));
        docsJSON.put("expressions", jsonArrayOfDocElems(Documentation.getExpressions()));
        docsJSON.put("types", jsonArrayOfDocElems(Documentation.getTypes()));
        FileWriter fileWriter = new FileWriter(file);
        docsJSON.writeJSONString(fileWriter);
        fileWriter.flush();
        fileWriter.close();
    }

    public static File getDocumentationFile() throws IOException {
        File result = new File(Mundo.get().getDataFolder().getAbsolutePath() + File.separator + FILENAME);
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
        result.put("since", fromStringArray(docElem.originVersion));
        if (docElem instanceof Expression) {
            result.put("return type", ((Expression) docElem).type.getDocName());
        }
        JSONArray descJSON = fromStringList(docElem.description);
        if (docElem instanceof Scope) {
            descJSON.add(0, "Not an effect, but rather a scope (written with a colon at the end with a section of indented code under it, like an if statement or loop). ");
        }
        result.put("description", descJSON);
        result.put("patterns", fromStringList(docElem.syntaxes));
        if (docElem instanceof Type) {
            Type typeDocElem = (Type) docElem;
            if (typeDocElem.usages.size() > 0) {
                result.put("usage", fromStringList(typeDocElem.usages));
            }
        }
        if (docElem instanceof Expression) {
            Expression exprDocElem = (Expression) docElem;
            if (exprDocElem.changers.size() > 0) {
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
            if (condDocElem.changers.size() > 0) {
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
            if (eventDocElem.eventValues.size() > 0) {
                JSONArray eventValues = new JSONArray();
                for (EventValue eventValue : eventDocElem.eventValues) {
                    eventValues.add("event-" + eventValue.type.getCodeName());
                }
                result.put("event values", eventValues);
            }
            result.put("cancellable", eventDocElem.cancellable);
        }
        if (docElem.examples.size() == 1) {
            result.put("examples", fromStringList(docElem.examples.get(0)));
        } else if (docElem.examples.size() > 1) {
            JSONArray examples = new JSONArray();
            for (int i = 1; i <= docElem.examples.size(); i++) {
                if (!examples.isEmpty()) {
                    examples.add("");
                }
                examples.add("#Example " + i);
                for (String line : docElem.examples.get(i - 1)) {
                    examples.add(line);
                }
            }
            result.put("examples", examples);
        }
        return result;
    }

    public static String idFromName(String name) {
        return name.toLowerCase().replace(' ', '_');
    }

    public static JSONArray fromStringArray(String... array) {
        JSONArray jsonArray = new JSONArray();
        for (String elem : array) {
            jsonArray.add(elem);
        }
        return jsonArray;
    }

    public static JSONArray fromStringList(List<String> list) {

        JSONArray jsonArray = new JSONArray();
        for (String elem : list) {
            jsonArray.add(elem);
        }
        return jsonArray;
    }
}
