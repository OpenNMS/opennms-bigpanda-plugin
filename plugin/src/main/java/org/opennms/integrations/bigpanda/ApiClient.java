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

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.opennms.integrations.bigpanda.model.Alert;
import org.opennms.integrations.bigpanda.model.Topology;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ApiClient {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final OkHttpClient client;
    private final ObjectMapper mapper = new ObjectMapper();
    private String url;
    private String accessToken;

    public ApiClient(String url, String accessToken) {
        this.url = Objects.requireNonNull(url);
        this.accessToken = Objects.requireNonNull(accessToken);
        this.client = new OkHttpClient();
    }

    public CompletableFuture<Void> sendAlert(Alert alert) {
        return doPost(url, alert);
    }

    public CompletableFuture<Void> forwardTopology(Topology topology) {
        return doPost(url, topology);
    }

    public CompletableFuture<Void> ping() {
        return doGet(url);
    }

    private CompletableFuture<Void> doPost(String url, Object requestBodyPayload) {
        RequestBody body;
        try {
            body = RequestBody.create(JSON, mapper.writeValueAsString(requestBodyPayload));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("User-Agent", ApiClient.class.getCanonicalName())
                .post(body)
                .build();
        return doAsync(request);
    }

    private CompletableFuture<Void> doGet(String url) {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("User-Agent", ApiClient.class.getCanonicalName())
                .get()
                .build();
        return doAsync(request);
    }

    private CompletableFuture<Void> doAsync(Request request) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        future.completeExceptionally(e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) {
                        try {
                            if (!response.isSuccessful()) {
                                String bodyPayload = "(empty)";
                                ResponseBody body = response.body();
                                if (body != null) {
                                    try {
                                        bodyPayload = body.string();
                                    } catch (IOException e) {
                                        // pass
                                    }
                                    body.close();
                                }
                                future.completeExceptionally(new Exception("Request to URL: " + request.url() +
                                        " failed with response code: " + response.code() +
                                        " and body: " + bodyPayload));
                            } else {
                                future.complete(null);
                            }
                        } finally {
                            response.close();
                        }
                    }
                });
        return future;
    }


}
