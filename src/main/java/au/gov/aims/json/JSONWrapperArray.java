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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JSONWrapperArray extends JSONWrapperAbstract<Integer> {
    private JSONArray jsonArray;

    public JSONWrapperArray(String jsonArrayStr) {
        this(new JSONArray(jsonArrayStr), null);
    }

    public JSONWrapperArray(JSONArray jsonArray) {
        this(jsonArray, null);
    }

    protected JSONWrapperArray(JSONArray jsonArray, String path) {
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
        if (JSONObject.class.equals(valueClass)) {
            return JSONWrapperObject.class;
        }
        if (JSONArray.class.equals(valueClass)) {
            return JSONWrapperArray.class;
        }

        return valueClass;
    }

    public <T> T get(Class<T> type, int index, T defaultValue) throws InvalidJSONException {
        T value = this.get(type, index);
        return (value == null ? defaultValue : value);
    }
    public <T> T get(Class<T> type, int index) throws InvalidJSONException {
        if (index < 0 || this.jsonArray.length()-1 < index) {
            return null;
        }

        if (JSONObject.class.equals(type)) {
            throw new IllegalArgumentException("Illegal class 'org.json.JSONObject'. Use 'au.gov.aims.json.JSONWrapperObject' instead.");
        }
        if (JSONArray.class.equals(type)) {
            throw new IllegalArgumentException("Illegal class 'org.json.JSONArray'. Use 'au.gov.aims.json.JSONWrapperArray' instead.");
        }

        Object rawValue = this.jsonArray.opt(index);

        return super.getValueAndCountVisit(type, index, rawValue);
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
    public boolean equals(Object obj) {
        if (!(obj instanceof JSONWrapperArray)) {
            return false;
        }

        return JSONUtils.equals(this.jsonArray, ((JSONWrapperArray)obj).jsonArray);
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
