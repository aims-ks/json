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

import java.io.InvalidClassException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public abstract class JSONAbstract<K> {
	private String path;

	// Map of
	// * <Key(String), Count(Integer)>
	// * <Key(String), JSONObjectWrapper>
	// * <Key(String), JSONArrayWrapper>
	private Map<K, Object> visitCount;

	public JSONAbstract(String path) {
		this.path = path;
		this.visitCount = new HashMap<K, Object>();
	}

	protected <T> T getValueAndCountVisit(Class<T> type, K key, Object rawValue) throws InvalidJSONException {
		if (org.json.JSONObject.NULL.equals(rawValue)) {
			rawValue = null;
		}

		String currentPath = this.getPath(this.path, key);

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

		} else {

			try {
				T value = JSONUtils.toType(rawValue, type);

				// Increase visit count (only if the value was retrieved without crashing)
				if (this.visitCount.containsKey(key)) {
					Object count = this.visitCount.get(key);
					if (count instanceof Integer) {
						this.visitCount.put(key, ((Integer)count + 1));
					}
				} else {
					this.visitCount.put(key, 1);
				}

				return value;
			} catch(InvalidClassException cause) {
				throw new InvalidJSONException("Invalid attribute type. " +
						"Expected type '" + type.getSimpleName() + "' for attribute " + currentPath + ". " +
						"Found '" + rawValue.getClass().getName() + "'.", cause);
			}
		}

		return null;
	}

	public Set<String> getNeverVisited() {
		Set<String> neverVisited = new TreeSet<String>();
		this.getNeverVisited(neverVisited, this.path);
		return neverVisited;
	}

	private void getNeverVisited(Set<String> neverVisited, String path) {
		for (K key : this.keySet()) {
			String currentPath = this.getPath(path, key);

			if (!this.visitCount.containsKey(key)) {
				neverVisited.add(currentPath);
			} else {
				Object countObj = this.visitCount.get(key);
				if (countObj instanceof Integer) {
					Integer count = (Integer)countObj;
					if (count <= 0) {
						neverVisited.add(currentPath);
					}
				} else if (countObj instanceof JSONAbstract) {
					JSONAbstract wrapper = (JSONAbstract)countObj;
					wrapper.getNeverVisited(neverVisited, currentPath);
				}
			}
		}
	}

	protected String getPath(String parent, K key) {
		if (key instanceof Integer) {
			Integer intKey = (Integer)key;
			return ((parent == null || parent.isEmpty()) ?
					"[" + (intKey+1) + "]" :
					parent + "[" + (intKey+1) + "]");
		} else {
			String strKey = key.toString();
			return ((parent == null || parent.isEmpty()) ?
					strKey :
					parent + "." + strKey);
		}
	}

	protected abstract Collection<K> keySet();

	public abstract String toString(int indentFactor);
}
