/*
 *  Copyright (C) 2016 Australian Institute of Marine Science
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

	public static void write(org.json.JSONObject json, File file) throws IOException {
		JSONUtils.write(json, file, -1);
	}

	public static void write(org.json.JSONObject json, File file, int indentFactor) throws IOException {
		if (file == null) {
			throw new IOException("Can not save video metadata; file is null");
		}
		if (json == null) {
			throw new IOException("Can not save video metadata to '" + file.getAbsolutePath() + "'; JSON is null");
		}

		Writer writer = null;
		try {
			writer = new FileWriter(file);
			if (indentFactor > 0) {
				json.write(writer, indentFactor, 0);
			} else {
				json.write(writer);
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
}
