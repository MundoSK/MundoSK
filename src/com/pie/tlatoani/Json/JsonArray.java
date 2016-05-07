package com.pie.tlatoani.Json;

import java.util.List;

/**
 * {@code JsonArray} represents an immutable JSON array
 * (an ordered sequence of zero or more values).
 * It also provides an unmodifiable list view of the values in the array.
 *
 * <p>A {@code JsonArray} object can be created by reading JSON data from
 * an input source or it can be built from scratch using an array builder
 * object.
 *
 * <p>The following example demonstrates how to create a {@code JsonArray}
 * object from an input source using the method {@link JsonReader#readArray()}:
 * <pre><code>
 * JsonReader jsonReader = Json.createReader(...);
 * JsonArray array = jsonReader.readArray();
 * jsonReader.close();
 * </code></pre>
 *
 * <p>The following example demonstrates how to build an empty JSON array
 * using the class {@link JsonArrayBuilder}:
 * <pre><code>
 * JsonArray array = Json.createArrayBuilder().build();
 * </code></pre>
 *
 * <p>The example code below demonstrates how to create the following JSON array:
 * <pre><code>
 * [
 *     { "type": "home", "number": "212 555-1234" },
 *     { "type": "fax", "number": "646 555-4567" }
 * ]
 * </code></pre>
 * <pre><code>
 * JsonArray value = Json.createArrayBuilder()
 *     .add(Json.createObjectBuilder()
 *         .add("type", "home")
 *         .add("number", "212 555-1234"))
 *     .add(Json.createObjectBuilder()
 *         .add("type", "fax")
 *         .add("number", "646 555-4567"))
 *     .build();
 * </code></pre>
 *
 * <p>The following example demonstrates how to write a {@code JsonArray} object
 * as JSON data:
 * <pre><code>
 * JsonArray arr = ...;
 * JsonWriter writer = Json.createWriter(...)
 * writer.writeArray(arr);
 * writer.close();
 * </code></pre>
 *
 * <p>The values in a {@code JsonArray} can be of the following types:
 * {@link JsonObject}, {@link JsonArray},
 * {@link JsonString}, {@link JsonNumber}, {@link JsonValue#TRUE},
 * {@link JsonValue#FALSE}, and {@link JsonValue#NULL}.
 * {@code JsonArray} provides various accessor methods to access the values
 * in an array.
 *
 * <p>The following example shows how to obtain the home phone number
 * "212 555-1234" from the array built in the previous example:
 * <pre><code>
 * JsonObject home = array.getJsonObject(0);
 * String number = home.getString("number");
 * </code></pre>
 *
 * <p>{@code JsonArray} instances are list objects that provide read-only
 * access to the values in the JSON array. Any attempt to modify the list,
 * whether directly or using its collection views, results in an
 * {@code UnsupportedOperationException}.
 *
 * @author Jitendra Kotamraju
 */
public interface JsonArray extends JsonStructure, List<JsonValue> {

    /**
     * Returns the object value at the specified position in this array.
     * This is a convenience method for {@code (JsonObject)get(index)}.
     *
     * @param index index of the value to be returned
     * @return the value at the specified position in this array
     * @throws IndexOutOfBoundsException if the index is out of range
     * @throws ClassCastException if the value at the specified position is not
     * assignable to the JsonObject type
     */
    JsonObject getJsonObject(int index);

    /**
     * Returns the array value at the specified position in this array.
     * This is a convenience method for {@code (JsonArray)get(index)}.
     *
     * @param index index of the value to be returned
     * @return the value at the specified position in this array
     * @throws IndexOutOfBoundsException if the index is out of range
     * @throws ClassCastException if the value at the specified position is not
     * assignable to the JsonArray type
     */
    JsonArray getJsonArray(int index);

    /**
     * Returns the number value at the specified position in this array.
     * This is a convenience method for {@code (JsonNumber)get(index)}.
     *
     * @param index index of the value to be returned
     * @return the value at the specified position in this array
     * @throws IndexOutOfBoundsException if the index is out of range
     * @throws ClassCastException if the value at the specified position is not
     * assignable to the JsonNumber type
     */
    JsonNumber getJsonNumber(int index);

    /**
     * Returns the string value at ths specified position in this array.
     * This is a convenience method for {@code (JsonString)get(index)}.
     *
     * @param index index of the value to be returned
     * @return the value at the specified position in this array
     * @throws IndexOutOfBoundsException if the index is out of range
     * @throws ClassCastException if the value at the specified position is not
     * assignable to the JsonString type
     */
    JsonString getJsonString(int index);

    /**
     * Returns a list a view of the specified type for the array. This method
     * does not verify if there is a value of wrong type in the array. Providing
     * this typesafe view dynamically may cause a program fail with a
     * {@code ClassCastException}, if there is a value of wrong type in this
     * array. Unfortunately, the exception can occur at any time after this
     * method returns.
     *
     * @param clazz a JsonValue type
     * @return a list view of the  specified type
     */
    <T extends JsonValue> List<T> getValuesAs(Class<T> clazz);

    /**
     * A convenience method for
     * {@code getJsonString(index).getString()}.
     *
     * @param index index of the {@code JsonString} value
     * @return the String value at the specified position
     * @throws IndexOutOfBoundsException if the index is out of range
     * @throws ClassCastException if the value at the specified position is not
     * assignable to {@code JsonString}
     */
    String getString(int index);

    /**
     * Returns the {@code String} value of {@code JsonString} at the specified
     * position in this JSON array values. If {@code JsonString} is found,
     * its {@link javax.json.JsonString#getString()} is returned. Otherwise,
     * the specified default value is returned.
     *
     * @param index index of the JsonString value
     * @return the String value at the specified position in this array,
     * or the specified default value
     */
    String getString(int index, String defaultValue);

    /**
     * A convenience method for
     * {@code getJsonNumber(index).intValue()}.
     *
     * @param index index of the {@code JsonNumber} value
     * @return the int value at the specified position
     * @throws IndexOutOfBoundsException if the index is out of range
     * @throws ClassCastException if the value at the specified position is not
     * assignable to {@code JsonNumber}
     */
    int getInt(int index);

    /**
     * Returns the int value of the {@code JsonNumber} at the specified position.
     * If the value at that position is a {@code JsonNumber},
     * this method returns {@link javax.json.JsonNumber#intValue()}. Otherwise
     * this method returns the specified default value.
     *
     * @param index index of the {@code JsonNumber} value
     * @return the int value at the specified position in this array,
     * or the specified default value
     */
    int getInt(int index, int defaultValue);

    /**
     * Returns the boolean value at the specified position.
     * If the value at the specified position is {@code JsonValue.TRUE}
     * this method returns {@code true}. If the value at the specified position
     * is {@code JsonValue.FALSE} this method returns {@code false}.
     *
     * @param index index of the JSON boolean value
     * @return the boolean value at the specified position
     * @throws IndexOutOfBoundsException if the index is out of range
     * @throws ClassCastException if the value at the specified position is not
     * assignable to {@code JsonValue.TRUE} or {@code JsonValue.FALSE}
     */
    boolean getBoolean(int index);

    /**
     * Returns the boolean value at the specified position.
     * If the value at the specified position is {@code JsonValue.TRUE}
     * this method returns {@code true}. If the value at the specified position
     * is {@code JsonValue.FALSE} this method returns {@code false}.
     * Otherwise this method returns the specified default value.
     *
     * @param index index of the JSON boolean value
     * @return the boolean value at the specified position,
     * or the specified default value
     */
    boolean getBoolean(int index, boolean defaultValue);

    /**
     * Returns {@code true} if the value at the specified location in this
     * array is {@code JsonValue.NULL}.
     *
     * @param index index of the JSON null value
     * @return return true if the value at the specified location is
     * {@code JsonValue.NUL}, otherwise false
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    boolean isNull(int index);

}