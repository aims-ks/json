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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

	/**
	 * Return true if "value" is an instance of "type", or equivalent.
	 * Equivalent means that java will let the library cast "value" into "type".
	 * Example:
	 *   String value or value "null" is equivalent to any "type".
	 *   Integer value is equivalent to Double.
	 */
	public static boolean isInstanceOf(Object value, Class type) {
		// Null can be any type
		if (value == null) {
			return true;
		}

		// Equivalent to:
		//   value instanceof type
		if (type.isInstance(value)) {
			return true;
		}

		// Type equivalence

		// Everything can be cast as a String
		if (String.class.equals(type)) {
			return true;
		}

		if (Double.class.equals(type)) {
			// Integer can be cast as Double
			if (value instanceof Integer) {
				return true;
			}
			// Float can be cast as Double
			if (value instanceof Float) {
				return true;
			}
		}

		if (Float.class.equals(type)) {
			// Integer can be cast as Float
			if (value instanceof Integer) {
				return true;
			}
			// Double can be cast as Float (most of the time)
			if (value instanceof Double) {
				return true;
			}
		}

		return false;
	}
}
