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

import java.io.File;
import java.io.InvalidClassException;
import java.io.ObjectInput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Observable;
import java.util.Set;
import java.util.TreeSet;

public class JSONArray extends JSONAbstract<Integer> {
	private org.json.JSONArray jsonArray;

	public JSONArray(String jsonArrayStr, File jsonFile) {
		this(new org.json.JSONArray(jsonArrayStr), null, jsonFile.getAbsolutePath());
	}

	public JSONArray(String jsonArrayStr, String jsonFilePath) {
		this(new org.json.JSONArray(jsonArrayStr), null, jsonFilePath);
	}

	public JSONArray(org.json.JSONArray jsonArray, File jsonFile) {
		this(jsonArray, null, jsonFile.getAbsolutePath());
	}

	public JSONArray(org.json.JSONArray jsonArray, String jsonFilePath) {
		this(jsonArray, null, jsonFilePath);
	}

	protected JSONArray(org.json.JSONArray jsonArray, String path, String jsonFilePath) {
		super(path);
		this.jsonArray = jsonArray;
	}

	public int length() {
		return this.jsonArray.length();
	}

	public Class getClass(int index) {
		if (index >= this.jsonArray.length()) {
			return null;
		}

		Class valueClass = this.jsonArray.opt(index).getClass();
		if (org.json.JSONObject.class.equals(valueClass)) {
			return JSONObject.class;
		}
		if (org.json.JSONArray.class.equals(valueClass)) {
			return JSONArray.class;
		}

		return valueClass;
	}

	public <T> T get(Class<T> type, int index, T defaultValue) throws InvalidClassException {
		T value = this.get(type, index);
		return (value == null ? defaultValue : value);
	}
	public <T> T get(Class<T> type, int index) throws InvalidClassException {
		if (index < 0 || this.jsonArray.length()-1 < index) {
			return null;
		}

		if (org.json.JSONObject.class.equals(type)) {
			throw new IllegalArgumentException("Illegal class 'org.json.JSONObject'. Use 'au.gov.aims.json.JSONObject' instead.");
		}
		if (org.json.JSONArray.class.equals(type)) {
			throw new IllegalArgumentException("Illegal class 'org.json.JSONArray'. Use 'au.gov.aims.json.JSONArray' instead.");
		}

		// Array index "1" based in error messages, for better readability.
//		String currentPath = (this.path == null ? "[" + (index+1) + "]" : this.path + "[" + (index+1) + "]");

		Object rawValue = this.jsonArray.opt(index);

		return super.getValueAndCountVisit(type, index, rawValue);

/*
		if (org.json.JSONObject.NULL.equals(rawValue)) {
			rawValue = null;
		}

		if (rawValue instanceof org.json.JSONObject) {
			if (JSONObject.class.equals(type)) {
				if (this.visitCount.containsKey(index)) {
					Object wrapper = this.visitCount.get(index);
					if (wrapper instanceof JSONObject) {
						return (T)wrapper;
					}
				} else {
					JSONObject wrapper = new JSONObject((org.json.JSONObject)rawValue, currentPath);
					this.visitCount.put(index, wrapper);
					return (T)wrapper;
				}
			}

		} else if (rawValue instanceof org.json.JSONArray) {
			if (JSONArray.class.equals(type)) {
				if (this.visitCount.containsKey(index)) {
					Object wrapper = this.visitCount.get(index);
					if (wrapper instanceof JSONArray) {
						return (T)wrapper;
					}
				} else {
					JSONArray wrapper = new JSONArray((org.json.JSONArray)rawValue, currentPath);
					this.visitCount.put(index, wrapper);
					return (T)wrapper;
				}
			}

		} else if (JSONUtils.isInstanceOf(rawValue, type)) {
			// Increase visit count
			if (this.visitCount.containsKey(index)) {
				Object count = this.visitCount.get(index);
				if (count instanceof Integer) {
					this.visitCount.put(index, ((Integer)count + 1));
				}
			} else {
				this.visitCount.put(index, new Integer(1));
			}

			return (T)rawValue;
		}

		// Do not display "JSONObjectWrapper" or "JSONArrayWrapper". It's confusing.
		String typeName = type.getSimpleName();
		if (type.equals(JSONObject.class)) {
			typeName = "JSONObject";
		} else if (type.equals(JSONArray.class)) {
			typeName = "JSONArray";
		}

		throw new InvalidClassException("Invalid attribute type. " +
				"Expected type '" + typeName + "' for attribute " + currentPath + ". " +
				"Found '" + rawValue.getClass().getSimpleName() + "'.");
*/
	}

	@Override
	protected Collection<Integer> keySet() {
		List<Integer> keys = new ArrayList<Integer>();
		for (int i=0; i<this.jsonArray.length(); i++) {
			keys.add(i);
		}

		return keys;
	}

	@Override
	public String toString() {
		return this.jsonArray.toString();
	}

	@Override
	public String toString(int indentFactor) {
		return this.jsonArray.toString(indentFactor);
	}
}
