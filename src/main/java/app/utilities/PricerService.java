package app.utilities;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PricerService extends AbstractVerticle {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(PricerService.class.getName());
    }

    private Map<String, Double> prices = new HashMap<>();
    private Random random = new Random();

    @Override
    public void start() {
        Router router = Router.router(vertx);
        router.get("/prices/:name").handler(rc -> {
            String name = rc.pathParam("name");
            Double price = prices
                .computeIfAbsent(name,
                    k -> (double) random.nextInt(50));
            System.out.println("Getting price for " + name + " : " + price);
            rc.response().end(new JsonObject().put("name", name)
                .put("price", price).encodePrettily());
        });
        
        vertx.createHttpServer()
            .requestHandler(router::accept)
            .listen(8081);
    }
}
