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
        vertx.eventBus().registerHandler("teste", new Handler<Message>() {
            @Override
            public void handle(Message event) {
                event.reply();
            }
        });
    }
}
