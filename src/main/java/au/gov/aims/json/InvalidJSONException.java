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
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package au.gov.aims.json;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

public class InvalidJSONException extends IOException {
	private File jsonFile;
	private Set<String> neverVisited;

	public InvalidJSONException(String message) {
		super(message);
		this.init();
	}

	public InvalidJSONException(String message, Throwable cause) {
		super(message, cause);
		this.init();
	}

	private void init() {
		this.neverVisited = new TreeSet<String>();
	}


	public File getJSONFile() {
		return this.jsonFile;
	}

	public void setJSONFile(File jsonFile) {
		this.jsonFile = jsonFile;
	}

	public void addNeverVisited(String neverVisitedElement) {
		this.neverVisited.add(neverVisitedElement);
	}

	public void addAllNeverVisited(Collection<String> neverVisitedElements) {
		this.neverVisited.addAll(neverVisitedElements);
	}

	public Set<String> getNeverVisited() {
		return this.neverVisited;
	}


	@Override
	public String getMessage() {
		StringBuilder msgSb = new StringBuilder(super.getMessage());

		if (this.jsonFile != null) {
			msgSb.append(System.lineSeparator()).append("JSON file: ").append(this.jsonFile.getAbsolutePath());
		}

		if (this.neverVisited != null && !this.neverVisited.isEmpty()) {
			for (String neverVisitedElement : this.neverVisited) {
				msgSb.append(System.lineSeparator()).append("Invalid attribute: ").append(neverVisitedElement);
			}
		}

		return msgSb.toString();
	}
}
