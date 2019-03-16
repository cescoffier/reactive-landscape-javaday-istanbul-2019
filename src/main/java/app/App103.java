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

import static app.utilities.SockJsHelper.getSockJsHandler;

public class App103 extends AbstractVerticle {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(App103.class.getName());
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
        router.get("/eventbus/*").handler(getSockJsHandler(vertx));
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
        String name = rc.getBodyAsString().trim();
        database.insert(name)
            .flatMap(p -> pricer
                .get("/prices/" + name)
                .rxSend()
                .map(HttpResponse::bodyAsJsonObject)
                .map(json ->
                    p.setPrice(json.getDouble("price"))))
            .subscribe(
                p -> {
                    String json = Json.encode(p);
                    rc.response().setStatusCode(201).end(json);
                    vertx.eventBus().publish("products", json);
                },
                rc::fail);
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
