package app.utilities;

import io.vertx.core.json.JsonArray;

/**
 * @author <a href="http://escoffier.me">Clement Escoffier</a>
 */
public class Product {

    private final Integer id;
    private final String name;

    private double price;

    public Product(JsonArray objects) {
        this.id = objects.getInteger(0);
        this.name = objects.getString(1);
    }

    public Product(String product, int id) {
        this.id = id;
        this.name = product;
    }

    public Product setPrice(double price) {
        this.price = price;
        return this;
    }

    public String getName() {
        return name;
    }

    public Integer getId() {
        return id;
    }

    public double getPrice() {
        return price;
    }


}
