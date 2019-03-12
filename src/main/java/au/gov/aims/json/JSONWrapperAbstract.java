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

import java.io.InvalidClassException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public abstract class JSONWrapperAbstract<K> {
    private Map<K, JSONWrapperValue> structure;

    public JSONWrapperAbstract() {
        this.structure = new HashMap<K, JSONWrapperValue>();
    }

    protected void put(K key, Object value) {
        this.structure.put(key, new JSONWrapperValue(value));
    }

    protected void put(K key, Object value, int visitCount) {
        this.structure.put(key, new JSONWrapperValue(value, visitCount));
    }

    public int length() {
        return this.structure.size();
    }

    public Class getClass(K key) {
        JSONWrapperValue valueWrapper = this.structure.get(key);
        if (valueWrapper == null) {
            return null;
        }

        Object value = valueWrapper.getValue();
        if (value == null) {
            return null;
        }

        return value.getClass();
    }

    public <T> T get(Class<T> type, K key, T defaultValue) throws InvalidClassException {
        T value = this.get(type, key);
        return (value == null ? defaultValue : value);
    }

    public <T> T get(Class<T> type, K key) throws InvalidClassException {
        if (JSONObject.class.equals(type)) {
            throw new IllegalArgumentException("Illegal class 'org.json.JSONObject'. Use 'au.gov.aims.json.JSONWrapperObject' instead.");
        }
        if (JSONArray.class.equals(type)) {
            throw new IllegalArgumentException("Illegal class 'org.json.JSONArray'. Use 'au.gov.aims.json.JSONWrapperArray' instead.");
        }

        JSONWrapperValue valueWrapper = this.structure.get(key);
        if (valueWrapper == null) {
            return null;
        }

        Object value = valueWrapper.getValue();

        T castValue = JSONUtils.toType(value, type);
        // Only increment the counter if the value was read using the proper type.
        valueWrapper.incrementCount();

        return castValue;
    }

    public Set<String> getNeverVisited() {
        // Use a TreeSet to keep entries ordered alphabetically
        Set<String> neverVisited = new TreeSet<String>();
        this.getNeverVisited(this, neverVisited, null);
        return neverVisited;
    }

    // Recursive
    private void getNeverVisited(JSONWrapperAbstract<K> json, Set<String> neverVisited, String path) {
        for (Map.Entry<K, JSONWrapperValue> valueWrapperEntry : json.structure.entrySet()) {
            K key = valueWrapperEntry.getKey();
            String valuePath = (key instanceof Integer) ?
                    (path == null ? "" : path) + "[" + key.toString() + "]" :
                    (path == null ? "" : path + ".") + key.toString();

            JSONWrapperValue valueWrapper = valueWrapperEntry.getValue();
            if (valueWrapper.getCount() <= 0) {
                neverVisited.add(valuePath);
            }

            Object value = valueWrapper.getValue();
            if (value != null && value instanceof JSONWrapperAbstract) {
                this.getNeverVisited((JSONWrapperAbstract)value, neverVisited, valuePath);
            }
        }
    }

    protected Map<K, JSONWrapperValue> getStructure() {
        return this.structure;
    }

    public abstract String toString(int indentFactor);

    protected void copyFrom(JSONWrapperAbstract<K> json) {
        for (Map.Entry<K, JSONWrapperValue> valueWrapperEntry : json.structure.entrySet()) {
            K key = valueWrapperEntry.getKey();
            JSONWrapperValue valueWrapper = valueWrapperEntry.getValue();

            Object value = valueWrapper.getValue();
            if (value instanceof JSONWrapperObject) {
                value = ((JSONWrapperObject)value).copy();
            } else if (value instanceof JSONWrapperArray) {
                value = ((JSONWrapperArray)value).copy();
            }

            json.put(key, new JSONWrapperValue(value, valueWrapper.getCount()));
        }
    }

    // Modify current object
    protected void inPlaceOverwrite(JSONWrapperAbstract<K> overwrites, String idKey) {
        for (Map.Entry<K, JSONWrapperValue> valueWrapperEntry : overwrites.structure.entrySet()) {
            K key = valueWrapperEntry.getKey();

            JSONWrapperValue overwriteWrapperValue = valueWrapperEntry.getValue();
            Object overwriteValue = overwriteWrapperValue == null ? null : overwriteWrapperValue.getValue();

            if (overwriteValue == null) {
                this.put(key, null);
            } else {
                JSONWrapperValue currentWrapperValue = this.structure.get(key);
                Object currentValue = currentWrapperValue == null ? null : currentWrapperValue.getValue();

                if (overwriteValue instanceof JSONWrapperObject) {
                    JSONWrapperObject jsonOverwriteValue = (JSONWrapperObject)overwriteValue;
                    if (currentValue == null) {
                        currentValue = new JSONWrapperObject();
                    }

                    if (currentValue instanceof JSONWrapperObject) {
                        JSONWrapperObject jsonCurrentValue = (JSONWrapperObject)currentValue;
                        jsonCurrentValue.overwrite(jsonOverwriteValue, idKey);
                        this.put(key, jsonCurrentValue, overwriteWrapperValue.getCount());
                    } else {
                        this.put(key, jsonOverwriteValue.copy(), overwriteWrapperValue.getCount());
                    }
                } else if (overwriteValue instanceof JSONWrapperArray) {
                    JSONWrapperArray jsonOverwriteValue = (JSONWrapperArray)overwriteValue;
                    if (currentValue == null) {
                        currentValue = new JSONWrapperArray();
                    }

                    if (currentValue instanceof JSONWrapperArray) {
                        JSONWrapperArray jsonCurrentValue = (JSONWrapperArray)currentValue;
                        jsonCurrentValue.overwrite(jsonOverwriteValue, idKey);
                        this.put(key, jsonCurrentValue, overwriteWrapperValue.getCount());
                    } else {
                        this.put(key, jsonOverwriteValue.copy(), overwriteWrapperValue.getCount());
                    }
                } else {
                    this.put(key, overwriteValue, overwriteWrapperValue.getCount());
                }
            }
        }
    }

    @Override
    public String toString() {
        return this.toString(4);
    }

    @Override
    public boolean equals(Object obj) {
        // Same instance
        if (this == obj) {
            return true;
        }

        if (obj == null || !(obj instanceof JSONWrapperAbstract)) {
            return false;
        }

        JSONWrapperAbstract<K> jsonWrapper = (JSONWrapperAbstract<K>)obj;

        Set<K> thisKeys = this.structure.keySet();
        Set<K> objKeys = jsonWrapper.structure.keySet();

        if (!thisKeys.equals(objKeys)) {
            return false;
        }

        for (K key : thisKeys) {
            if (!this.valueEquals(this.structure.get(key), jsonWrapper.structure.get(key))) {
                return false;
            }
        }

        return true;
    }

    private boolean valueEquals(Object val1, Object val2) {
        // Both null or same instance
        if (val1 == val2) {
            return true;
        }

        // One and only one of the value is null
        if (val1 == null || val2 == null) {
            return false;
        }

        return val1.equals(val2);
    }
}
