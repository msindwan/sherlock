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
package io.sherlock.core;

import io.sherlock.core.handlers.Search;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.function.Consumer;

/**
 * Server:
 * Defines the entry point for the Sherlock application.
 */
public class Server extends AbstractVerticle {

    public static void main(String[] args) {
        DeploymentOptions deploymentOptions;
        Consumer<Vertx> runner;
        VertxOptions options;
        JsonObject config;

        // Update the cwd.
        System.setProperty("vertx.cwd", "/src/main/java");

        // Parse deployment options.
        deploymentOptions = new DeploymentOptions();
        options = new VertxOptions();
        config = new JsonObject();

        deploymentOptions.setConfig(config);

        // Define and deploy vertx.
        runner = vertx -> {
            try {
                vertx.deployVerticle(
                    Server.class.getName(), deploymentOptions);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        };

        // Pass vertx instance.
        if (options.isClustered()) {
            Vertx.clusteredVertx(options, res -> {
                if (res.succeeded()) {
                    runner.accept(res.result());
                } else {
                    res.cause().printStackTrace();
                }
            });
        } else {
            runner.accept(Vertx.vertx(options));
        }
    }

    @Override
    public void start() {
        // Get server options.
        Boolean cacheEnabled = config().getBoolean("cacheEnabled", true);
        int port = config().getInteger("port", 8080);

        // Route endpoints.
        Router router = Router.router(vertx);
        router.get("/api/search").handler(Search::searchFiles);
        router.get("/api/search/files").handler(Search::listFileFolders);

        // Serve static files from the dist folder.
        StaticHandler staticHandler = StaticHandler.create("dist");
        staticHandler.setCachingEnabled(cacheEnabled);
        router.route().handler(staticHandler);

        // Handle errors.
        router.route().failureHandler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.sendFile("dist/index.html");
        });

        // Start the server.
        vertx.createHttpServer().requestHandler(router::accept).listen(port);
    }
}
