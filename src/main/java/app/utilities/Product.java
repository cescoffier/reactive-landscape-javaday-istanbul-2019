package app.utilities;

import io.reactiverse.reactivex.pgclient.Row;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class Product {

    private final Integer id;
    private final String name;

    private double price;

    public Product(String product, int id) {
        this.id = id;
        this.name = product;
    }

    public Product(Row row) {
        this.id = row.getInteger(0);
        String theName = row.getString(1);
        if (theName.endsWith("\n")) {
            theName = theName.substring(0, theName.length() -1);
        }
        this.name = theName;
    }

    public JsonObject toJson() {
        return new JsonObject().put("id", id).put("name", name).put("price", price);
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
