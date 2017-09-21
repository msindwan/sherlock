package io.sherlock.core;

import io.sherlock.core.handlers.Search;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.function.Consumer;

public class Server extends AbstractVerticle {

    public static void main(String[] args) {
        System.setProperty("vertx.cwd", "/src/main/java");
        VertxOptions options = new VertxOptions();
        Consumer<Vertx> runner = vertx -> {
            try {
                vertx.deployVerticle(Server.class.getName());
            } catch (Throwable t) {
                t.printStackTrace();
            }
        };
        if (options.isClustered()) {
            Vertx.clusteredVertx(options, res -> {
                if (res.succeeded()) {
                    Vertx vertx = res.result();
                    runner.accept(vertx);
                } else {
                    res.cause().printStackTrace();
                }
            });
        } else {
            Vertx vertx = Vertx.vertx(options);
            runner.accept(vertx);
        }
    }

    @Override
    public void start() {
        Router router = Router.router(vertx);
        router.get("/search").handler(Search::searchFiles);
        router.route().handler(StaticHandler.create("dist"));
        vertx.createHttpServer().requestHandler(router::accept).listen(8080);
    }
}
