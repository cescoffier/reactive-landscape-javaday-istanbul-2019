package app.utilities;

import io.reactiverse.pgclient.PgPoolOptions;
import io.reactiverse.reactivex.pgclient.PgClient;
import io.reactiverse.reactivex.pgclient.PgPool;
import io.reactiverse.reactivex.pgclient.Tuple;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.vertx.reactivex.core.Vertx;

public class Database {

    private PgPool client;

    private static final String INSERT = "INSERT INTO products (name) VALUES ($1) RETURNING (id)";

    private static final String SELECT_ALL = "SELECT * FROM products";

    public Database(Vertx vertx) {

        PgPoolOptions options = new PgPoolOptions()
                .setPort(5432)
                .setHost(Hosts.getHost())
                .setDatabase("rest-crud")
                .setUser("restcrud")
                .setPassword("restcrud")
                .setMaxSize(50);

        // Create the client pool
        client = PgClient.pool(vertx, options);

    }

    /**
     * INSERT INTO products (name) values ('Apple');
     * INSERT INTO products (name) values ('Orange');
     * INSERT INTO products (name) values ('Pear');
     */

    public static Single<Database> initialize(Vertx vertx) {
        Database database = new Database(vertx);
        return database.client.rxGetConnection()
                .flatMap(connection ->
                        vertx.fileSystem().rxReadFile("ddl.sql")
                                .flatMapPublisher(buffer ->
                                        Flowable.fromArray(buffer
                                                .toString().split(";")))
                                .flatMapSingle(connection::rxQuery)
                                .ignoreElements()
                                .doAfterTerminate(connection::close)
                                .toSingleDefault(connection)
                ).flatMapCompletable(connection ->
                        connection.rxQuery(SELECT_ALL)
                                .flatMapCompletable(set -> {
                                    if (set.rowCount() == 0) {
                                        return connection
                                                .rxQuery("INSERT INTO products (name) values ('Apple')")
                                                .flatMap(x -> connection
                                                        .rxQuery("INSERT INTO products (name) values ('Orange')"))
                                                .flatMap(x -> connection
                                                        .rxQuery("INSERT INTO products (name) values ('Pear')"))
                                                .ignoreElement();
                                    } else {
                                        return Completable.complete();
                                    }
                                })
                ).andThen(Single.just(database));
    }

    public Flowable<Product> retrieve() {
        return client
                .rxBegin()
                .flatMapPublisher(tx -> tx
                        .rxPrepare(SELECT_ALL)
                        .flatMapPublisher(query -> query.createStream(3, Tuple.tuple()).toFlowable())
                        .map(Product::new)
                        .doAfterTerminate(tx::commit)
                );
    }

    public Single<Product> insert(String product) {
        String name = product.trim();
        return client
                .rxPreparedQuery(INSERT, Tuple.of(name))
                .map(set -> set.iterator().next())
                .map(ur -> new Product(name, ur.getInteger("id")));

    }
}
