/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2011-2014 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2014 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.netmgt.xml.eventconf;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.runners.Parameterized.Parameters;
import org.opennms.core.test.xml.XmlTestNoCastor;

public class MaskelementTest extends XmlTestNoCastor<Maskelement> {

	public MaskelementTest(final Maskelement sampleObject, final String sampleXml, final String schemaFile) {
		super(sampleObject, sampleXml, schemaFile);
	}

	@Parameters
	public static Collection<Object[]> data() throws ParseException {
		Maskelement maskelement0 = new Maskelement();
		maskelement0.setMename("specific");
		maskelement0.addMevalue("3");
		Maskelement maskelement1 = new Maskelement();
		maskelement1.setMename("specific");
		maskelement1.addMevalue("3");
		maskelement1.addMevalue("4");
		return Arrays.asList(new Object[][] {
				{maskelement0,
				"<maskelement> <mename>specific</mename> <mevalue>3</mevalue></maskelement>",
				"target/classes/xsds/eventconf.xsd" }, 
				{maskelement1,
					"<maskelement> <mename>specific</mename> <mevalue>3</mevalue><mevalue>4</mevalue></maskelement>",
					"target/classes/xsds/eventconf.xsd" }, 
		});
	}

}
