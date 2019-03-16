package app.utilities;

import io.vertx.core.Handler;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.handler.sockjs.SockJSHandler;

public class SockJsHelper {

    public static Handler<RoutingContext> getSockJsHandler(Vertx vertx) {
        SockJSHandler sockJSHandler = SockJSHandler
            .create(vertx);
        BridgeOptions options = new BridgeOptions();
        options.addInboundPermitted(
            new PermittedOptions().setAddress("products"));
        options.addOutboundPermitted(
            new PermittedOptions().setAddress("products"));
        sockJSHandler.bridge(options);
        return sockJSHandler;
    }
}
