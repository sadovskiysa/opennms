/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2017-2017 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2017 The OpenNMS Group, Inc.
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

package org.opennms.netmgt.syslogd;

import java.nio.ByteBuffer;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.opennms.netmgt.model.events.EventBuilder;

/**
 * The state of the entire parse operation. This state
 * should include all of the finished tokens generated
 * by {@link ParserStage} operations.
 */
public class ParserState {
	private final ByteBuffer buffer;

	// TODO: Replace with a strategy
	public final EventBuilder builder;

	public ParserState(ByteBuffer input) {
		this(input, new EventBuilder());
	}

	public ParserState(ByteBuffer input, EventBuilder builder) {
		this.buffer = input;
		this.builder = builder;
	}

	public ByteBuffer getBuffer() {
		// TODO: See if this slows anything down
		return buffer.asReadOnlyBuffer();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("builder", builder)
			.toString();
	}
}