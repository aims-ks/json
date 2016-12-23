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
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

public class JSONObject extends JSONAbstract<String> {
	private org.json.JSONObject jsonObject;

	public JSONObject(String jsonObjectStr, File jsonFile) {
		this(new org.json.JSONObject(jsonObjectStr), null, jsonFile.getAbsolutePath());
	}

	public JSONObject(String jsonObjectStr, String jsonFilePath) {
		this(new org.json.JSONObject(jsonObjectStr), null, jsonFilePath);
	}

	public JSONObject(org.json.JSONObject jsonObject, File jsonFile) {
		this(jsonObject, null, jsonFile.getAbsolutePath());
	}

	public JSONObject(org.json.JSONObject jsonObject, String jsonFilePath) {
		this(jsonObject, null, jsonFilePath);
	}

	protected JSONObject(org.json.JSONObject jsonObject, String path, String jsonFilePath) {
		super(path);
		this.jsonObject = jsonObject;
	}

	@Override
	public Set<String> keySet() {
		return this.jsonObject.keySet();
	}

	public boolean has(String key) {
		return this.jsonObject.has(key);
	}

	public Class getClass(String key) {
		if (!this.jsonObject.has(key)) {
			return null;
		}

		Class valueClass = this.jsonObject.opt(key).getClass();
		if (org.json.JSONObject.class.equals(valueClass)) {
			return JSONObject.class;
		}
		if (org.json.JSONArray.class.equals(valueClass)) {
			return JSONArray.class;
		}

		return valueClass;
	}

	public <T> T get(Class<T> type, String key, T defaultValue) throws InvalidClassException {
		T value = this.get(type, key);
		return (value == null ? defaultValue : value);
	}
	public <T> T get(Class<T> type, String key) throws InvalidClassException {
		if (!this.jsonObject.has(key)) {
			return null;
		}

		if (org.json.JSONObject.class.equals(type)) {
			throw new IllegalArgumentException("Illegal class 'org.json.JSONObject'. Use 'au.gov.aims.json.JSONObject' instead.");
		}
		if (org.json.JSONArray.class.equals(type)) {
			throw new IllegalArgumentException("Illegal class 'org.json.JSONArray'. Use 'au.gov.aims.json.JSONArray' instead.");
		}

		//String currentPath = (this.path == null ? key : this.path + "." + key);

		Object rawValue = this.jsonObject.opt(key);

		return super.getValueAndCountVisit(type, key, rawValue);

/*
		if (org.json.JSONObject.NULL.equals(rawValue)) {
			rawValue = null;
		}

		if (rawValue instanceof org.json.JSONObject) {
			if (JSONObject.class.equals(type)) {
				if (this.visitCount.containsKey(key)) {
					Object wrapper = this.visitCount.get(key);
					if (wrapper instanceof JSONObject) {
						return (T)wrapper;
					}
				} else {
					JSONObject wrapper = new JSONObject((org.json.JSONObject)rawValue, currentPath);
					this.visitCount.put(key, wrapper);
					return (T)wrapper;
				}
			}

		} else if (rawValue instanceof org.json.JSONArray) {
			if (JSONArray.class.equals(type)) {
				if (this.visitCount.containsKey(key)) {
					Object wrapper = this.visitCount.get(key);
					if (wrapper instanceof JSONArray) {
						return (T)wrapper;
					}
				} else {
					JSONArray wrapper = new JSONArray((org.json.JSONArray)rawValue, currentPath);
					this.visitCount.put(key, wrapper);
					return (T)wrapper;
				}
			}

		} else if (JSONUtils.isInstanceOf(rawValue, type)) {
			// Increase visit count
			if (this.visitCount.containsKey(key)) {
				Object count = this.visitCount.get(key);
				if (count instanceof Integer) {
					this.visitCount.put(key, ((Integer)count + 1));
				}
			} else {
				this.visitCount.put(key, new Integer(1));
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
				"Found '" + rawValue.getClass().getName() + "'.");
*/
	}

	@Override
	public String toString() {
		return this.jsonObject.toString();
	}

	@Override
	public String toString(int indentFactor) {
		return this.jsonObject.toString(indentFactor);
	}
}
