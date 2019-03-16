package demo;

import hu.akarnokd.rxjava2.math.MathFlowable;
import io.reactivex.Flowable;
import io.vertx.core.Vertx;
import io.vertx.reactivex.core.AbstractVerticle;


public class Example101 extends AbstractVerticle {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(Example101.class.getName());
    }

    @Override
    public void start() {
        Flowable<Integer> obs1 = Flowable.range(1, 10);
        Flowable<Integer> obs2 = obs1.map(i -> i + 1);
        Flowable<Integer> obs3 = obs2.window(2)
            .flatMap(MathFlowable::sumInt);
        obs3.subscribe(
            i -> System.out.println("Computed " + i)
        );
    }
}
