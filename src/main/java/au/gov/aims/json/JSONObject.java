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

import java.util.Set;

public class JSONObject extends JSONAbstract<String> {
	private org.json.JSONObject jsonObject;

	public JSONObject(String jsonObjectStr) {
		this(new org.json.JSONObject(jsonObjectStr), null);
	}

	public JSONObject(org.json.JSONObject jsonObject) {
		this(jsonObject, null);
	}

	protected JSONObject(org.json.JSONObject jsonObject, String path) {
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

	public <T> T get(Class<T> type, String key, T defaultValue) throws InvalidJSONException {
		T value = this.get(type, key);
		return (value == null ? defaultValue : value);
	}
	public <T> T get(Class<T> type, String key) throws InvalidJSONException {
		if (!this.jsonObject.has(key)) {
			return null;
		}

		if (org.json.JSONObject.class.equals(type)) {
			throw new IllegalArgumentException("Illegal class 'org.json.JSONObject'. Use 'au.gov.aims.json.JSONObject' instead.");
		}
		if (org.json.JSONArray.class.equals(type)) {
			throw new IllegalArgumentException("Illegal class 'org.json.JSONArray'. Use 'au.gov.aims.json.JSONArray' instead.");
		}

		Object rawValue = this.jsonObject.opt(key);

		return super.getValueAndCountVisit(type, key, rawValue);
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
