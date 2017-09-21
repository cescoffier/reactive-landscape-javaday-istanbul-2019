package demo;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;

public class Example102 extends AbstractVerticle {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(Example102.class.getName());
    }

    @Override
    public void start() throws Exception {
        vertx.createHttpServer()
            .requestHandler(req ->
                req.response().end(Thread.currentThread().getName())
            )
            .listen(8080, hopefullySuccessful -> {
                if (hopefullySuccessful.succeeded()) {
                    System.out.println("server started");
                } else {
                    System.out.println("D'oh !");
                }
            });

    }
}
