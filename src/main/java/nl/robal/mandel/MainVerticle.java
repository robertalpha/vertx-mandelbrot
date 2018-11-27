package nl.robal.mandel;

import io.vertx.core.*;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.handler.BodyHandler;
import nl.robal.mandel.utility.Util;

import static nl.robal.mandel.utility.Util.debug;

public class MainVerticle extends AbstractVerticle {

    private static final int PORT = 8050;
    private static final String BASE_PATH = "app/";
    private static final String WELCOME_PAGE = BASE_PATH + "index.html";

    @Override
    public void start(Future startFuture) {

        VertxOptions options = new VertxOptions();
        options.setMaxWorkerExecuteTime(VertxOptions.DEFAULT_MAX_WORKER_EXECUTE_TIME * 12);

        debug("[Main] Running in " + Thread.currentThread().getName());

        vertx.createHttpServer(new HttpServerOptions().setPort(PORT)).requestHandler(getRouter(vertx)::accept).listen();

        // Start at least one single mandelbrot verticle... or 8:
        vertx.deployVerticle("nl.robal.mandel.MandelBrotVerticle",
                new DeploymentOptions().
                        setMultiThreaded(true)
                        .setWorker(true)
                        .setInstances(8)
                , h -> {
                    if (h.failed()) {
                        Util.debug("MandelBrotVerticle failed to start");
                    }
                    if (h.succeeded()) {
                        Util.debug("Application started, running on: http://localhost:" + PORT);
                    }
                });

        startFuture.complete();
    }

    private static io.vertx.ext.web.Router getRouter(Vertx vertx) {
        io.vertx.ext.web.Router router = io.vertx.ext.web.Router.router(vertx);

        router.route("/api/*").handler(BodyHandler.create().setMergeFormAttributes(false));
        router.route(HttpMethod.GET, "/").handler(req -> req.response().sendFile(WELCOME_PAGE));
        router.routeWithRegex(HttpMethod.GET, "^\\/(app)\\/.*").handler(routingContext -> {
            final String filename = routingContext.normalisedPath().substring(1);
            routingContext.response().sendFile(filename);
        });

        router.route(HttpMethod.POST, "/api/mandelbrotpart").handler(
                rc -> vertx.eventBus().send("mandelbrot.imagerequest", rc.getBodyAsJson(),
                        result -> {
                            if (result.failed()) {
                                Util.debug("Exception:" + result.cause().getMessage());
                            } else {
                                JsonObject body = new JsonObject(result.result().body().toString());
                                Util.debug("status response from verticle:" + body.getString("status"));
                                rc.response().end(body.toString());
                            }
                        }));

        return router;
    }


}
