package br.com.ricardolonga.tdc2015vertx;

import br.com.ricardolonga.tdc2015vertx.shared.AppVerticle;
import io.vertx.rxcore.java.eventbus.RxEventBus;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

/**
 * Created by longa on 13/05/15.
 */
public class RepositoryWorker extends AppVerticle {

    @Override
    public void start() {
        final RxEventBus rxEventBus = new RxEventBus(vertx.eventBus());

        rxEventBus.<JsonObject>registerHandler("emp").subscribe(event -> {
            JsonObject empresa = new JsonObject();
            empresa.putString("id", "123");
            event.reply(empresa);
        });

        rxEventBus.<JsonObject>registerHandler("func").subscribe(event -> {
            for (long i = 0; i < 10_000_000_000L; i++) {

            }

            JsonObject func = new JsonObject();

            func.putString("type", "func");
            func.putArray("funcionarios", new JsonArray().addObject(new JsonObject().putString("name", "Longa")).addObject(new JsonObject().putString("name", "Pizarro")));

            event.reply(func);
        });
    }

}
