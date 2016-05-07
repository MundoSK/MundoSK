package com.pie.tlatoani.Json;

import com.pie.tlatoani.Json.spi.*;
import com.pie.tlatoani.Json.stream.*;

import java.io.*;
import java.util.Map;

/**
 * Factory class for creating JSON processing objects.
 * This class provides the most commonly used methods for creating these
 * objects and their corresponding factories. The factory classes provide
 * all the various ways to create these objects.
 *
 * <p>
 * The methods in this class locate a provider instance using the method
 * {@link JsonProvider#provider()}. This class uses the provider instance
 * to create JSON processing objects.
 *
 * <p>
 * The following example shows how to create a JSON parser to parse
 * an empty array:
 * <pre>
 * <code>
 * StringReader reader = new StringReader("[]");
 * JsonParser parser = Json.createParser(reader);
 * </code>
 * </pre>
 *
 * <p>
 * All the methods in this class are safe for use by multiple concurrent
 * threads.
 *
 * @author Jitendra Kotamraju
 */
public class Json {

    private Json() {
    }

    /**
     * Creates a JSON parser from a character stream.
     *
     * @param reader i/o reader from which JSON is to be read
     * @return a JSON parser
     */
    public static JsonParser createParser(Reader reader) {
        return JsonProvider.provider().createParser(reader);
    }

    /**
     * Creates a JSON parser from a byte stream.
     * The character encoding of the stream is determined as specified in 
     * <a href="http://tools.ietf.org/rfc/rfc4627.txt">RFC 4627</a>.
     *
     * @param in i/o stream from which JSON is to be read
     * @throws JsonException if encoding cannot be determined
     *         or i/o error (IOException would be cause of JsonException)
     * @return a JSON parser
     */
    public static JsonParser createParser(InputStream in) {
        return JsonProvider.provider().createParser(in);
    }

    /**
     * Creates a JSON generator for writing JSON to a character stream.
     *
     * @param writer a i/o writer to which JSON is written
     * @return a JSON generator
     */
    public static JsonGenerator createGenerator(Writer writer) {
        return JsonProvider.provider().createGenerator(writer);
    }

    /**
     * Creates a JSON generator for writing JSON to a byte stream.
     *
     * @param out i/o stream to which JSON is written
     * @return a JSON generator
     */
    public static JsonGenerator createGenerator(OutputStream out) {
        return JsonProvider.provider().createGenerator(out);
    }

    /**
     * Creates a parser factory for creating {@link JsonParser} objects.
     *
     * @return JSON parser factory.
     *
    public static JsonParserFactory createParserFactory() {
        return JsonProvider.provider().createParserFactory();
    }
     */

    /**
     * Creates a parser factory for creating {@link JsonParser} objects.
     * The factory is configured with the specified map of provider specific
     * configuration properties. Provider implementations should ignore any
     * unsupported configuration properties specified in the map.
     *
     * @param config a map of provider specific properties to configure the
     *               JSON parsers. The map may be empty or null
     * @return JSON parser factory
     */
    public static JsonParserFactory createParserFactory(Map<String, ?> config) {
        return JsonProvider.provider().createParserFactory(config);
    }

    /**
     * Creates a generator factory for creating {@link JsonGenerator} objects.
     *
     * @return JSON generator factory
     *
    public static JsonGeneratorFactory createGeneratorFactory() {
        return JsonProvider.provider().createGeneratorFactory();
    }
    */

    /**
     * Creates a generator factory for creating {@link JsonGenerator} objects.
     * The factory is configured with the specified map of provider specific
     * configuration properties. Provider implementations should ignore any
     * unsupported configuration properties specified in the map.
     *
     * @param config a map of provider specific properties to configure the
     *               JSON generators. The map may be empty or null
     * @return JSON generator factory
     */
    public static JsonGeneratorFactory createGeneratorFactory(
            Map<String, ?> config) {
        return JsonProvider.provider().createGeneratorFactory(config);
    }

    /**
     * Creates a JSON writer to write a
     * JSON {@link JsonObject object} or {@link JsonArray array}
     * structure to the specified character stream.
     *
     * @param writer to which JSON object or array is written
     * @return a JSON writer
     */
    public static JsonWriter createWriter(Writer writer) {
        return JsonProvider.provider().createWriter(writer);
    }

    /**
     * Creates a JSON writer to write a
     * JSON {@link JsonObject object} or {@link JsonArray array}
     * structure to the specified byte stream. Characters written to
     * the stream are encoded into bytes using UTF-8 encoding.
     *
     * @param out to which JSON object or array is written
     * @return a JSON writer
     */
    public static JsonWriter createWriter(OutputStream out) {
        return JsonProvider.provider().createWriter(out);
    }

    /**
     * Creates a JSON reader from a character stream.
     *
     * @param reader a reader from which JSON is to be read
     * @return a JSON reader
     */
    public static JsonReader createReader(Reader reader) {
        return JsonProvider.provider().createReader(reader);
    }

    /**
     * Creates a JSON reader from a byte stream. The character encoding of
     * the stream is determined as described in
     * <a href="http://tools.ietf.org/rfc/rfc4627.txt">RFC 4627</a>.
     *
     * @param in a byte stream from which JSON is to be read
     * @return a JSON reader
     */
    public static JsonReader createReader(InputStream in) {
        return JsonProvider.provider().createReader(in);
    }

    /**
     * Creates a reader factory for creating {@link JsonReader} objects.
     * The factory is configured with the specified map of provider specific
     * configuration properties. Provider implementations should ignore any
     * unsupported configuration properties specified in the map.
     *
     * @param config a map of provider specific properties to configure the
     *               JSON readers. The map may be empty or null
     * @return a JSON reader factory
     */
    public static JsonReaderFactory createReaderFactory(Map<String, ?> config) {
        return JsonProvider.provider().createReaderFactory(config);
    }

    /**
     * Creates a writer factory for creating {@link JsonWriter} objects.
     * The factory is configured with the specified map of provider specific
     * configuration properties. Provider implementations should ignore any
     * unsupported configuration properties specified in the map.
     *
     * @param config a map of provider specific properties to configure the
     *               JSON writers. The map may be empty or null
     * @return a JSON writer factory
     */
    public static JsonWriterFactory createWriterFactory(Map<String, ?> config) {
        return JsonProvider.provider().createWriterFactory(config);
    }

    /**
     * Creates a JSON array builder
     *
     * @return a JSON array builder
     */
    public static JsonArrayBuilder createArrayBuilder() {
        return JsonProvider.provider().createArrayBuilder();
    }

    /**
     * Creates a JSON object builder
     *
     * @return a JSON object builder
     */
    public static JsonObjectBuilder createObjectBuilder() {
        return JsonProvider.provider().createObjectBuilder();
    }

    /**
     * Creates a builder factory for creating {@link JsonArrayBuilder}
     * and {@link JsonObjectBuilder} objects.
     * The factory is configured with the specified map of provider specific
     * configuration properties. Provider implementations should ignore any
     * unsupported configuration properties specified in the map.
     *
     * @param config a map of provider specific properties to configure the
     *               JSON builders. The map may be empty or null
     * @return a JSON builder factory
     */
    public static JsonBuilderFactory createBuilderFactory(
            Map<String, ?> config) {
        return JsonProvider.provider().createBuilderFactory(config);
    }

}