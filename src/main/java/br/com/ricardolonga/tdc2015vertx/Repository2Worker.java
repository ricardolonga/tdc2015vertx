package br.com.ricardolonga.tdc2015vertx;

import br.com.ricardolonga.tdc2015vertx.shared.AppVerticle;
import io.vertx.rxcore.java.eventbus.RxEventBus;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

/**
 * Created by longa on 13/05/15.
 */
public class Repository2Worker extends AppVerticle {

    @Override
    public void start() {
        final RxEventBus rxEventBus = new RxEventBus(vertx.eventBus());

        rxEventBus.<JsonObject>registerHandler("obras").subscribe(event -> {
            for (long i = 0; i < 10_000_000_000L; i++) {

            }

            JsonObject obras = new JsonObject();

            obras.putString("type", "obras");
            obras.putArray("obras", new JsonArray().addObject(new JsonObject().putString("bairro", "Centro")).addObject(new JsonObject().putString("bairro", "Itacorubi")));

            event.reply(obras);
        });
    }

}
