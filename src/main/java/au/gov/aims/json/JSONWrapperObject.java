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

import java.util.Set;

public class JSONWrapperObject extends JSONWrapperAbstract<String> {
    private JSONObject jsonObject;

    public JSONWrapperObject(String jsonObjectStr) {
        this(new JSONObject(jsonObjectStr), null);
    }

    public JSONWrapperObject(JSONObject jsonObject) {
        this(jsonObject, null);
    }

    protected JSONWrapperObject(JSONObject jsonObject, String path) {
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
        if (JSONObject.class.equals(valueClass)) {
            return JSONWrapperObject.class;
        }
        if (JSONArray.class.equals(valueClass)) {
            return JSONWrapperArray.class;
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

        if (JSONObject.class.equals(type)) {
            throw new IllegalArgumentException("Illegal class 'org.json.JSONObject'. Use 'au.gov.aims.json.JSONWrapperObject' instead.");
        }
        if (JSONArray.class.equals(type)) {
            throw new IllegalArgumentException("Illegal class 'org.json.JSONArray'. Use 'au.gov.aims.json.JSONWrapperArray' instead.");
        }

        Object rawValue = this.jsonObject.opt(key);

        return super.getValueAndCountVisit(type, key, rawValue);
    }

    /**
     * Returns a new {@link JSONWrapperObject} representing the current object (this) overwritten with
     * the values from the overwrites parameter.
     * Both the current object (this) and the overwrites parameter are unmodified.
     * @param overwrites
     * @return
     */
    public JSONWrapperObject overwrite(JSONWrapperObject overwrites) {
        return new JSONWrapperObject(
            JSONUtils.overwrite(this.jsonObject, overwrites.jsonObject),
            super.path
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof JSONWrapperObject)) {
            return false;
        }

        return JSONUtils.equals(this.jsonObject, ((JSONWrapperObject)obj).jsonObject);
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
