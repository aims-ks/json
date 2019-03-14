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

public class JSONWrapperValue {
    private Object value;
    private int count;

    public JSONWrapperValue(Object value) {
        this(value, 0);
    }

    public JSONWrapperValue(Object value, int count) {
        this.setValue(value);
        this.count = count;
    }

    private void setValue(Object value) {
        if (value == null) {
            this.value = null;
        } else {
            if (
                (value instanceof String) ||
                (value instanceof Integer) ||
                (value instanceof Long) ||
                (value instanceof Float) ||
                (value instanceof Double) ||
                (value instanceof Boolean) ||
                (value instanceof JSONWrapperObject) ||
                (value instanceof JSONWrapperArray)
            ) {
                this.value = value;

            } else if (value instanceof JSONObject) {
                this.value = new JSONWrapperObject((JSONObject)value);
            } else if (value instanceof JSONArray) {
                this.value = new JSONWrapperArray((JSONArray)value);

            } else if (value instanceof Map) {
                this.value = new JSONWrapperObject((Map)value);
            } else if (value instanceof Collection) {
                this.value = new JSONWrapperArray((Collection)value);

            } else {
                this.value = value.toString();
            }
        }
    }

    public Object getValue() {
        return this.value;
    }

    public int getCount() {
        return this.count;
    }

    public void incrementCount() {
        this.incrementCount(1);
    }

    public void incrementCount(int increment) {
        this.count += increment;
    }
}
