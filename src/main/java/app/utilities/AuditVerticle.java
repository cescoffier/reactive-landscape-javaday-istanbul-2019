package app.utilities;

import io.vertx.reactivex.core.AbstractVerticle;

public class AuditVerticle extends AbstractVerticle {

    int actionID = 1000;

    @Override
    public void start() throws Exception {
        vertx.eventBus().consumer("audit").toObservable()
            .subscribe(msg -> {
               actionID++;
               System.out.println("[AUDIT] " + msg.body()
                   + "(" + actionID + ")");
               msg.reply(actionID);
            });
    }
}
