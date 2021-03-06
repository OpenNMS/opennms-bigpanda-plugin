/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2020 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2020 The OpenNMS Group, Inc.
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

package org.opennms.integrations.bigpanda;

import org.json.JSONException;
import org.junit.Test;
import org.opennms.integrations.bigpanda.model.Topology;
import org.skyscreamer.jsonassert.JSONAssert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TopologyTest {

    private ObjectMapper mapper = new ObjectMapper();

    /**
     * Verifies that the object is serialized to JSON as expected.
     */
    @Test
    public void canSerializeToJson() throws JsonProcessingException, JSONException {
        Topology topology = new Topology();
        topology.addLink("host", "check");
        topology.addLink("host", "cluster");

        String expectedJson = "{ \n" +
                "      \"links\":[ \n" +
                "         { \n" +
                "            \"source\":\"host\",\n" +
                "            \"target\":\"check\"\n" +
                "         },\n" +
                "         { \n" +
                "            \"source\":\"host\",\n" +
                "            \"target\":\"cluster\"\n" +
                "         }\n" +
                "      ]\n" +
                "}";
        JSONAssert.assertEquals(expectedJson, mapper.writeValueAsString(topology), false);
    }
}
