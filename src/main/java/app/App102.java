package app;

import app.utilities.Database;
import io.vertx.core.json.Json;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.http.HttpServerResponse;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import io.vertx.reactivex.ext.web.handler.StaticHandler;

public class App102 extends AbstractVerticle {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(App102.class.getName());
    }

    private Database database;
    private WebClient pricer;

    @Override
    public void start() {
        pricer = WebClient.create(vertx, new WebClientOptions()
                .setDefaultHost("localhost")
                .setDefaultPort(8081)
        );

        Router router = Router.router(vertx);
        router.get("/assets/*").handler(StaticHandler.create());
        router.get("/products").handler(this::list);
        router.route().handler(BodyHandler.create());
        router.post("/products").handler(this::add);

        Database.initialize(vertx)
                .flatMap(db -> {
                    database = db;
                    return vertx.createHttpServer()
                            .requestHandler(router)
                            .rxListen(8080);
                }).subscribe();
    }

    private void add(RoutingContext rc) {
        String name = rc.getBodyAsString();
        database.insert(name)
                .subscribe(
                        p -> rc.response().setStatusCode(201)
                                .end(Json.encode(p)),
                        rc::fail
                );
    }

    private void list(RoutingContext rc) {
        HttpServerResponse response = rc.response().setChunked(true);
        database.retrieve()
                .flatMapSingle(p ->
                        pricer
                                .get("/prices/" + p.getName())
                                .rxSend()
                                .map(HttpResponse::bodyAsJsonObject)
                                .map(json ->
                                        p.setPrice(json.getDouble("price")))
                )
                .subscribe(
                        p -> response.write(Json.encode(p) + " \n\n"),
                        rc::fail,
                        response::end);
    }

}
