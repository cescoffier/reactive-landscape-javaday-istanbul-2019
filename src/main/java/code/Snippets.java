package code;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.reactivex.core.Context;
import io.vertx.reactivex.core.RxHelper;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.ext.web.client.WebClient;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="http://escoffier.me">Clement Escoffier</a>
 */
public class Snippets {

    public void subscription() {
        Flowable<Integer> observable = Flowable.range(0, 5);
        Single<String> single = Single.fromCallable(() -> "hello");
        Completable completable = Completable.fromAction(() -> {
        });

        observable.subscribe(
            val -> { /* New value */ },
            error -> { /* failure */ },
            () -> { /* End of data */ }
        );

        single.subscribe(
            val -> { /* the value */ },
            error -> { /* failure */ }
        );

        completable.subscribe(
            () -> { /* completed */ },
            error -> { /* failure */ }
        );

    }

    public void client() {
        WebClient client = null;
        Vertx vertx = null;
        Context context = null;

        client
            .get("/products")
            .rxSend()
            .subscribeOn(RxHelper.scheduler(context))
            .timeout(5, TimeUnit.SECONDS)
            .retry(1)
            .map(HttpResponse::bodyAsString)
            .onErrorReturn(t -> "")
            .subscribe(
                System.out::println
            );

    }

    Vertx vertx;
    List<String> list;

    public void web() {
        vertx.createHttpServer()
            .requestHandler(req ->
                req.response().end(Json.encode(list)))
            .listen(8080, hopefullySuccessful -> {
                if (hopefullySuccessful.succeeded()) {
                    System.out.println("server started");
                } else {
                    System.out.println("D'oh !");
                }
            });
    }
}
