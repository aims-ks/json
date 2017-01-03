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

import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.util.Set;

public class JSONTest {

	@Test
	public void testParseJson() throws Exception {
		String jsonFilePath = "test.json";

		JSONObject jsonObject = null;
		InputStream jsonConfigStream = null;
		try {
			jsonConfigStream = JSONTest.class.getClassLoader().getResourceAsStream(jsonFilePath);
			String jsonConfigStr = JSONUtils.streamToString(jsonConfigStream);
			jsonObject = new JSONObject(jsonConfigStr);
		} finally {
			if (jsonConfigStream != null) {
				try {
					jsonConfigStream.close();
				} catch(Exception ex) {
					ex.printStackTrace();
					Assert.fail("Can not close the input stream.");
				}
			}
		}

		Assert.assertNotNull("The JSON Object is null", jsonObject);

		// Check for property that has never been read
		Set<String> neverVisited = jsonObject.getNeverVisited();
		Assert.assertNotNull("List of never visited attribute is null at the beginning", neverVisited);
		Assert.assertFalse("List of never visited attribute is empty at the beginning", neverVisited.isEmpty());

		// Try to load an object
		JSONObject countries = jsonObject.get(JSONObject.class, "countries");
		Assert.assertNotNull("Countries is null", countries);

		// Visit all attributes of all countries
		for (String countryId : countries.keySet()) {
			// IMPORTANT: The cast in only performed when the value is stored in a variable.
			//   Removing the variables (but keeping the "get") would make the test pointless.
			JSONObject country = countries.get(JSONObject.class, countryId);
			String name       = country.get(String.class, "name");
			String capital    = country.get(String.class, "capital");
			Float area        = country.get(Float.class, "area");
			Double population = country.get(Double.class, "population");
		}

		// Try to load an array
		JSONArray group = jsonObject.get(JSONArray.class, "group");
		Assert.assertNotNull("Group is null", group);

		// Visit all elements of the group
		for (int i=0; i<group.length(); i++) {
			group.get(String.class, i);
		}

		// Try to load a missing property
		String missing = jsonObject.get(String.class, "missing");
		Assert.assertNull("Missing property was found", missing);

		// Try to load a missing property with a default value
		String missingWithDefault = jsonObject.get(String.class, "missing", "Default");
		Assert.assertNotNull("Reading missing property with default value returned null", missingWithDefault);
		Assert.assertEquals("Reading missing property with default didn't returned the default", "Default", missingWithDefault);

		// Try to load a property using the wrong type
		try {
			jsonObject.get(JSONObject.class, "vanished");
			Assert.fail("Getting an attribute using the wrong class should throw an exception");
		} catch (InvalidJSONException ex) {}

		// Check for property that has never been read
		neverVisited = jsonObject.getNeverVisited();
		Assert.assertNotNull("List of never visited attribute is null", neverVisited);
		Assert.assertFalse("List of never visited attribute is empty", neverVisited.isEmpty());

		// Visit the remaining element
		jsonObject.get(String.class, "vanished");

		// The list should now be empty
		neverVisited = jsonObject.getNeverVisited();
		Assert.assertTrue("List of never visited attribute is not empty: " + neverVisited, neverVisited == null || neverVisited.isEmpty());
	}

}
