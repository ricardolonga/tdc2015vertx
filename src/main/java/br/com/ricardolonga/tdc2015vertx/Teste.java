package br.com.ricardolonga.tdc2015vertx;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.platform.Verticle;

/**
 * Created by longa on 13/05/15.
 */
public class Teste extends Verticle {

    @Override
    public void start() {
        vertx.eventBus().registerHandler("teste", event -> {
            for(long i = 0; i < 1_000_000_000L; i++) {

            }

            event.reply(event.body());
        });

        vertx.eventBus().registerHandler("teste2", event -> {
            for(long i = 0; i < 2_000_000_000L; i++) {

            }

            event.reply(event.body());
        });
    }

}
