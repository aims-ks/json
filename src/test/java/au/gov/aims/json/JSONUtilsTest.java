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
import org.junit.Assert;
import org.junit.Test;

public class JSONUtilsTest {

    @Test
    public void testEquals() {
        JSONObject obj1 = new JSONObject()
            .put("str", "overwritten")
            .put("int", -8)
            .put("double", 5.624)
            .put("null", JSONObject.NULL)
            .put("child", new JSONObject()
                .put("ver", "1.2")
                .put("array", new JSONArray()
                    .put("a")
                    .put("d")));

        // Same as obj1
        JSONObject obj2 = new JSONObject()
            .put("str", "overwritten")
            .put("int", -8)
            .put("double", 5.624)
            .put("null", JSONObject.NULL)
            .put("child", new JSONObject()
                .put("ver", "1.2")
                .put("array", new JSONArray()
                    .put("a")
                    .put("d")));

        // Property null was removed
        JSONObject obj3 = new JSONObject()
            .put("str", "overwritten")
            .put("int", -8)
            .put("double", 5.624)
            .put("child", new JSONObject()
                .put("ver", "1.2")
                .put("array", new JSONArray()
                    .put("a")
                    .put("d")));

        // Array order reversed
        JSONObject obj4 = new JSONObject()
            .put("str", "overwritten")
            .put("int", -8)
            .put("double", 5.624)
            .put("null", JSONObject.NULL)
            .put("child", new JSONObject()
                .put("ver", "1.2")
                .put("array", new JSONArray()
                    .put("d")
                    .put("a")));

        Assert.assertTrue("obj1 is not equals to obj2", JSONUtils.equals(obj1, obj2));

        Assert.assertFalse("obj1 is equals to obj3", JSONUtils.equals(obj1, obj3));
        Assert.assertFalse("obj1 is equals to obj4", JSONUtils.equals(obj1, obj4));
        Assert.assertFalse("obj2 is equals to obj3", JSONUtils.equals(obj2, obj3));
        Assert.assertFalse("obj2 is equals to obj4", JSONUtils.equals(obj2, obj4));
        Assert.assertFalse("obj3 is equals to obj4", JSONUtils.equals(obj3, obj4));

        // Test commutativity
        Assert.assertTrue("JSONUtils.equals is not commutative; obj2 is not equals to obj1", JSONUtils.equals(obj2, obj1));

        Assert.assertFalse("JSONUtils.equals is not commutative; obj3 is equals to obj1", JSONUtils.equals(obj3, obj1));
        Assert.assertFalse("JSONUtils.equals is not commutative; obj4 is equals to obj1", JSONUtils.equals(obj4, obj1));
        Assert.assertFalse("JSONUtils.equals is not commutative; obj3 is equals to obj2", JSONUtils.equals(obj3, obj2));
        Assert.assertFalse("JSONUtils.equals is not commutative; obj4 is equals to obj2", JSONUtils.equals(obj4, obj2));
        Assert.assertFalse("JSONUtils.equals is not commutative; obj4 is equals to obj3", JSONUtils.equals(obj4, obj3));
    }

    @Test
    public void testOverwriteNull() {
        JSONObject jsonObject = new JSONObject()
            .put("str", "orig")
            .put("null", JSONObject.NULL) // This should stay there
            .put("ver", "1.2")
            .put("bad", "TO REMOVE")
            .put("array", new JSONArray()
                .put("a")
                .put("b")
                .put("c"));

        JSONObject result1 = JSONUtils.overwrites(jsonObject, null);
        Assert.assertNotNull("Overwriting a JSONObject with null returns null", result1);
        Assert.assertTrue("Overwriting a JSONObject with null returns unexpected results", JSONUtils.equals(jsonObject, result1));

        JSONObject result2 = JSONUtils.overwrites(null, jsonObject);
        Assert.assertNotNull("Overwriting null with a JSONObject returns null", result2);
        Assert.assertTrue("Overwriting null with a JSONObject returns unexpected results", JSONUtils.equals(jsonObject, result2));
    }

    @Test
    public void testOverwrite() {
        JSONObject base = new JSONObject()
            .put("str", "orig")
            .put("null", JSONObject.NULL) // This should stay there
            .put("ver", "1.2")
            .put("bad", "TO REMOVE")
            .put("array", new JSONArray()
                .put("a")
                .put("b")
                .put("c"));

        JSONObject overwrites = new JSONObject()
            .put("str", "overwritten")
            .put("bad", JSONObject.NULL) // This is equivalent to put { "bad": null } in the JSON file.
            .put("unexistant", JSONObject.NULL)
            .put("array", new JSONArray()
                .put("a")
                .put("d"));

        JSONObject expected = new JSONObject()
            .put("str", "overwritten")
            .put("null", JSONObject.NULL)
            .put("ver", "1.2")
            .put("array", new JSONArray()
                .put("a")
                .put("d"));

        JSONObject result = JSONUtils.overwrites(base, overwrites);

        //System.out.println("Expected: " + expected.toString(4));
        //System.out.println("Found: " + result.toString(4));

        Assert.assertNotNull("Overwrite result is null", result);

        // Test attribute presence
        Assert.assertTrue("Missing str property", result.has("str"));
        Assert.assertTrue("Missing null property", result.has("null"));
        Assert.assertTrue("Missing ver property", result.has("ver"));
        Assert.assertFalse("Unexpected bad property", result.has("bad"));
        Assert.assertTrue("Missing array property", result.has("array"));

        // Test attribute value
        Assert.assertEquals("Wrong str value", "overwritten", result.optString("str", null));
        Assert.assertEquals("Wrong null value", JSONObject.NULL, result.opt("null"));
        Assert.assertNotNull("Wrong null value (JSONObject.NULL is not the same as null)", result.opt("null"));
        Assert.assertEquals("Wrong ver value", "1.2", result.optString("ver", null));

        JSONArray jsonArray = result.optJSONArray("array");
        Assert.assertEquals("Wrong array length", 2, jsonArray.length());
        Assert.assertEquals("Wrong array first value", "a", jsonArray.optString(0));
        Assert.assertEquals("Wrong array second value", "d", jsonArray.optString(1));

        Assert.assertNotEquals("Overwrites and result are the same, the overwrite method has modified its input", overwrites, result);
        Assert.assertNotEquals("Base and result are the same, the overwrite method has modified its input", base, result);

        Assert.assertTrue("Result is not as expected according to JSONUtils.equals()", JSONUtils.equals(expected, result));
    }
}
