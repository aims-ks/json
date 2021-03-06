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

import java.util.Collection;
import java.util.Map;

public class JSONWrapperArray extends JSONWrapperAbstract<Integer> {

    protected JSONWrapperArray() {
        this((JSONArray)null);
    }

    public JSONWrapperArray(String jsonArrayStr) {
        this(new JSONArray(jsonArrayStr));
    }

    public JSONWrapperArray(JSONArray jsonArray) {
        super();
        this.parse(jsonArray);
    }

    public JSONWrapperArray(Collection collection) {
        super();
        this.parse(new JSONArray(collection));
    }

    private void parse(JSONArray jsonArray) {
        if (jsonArray != null) {
            for (int i=0; i<jsonArray.length(); i++) {
                Object value = jsonArray.opt(i);
                if (value == null || JSONObject.NULL.equals(value)) {
                    super.put(i, null);
                } else if (value instanceof JSONArray) {
                    super.put(i, new JSONWrapperArray((JSONArray)value));
                } else if (value instanceof JSONObject) {
                    super.put(i, new JSONWrapperObject((JSONObject)value));
                } else {
                    super.put(i, value);
                }
            }
        }
    }

    public JSONArray toJSON() {
        JSONArray json = new JSONArray();

        // NOTE: Extract values directly from the structure to avoid incrementing the visit count
        Map<Integer, JSONWrapperValue> structure = this.getStructure();
        for (int i=0; i<structure.size(); i++) {
            JSONWrapperValue valueWrapper = structure.get(i);
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

            json.put(value);
        }

        return json;
    }

    public JSONWrapperArray copy() {
        JSONWrapperArray json = new JSONWrapperArray();
        json.copyFrom(this);
        return json;
    }

    /**
     * Returns a new {@link JSONWrapperArray} representing the current array (this) overwritten with
     * the values from the overwrites parameter.
     * Both the current object (this) and the overwrites parameter are unmodified.
     * @param overwrites
     * @return
     */
    public JSONWrapperArray overwrite(JSONWrapperArray overwrites) {
        return this.overwrite(overwrites, null);
    }
    public JSONWrapperArray overwrite(JSONWrapperArray overwrites, String idKey) {
        JSONWrapperArray copy = this.copy();
        copy.inPlaceOverwrite(overwrites, idKey);

        return copy;
    }

    @Override
    public boolean equals(Object obj) {
        // Same instance
        if (this == obj) {
            return true;
        }

        if (obj == null || !(obj instanceof JSONWrapperArray)) {
            return false;
        }

        return super.equals(obj);
    }

    @Override
    public String toString(int indentFactor) {
        return this.toJSON().toString(indentFactor);
    }
}
