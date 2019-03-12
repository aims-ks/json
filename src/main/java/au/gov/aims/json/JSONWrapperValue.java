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

public class JSONWrapperValue {
    private Object value;
    private int count;

    public JSONWrapperValue(Object value) {
        this(value, 0);
    }

    public JSONWrapperValue(Object value, int count) {
        this.value = value;
        this.count = count;
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
