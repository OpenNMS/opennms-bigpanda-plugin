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

package org.opennms.integrations.bigpanda.model;

import java.io.IOException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.NONE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Alert {

    /**
     * Required. Application key, created in the first step of a BigPanda tutorial.
     */
    @JsonProperty("app_key")
    private String appKey;

    /**
     * Required. Status of the alert. One of [ ok, critical, warning, acknowledged ].
     */
    @JsonProperty("status")
    @JsonSerialize(using = Status.Serializer.class)
    private Status status;

    /**
     * Main object that caused the alert. Can be the associated host or, if a host isn't relevant, a service or an application.
     * If you want to include more than one of these fields, consider specifying the primary and secondary properties.
     */
    @JsonProperty("host")
    private String host;

    /**
     * (Optional) Time that the alert occurred in Unix format (UTC timezone).
     * If the time is not specified, the value defaults to the current time.
     */
    @JsonProperty("timestamp")
    @JsonSerialize(using = InstantSerializer.class)
    private Instant timestamp;

    /**
     * (Optional) Secondary object or sub-item that caused the alert (often shown as an incident subtitle in BigPanda).
     */
    @JsonProperty("check")
    private String check;

    /**
     * (Optional) Brief summary (max. 2048 characters) of the alert for certain monitoring tools.
     */
    @JsonProperty("description")
    private String description;

    /**
     * (Optional) Server cluster or logical host-group from which the alert was sent.
     * This value is used to correlate alerts into high-level incidents.
     */
    @JsonProperty("cluster")
    private String cluster;

    /**
     * (Optional) Additional information you want to have available in BigPanda.
     * You can add any number of custom JSON attributes with a string value to the payload.
     */
    private Map<String,String> attributes = new LinkedHashMap<>();

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getCheck() {
        return check;
    }

    public void setCheck(String check) {
        this.check = check;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    @JsonAnyGetter
    public Map<String, String> getAttributes() {
        return attributes;
    }

    @JsonAnySetter
    public void setAttribute(String name, String value) {
        attributes.put(name, value);
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Alert alert = (Alert) o;
        return Objects.equals(appKey, alert.appKey) &&
                Objects.equals(status, alert.status) &&
                Objects.equals(host, alert.host) &&
                Objects.equals(timestamp, alert.timestamp) &&
                Objects.equals(check, alert.check) &&
                Objects.equals(description, alert.description) &&
                Objects.equals(cluster, alert.cluster) &&
                Objects.equals(attributes, alert.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appKey, status, host, timestamp, check, description, cluster, attributes);
    }

    @Override
    public String toString() {
        return "Alert{" +
                "appKey='" + appKey + '\'' +
                ", status='" + status + '\'' +
                ", host='" + host + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", check='" + check + '\'' +
                ", description='" + description + '\'' +
                ", cluster='" + cluster + '\'' +
                ", attributes=" + attributes +
                '}';
    }

    public enum Status {
        OK,
        CRITICAL,
        WARNING,
        ACKNOWLEDGED;

        public static class Serializer extends StdSerializer<Status> {
            protected Serializer() {
                super(Status.class);
            }

            @Override
            public void serialize(Status status, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                jsonGenerator.writeString(status.name().toLowerCase());
            }
        }
    }

    public static class InstantSerializer extends StdSerializer<Instant> {
        protected InstantSerializer() {
            super(Instant.class);
        }

        @Override
        public void serialize(Instant instant, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeNumber(instant.getEpochSecond());
        }
    }
}
