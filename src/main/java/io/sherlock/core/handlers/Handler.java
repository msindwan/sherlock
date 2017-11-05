/*
 * Copyright (C) 2017 Mayank Sindwani
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.sherlock.core.handlers;

import io.vertx.ext.web.RoutingContext;
import io.vertx.core.json.JsonObject;

/**
 * Handler:
 * Defines the base handler class.
 */
public final class Handler {

    public static final int HTTP_NOT_FOUND = 404;
    public static final int HTTP_INTERNAL_SERVER_ERROR = 500;

    /**
     * Hidden Constructor.
     */
    private Handler() { }

    /**
     * Send Error
     *
     * Description: Returns an error to the client.
     * @param routingContext // The vertx routing context.
     * @param statusCode     // The status code.
     * @param message        // The error message.
     */
    public static void sendError(
        final RoutingContext routingContext,
        final int statusCode,
        final String message) {

        JsonObject responseObject = new JsonObject();
        responseObject.put("message", message);

        routingContext.response()
            .setStatusCode(statusCode)
            .putHeader("content-type", "application/json")
            .end(responseObject.encode());
    }
}
