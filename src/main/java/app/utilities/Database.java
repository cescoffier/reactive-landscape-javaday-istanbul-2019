package app.utilities;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.jdbc.JDBCClient;
import io.vertx.reactivex.ext.sql.SQLRowStream;
import io.reactivex.Flowable;
import io.reactivex.Single;

/**
 * @author <a href="http://escoffier.me">Clement Escoffier</a>
 */
public class Database {

    private JDBCClient client;

    private static final String INSERT = "INSERT INTO products (name) VALUES (?)";

    private static final String SELECT_ALL = "SELECT * FROM products";


    public Database(Vertx vertx) {
        client = JDBCClient.createShared(vertx,
            new JsonObject()
                .put("url", "jdbc:hsqldb:mem:test?shutdown=true")
                .put("driver_class", "org.hsqldb.jdbcDriver")
                .put("max_pool_size", 30));

    }

    public static Single<Database> initialize(Vertx vertx) {
        Database database = new Database(vertx);
        return database.client.rxGetConnection()
            .flatMapCompletable(connection ->
                vertx.fileSystem().rxReadFile("ddl.sql")
                    .flatMapPublisher(buffer ->
                        Flowable.fromArray(buffer
                            .toString().split(";")))
                    .flatMapCompletable(connection::rxExecute)
                    .doAfterTerminate(connection::close)
            )
            .andThen(Single.just(database));
    }

    public Flowable<Product> retrieve() {
        return client.rxGetConnection()
            .flatMapPublisher(conn ->
                conn
                    .rxQueryStream(SELECT_ALL)
                    .flatMapPublisher(SQLRowStream::toFlowable)
                    .doAfterTerminate(conn::close)
            )
            .map(Product::new);
    }

    public Single<Product> insert(String product) {
        return client.rxGetConnection()
            .flatMap(conn -> {
                JsonArray params = new JsonArray().add(product);
                return conn
                    .rxUpdateWithParams(INSERT, params)
                    .map(ur -> new Product(product,
                        ur.getKeys().getInteger(0)))
                    .doAfterTerminate(conn::close);
            });

    }

}
