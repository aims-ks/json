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

import java.util.Map;
import java.util.Set;

public class JSONWrapperObject extends JSONWrapperAbstract<String> {

    protected JSONWrapperObject() {
        this((JSONObject)null);
    }


    public JSONWrapperObject(String jsonObjectStr) {
        this(new JSONObject(jsonObjectStr));
    }

    public JSONWrapperObject(JSONObject jsonObject) {
        super();
        this.parse(jsonObject);
    }

    public JSONWrapperObject(Map map) {
        super();
        this.parse(new JSONObject(map));
    }

    private void parse(JSONObject jsonObject) {
        if (jsonObject != null) {
            for (String key : jsonObject.keySet()) {
                Object value = jsonObject.opt(key);
                if (value == null || JSONObject.NULL.equals(value)) {
                    super.put(key, null);
                } else if (value instanceof JSONArray) {
                    super.put(key, new JSONWrapperArray((JSONArray)value));
                } else if (value instanceof JSONObject) {
                    super.put(key, new JSONWrapperObject((JSONObject)value));
                } else {
                    super.put(key, value);
                }
            }
        }
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();

        // NOTE: Extract values directly from the structure to avoid incrementing the visit count
        Map<String, JSONWrapperValue> structure = this.getStructure();
        for (String key : structure.keySet()) {
            JSONWrapperValue valueWrapper = structure.get(key);
            Object value = JSONObject.NULL;
            if (valueWrapper != null) {
                Object rawValue = valueWrapper.getValue();
                if (rawValue != null) {
                    if (rawValue instanceof JSONWrapperArray) {
                        value = ((JSONWrapperArray)rawValue).toJSON();
                    } else if (rawValue instanceof JSONWrapperObject) {
                        value = ((JSONWrapperObject)rawValue).toJSON();
                    } else {
                        value = rawValue;
                    }
                }
            }

            json.put(key, value);
        }

        return json;
    }

    public Set<String> keySet() {
        return super.getStructure().keySet();
    }

    public boolean has(String key) {
        return super.getStructure().containsKey(key);
    }

    public JSONWrapperObject copy() {
        JSONWrapperObject json = new JSONWrapperObject();
        json.copyFrom(this);
        return json;
    }

    /**
     * Returns a new {@link JSONWrapperObject} representing the current object (this) overwritten with
     * the values from the overwrites parameter.
     * Both the current object (this) and the overwrites parameter are unmodified.
     * @param overwrites
     * @return
     */
    public JSONWrapperObject overwrite(JSONWrapperObject overwrites) {
        return this.overwrite(overwrites, null);
    }
    public JSONWrapperObject overwrite(JSONWrapperObject overwrites, String idKey) {
        JSONWrapperObject copy = this.copy();
        copy.inPlaceOverwrite(overwrites, idKey);

        return copy;
    }

    @Override
    public boolean equals(Object obj) {
        // Same instance
        if (this == obj) {
            return true;
        }

        if (obj == null || !(obj instanceof JSONWrapperObject)) {
            return false;
        }

        return super.equals(obj);
    }

    @Override
    public String toString(int indentFactor) {
        return this.toJSON().toString(indentFactor);
    }
}
