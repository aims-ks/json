/*
 *  Copyright (C) 2019 Australian Institute of Marine Science
 *
 *  Contact: Gael Lafond <g.lafond@aims.org.au>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package au.gov.aims.json;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InvalidClassException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class JSONUtils {
    private static final Charset DEFAULT_ENCODING = StandardCharsets.UTF_8;

    public static String streamToString(InputStream inputStream) throws IOException {
        return JSONUtils.streamToString(inputStream, DEFAULT_ENCODING, true);
    }

    public static String streamToString(InputStream inputStream, boolean removeComments) throws IOException {
        return JSONUtils.streamToString(inputStream, DEFAULT_ENCODING, removeComments);
    }

    /**
     * Read a file into a String.
     * See: http://stackoverflow.com/questions/326390/how-to-create-a-java-string-from-the-contents-of-a-file#326440
     * @param inputStream
     * @param encoding
     * @return
     * @throws IOException
     */
    public static String streamToString(InputStream inputStream, Charset encoding, boolean removeComments) throws IOException {
        if (inputStream == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, encoding));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (!removeComments || !line.trim().startsWith("//")) {
                    sb.append(line).append("\n");
                }
            }
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }
        return sb.toString();
    }

    public static void write(JSONObject json, File file) throws IOException {
        JSONUtils.internalWrite(json, file, -1);
    }

    public static void write(JSONObject json, File file, int indentFactor) throws IOException {
        JSONUtils.internalWrite(json, file, indentFactor);
    }

    public static void write(JSONArray json, File file) throws IOException {
        JSONUtils.internalWrite(json, file, -1);
    }

    public static void write(JSONArray json, File file, int indentFactor) throws IOException {
        JSONUtils.internalWrite(json, file, indentFactor);
    }

    private static void internalWrite(Object json, File file, int indentFactor) throws IOException {
        if (file == null) {
            throw new IOException("Can not save JSON; file is null");
        }
        if (json == null) {
            throw new IOException("Can not save JSON to '" + file.getAbsolutePath() + "'; JSON is null");
        }

        Writer writer = null;
        try {
            writer = new FileWriter(file);

            if (json instanceof JSONObject) {
                // JSONObject
                JSONObject jsonObject = (JSONObject)json;
                if (indentFactor > 0) {
                    jsonObject.write(writer, indentFactor, 0);
                } else {
                    jsonObject.write(writer);
                }

            } else if (json instanceof JSONArray) {
                // JSONArray
                JSONArray jsonArray = (JSONArray)json;
                if (indentFactor > 0) {
                    jsonArray.write(writer, indentFactor, 0);
                } else {
                    jsonArray.write(writer);
                }

            } else {
                throw new IOException("Can not save JSON to '" + file.getAbsolutePath() + "'; JSON is not a JSONObject or a JSONArray");
            }

        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    /**
     * Return the "value" in the requested "type".
     * If the value can't be converted to the resquested type, a InvalidClassException is thrown.
     */
    public static <T> T toType(Object value, Class<T> type) throws InvalidClassException {
        // Null can be any type
        if (value == null) {
            return null;
        }

        // Equivalent to:
        //   value instanceof type
        if (type.isInstance(value)) {
            return (T)value;
        }

        // Type equivalence

        // Everything can be cast as a String
        if (String.class.equals(type)) {
            return (T)value;
        }

        if (Double.class.equals(type)) {
            // Integer can be cast as Double
            if (value instanceof Integer) {
                Integer intValue = (Integer)value;
                return (T)new Double(intValue.doubleValue());
            }
            // Float can be cast as Double
            if (value instanceof Float) {
                Float floatValue = (Float)value;
                return (T)new Double(floatValue.doubleValue());
            }
        }

        if (Float.class.equals(type)) {
            // Integer can be cast as Float
            if (value instanceof Integer) {
                Integer intValue = (Integer)value;
                return (T)new Float(intValue.floatValue());
            }
            // Double can be cast as Float (most of the time)
            if (value instanceof Double) {
                Double doubleValue = (Double)value;
                return (T)new Float(doubleValue.floatValue());
            }
        }

        throw new InvalidClassException("Object '" + value.toString() + "' " +
                "of class '" + value.getClass().getName() + "' " +
                "can not be convert into '" + type.getName() + "'");
    }

    /**
     * Create a deep clone copy of a JSON object
     * @param json
     * @return
     */
    public static JSONObject copy(JSONObject json) {
        return new JSONObject(json.toString());
    }

    /**
     * Create a deep clone copy of a JSON array
     * @param json
     * @return
     */
    public static JSONArray copy(JSONArray json) {
        return new JSONArray(json.toString());
    }

    public static boolean equals(JSONObject json1, JSONObject json2) {
        // Same instance or both null
        if (json1 == json2) {
            return true;
        }
        if (json1 == null || json2 == null) {
            return false;
        }

        // If one is empty, both needs to be empty
        if (json1.isEmpty() || json2.isEmpty()) {
            return json1.isEmpty() && json2.isEmpty();
        }

        Set<String> keys1 = json1.keySet();
        Set<String> keys2 = json2.keySet();
        if (!keys1.equals(keys2)) {
            return false;
        }

        for (String key : keys1) {
            if (!valueEquals(json1.opt(key), json2.opt(key))) {
                return false;
            }
        }

        return true;
    }

    public static boolean equals(JSONArray json1, JSONArray json2) {
        // Same instance or both null
        if (json1 == json2) {
            return true;
        }
        if (json1 == null || json2 == null) {
            return false;
        }

        // If one is empty, both needs to be empty
        if (json1.isEmpty() || json2.isEmpty()) {
            return json1.isEmpty() && json2.isEmpty();
        }

        int size1 = json1.length();
        int size2 = json2.length();
        if (size1 != size2) {
            return false;
        }

        for (int i=0; i<size1; i++) {
            if (!valueEquals(json1.opt(i), json2.opt(i))) {
                return false;
            }
        }

        return true;
    }

    private static boolean valueEquals(Object jsonValue1, Object jsonValue2) {
        // Same instance or both null
        if (jsonValue1 == jsonValue2) {
            return true;
        }

        if (jsonValue1 instanceof JSONObject) {
            return (jsonValue2 instanceof JSONObject) ?
                JSONUtils.equals((JSONObject)jsonValue1, (JSONObject)jsonValue2) :
                false;
        }

        if (jsonValue1 instanceof JSONArray) {
            return (jsonValue2 instanceof JSONArray) ?
                JSONUtils.equals((JSONArray)jsonValue1, (JSONArray)jsonValue2) :
                false;
        }

        return jsonValue1.equals(jsonValue2);
    }

    /**
     * Overwrites the values of parameter base with the values of parameter overwrites.
     * This is not a merge, it replace arrays instead of attempting to merge their values.
     * NOTE: It would be nearly impossible to instruct the overwrites to reorder the array and/or remove an element
     *   from a JSONArray from the base (matching indexes is a terribly bad idea), therefor this method simply
     *   replace JSONArray from the base with the JSONArray in the overwrites.
     *
     * Example:
     *   JSONObject base = new JSONObject()
     *       .put("str", "orig")            // Will be overwritten with value "overwritten"
     *       .put("ver", "1.2")             // Kept as-is since it's not in overwrites
     *       .put("array", new JSONArray()
     *           .put("a")                  // Kept since it's in overwrites
     *           .put("b")                  // Removed since it's not in overwrites
     *           .put("c"));                // Removed since it's not in overwrites
     *
     *   JSONObject overwrites = new JSONObject()
     *       .put("str", "overwritten")
     *       .put("array", new JSONArray()
     *           .put("a")
     *           .put("d"));                // Added
     *
     *   JSONObject result = JSONUtils.overwrites(base, overwrites);
     *   System.out.println(result.toString(4));
     *
     * Outputs:
     *   {
     *       "str": "overwritten",
     *       "ver": "1.2",
     *       "array": [
     *           "a",
     *           "d"
     *       ]
     *   }
     * @param base The base JSONObject that needs its values to be overwritten.
     *   NOTE: This parameter is safe, it won't be modified by this method.
     * @param overwrites The values that will be used to overwrite the base.
     *   NOTE: This parameter is safe, it won't be modified by this method.
     * @return A new JSONObject representing the base overwritten with the values from the overwrites parameter.
     */
    public static JSONObject overwrites(JSONObject base, JSONObject overwrites) {
        if (base == null) {
            return JSONUtils.copy(overwrites);
        }
        if (overwrites == null) {
            return JSONUtils.copy(base);
        }

        JSONObject copy = JSONUtils.copy(base);

        return recursiveOverwrites(copy, overwrites);
    }

    private static JSONObject recursiveOverwrites(JSONObject base, JSONObject overwrites) {
        Set<String> keys = overwrites.keySet();
        for (String key : keys) {
            Object value = overwrites.get(key);
            if (value == null || value.equals(JSONObject.NULL)) {
                base.remove(key);
            } else {
                if (value instanceof JSONObject) {
                    JSONObject origValue = base.optJSONObject(key);
                    if (origValue != null) {
                        value = JSONUtils.recursiveOverwrites(origValue, (JSONObject) value);
                    }
                }

                base.put(key, value);
            }
        }

        return base;
    }
}
