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

import io.sherlock.common.options.ServerOptions;
import io.sherlock.common.options.InvalidOptionException;

import org.apache.commons.cli.ParseException;

import io.sherlock.core.handlers.Search;
import io.sherlock.common.util.SherlockFile;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.function.Consumer;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

/**
 * Server:
 * Defines the entry point for the Sherlock application.
 */
public class Server extends AbstractVerticle {

    private static Logger logger = Logger.getLogger(Server.class.getName());

    /**
     * Entry point.
     *
     * @param args // The command line arguments.
     */
    public static void main(final String[] args) {
        DeploymentOptions deploymentOptions;
        ServerOptions serverOptions;
        Consumer<Vertx> runner;
        VertxOptions options;
        JsonObject config;

        BasicConfigurator.configure();

        deploymentOptions = new DeploymentOptions();
        serverOptions = new ServerOptions();
        options = new VertxOptions();
        config = new JsonObject();

        // Update the cwd.
        System.setProperty("vertx.cwd", "/src/main/java");

        // Set the configuration options.
        deploymentOptions.setConfig(config);

        try {
            // Check if the help menu needs to be printed.
            if (serverOptions.hasHelpOption(args)) {
                serverOptions.printHelp();
                System.exit(0);
            }

            // Parse and validate arguments.
            serverOptions.parse(args);
            config.put("root", serverOptions.getRoot());
            config.put("indexes", serverOptions.getIndexes());
            config.put("port", serverOptions.getPort());

        } catch (ParseException | InvalidOptionException | NumberFormatException e) {
            // Handle cli exceptions.
            System.err.println(String.format(
                "Failed to process args: %s",
                e.getMessage())
            );
            serverOptions.printHelp();
            System.exit(1);
        }

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
    public final void start() {
        // Get server options.
        final String indexes = config().getString("indexes");
        final String root = config().getString("root");
        final SherlockFile sherlockFile = new SherlockFile();

        try {
            sherlockFile.read(root);
        } catch (IOException e) {
            logger.error(String.format(
                "Failed to read sherlock file: %s",
                e.getMessage()
            ));
        }

        // Route endpoints.
        Router router = Router.router(vertx);
        router.get("/api/search").handler(r -> {
            Search.searchFiles(r, indexes, root);
        });
        router.get("/api/search/file").handler(r -> {
            Search.getFile(r, root, sherlockFile);
        });
        router.get("/api/search/files").handler(r -> {
            Search.listFileFolders(r, root, sherlockFile);
        });

        // Serve static files from the dist folder.
        StaticHandler staticHandler = StaticHandler.create("dist");
        staticHandler.setCachingEnabled(true);
        router.route("/dist/*").handler(staticHandler);

        // Default to the index page.
        router.route().handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.sendFile("dist/index.html");
        });

        // Start the server.
        vertx.createHttpServer()
            .requestHandler(router::accept)
            .listen(config().getInteger("port"));
    }
}
