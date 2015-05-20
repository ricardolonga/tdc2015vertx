package br.com.ricardolonga.tdc2015vertx.shared;

import io.vertx.rxcore.java.eventbus.RxEventBus;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

public class AppVerticle extends Verticle {

    protected final org.apache.log4j.Logger logger;

    public AppVerticle(){
        logger = org.apache.log4j.Logger.getLogger(this.getClass());
    }

    protected void deployModule(String moduleName, JsonObject config){
        container.deployModule(moduleName, config);
    }

    protected void deployModule(String moduleName, JsonObject config, Handler<AsyncResult<String>> callback){
        container.deployModule(moduleName, config, callback);
    }

    protected void deployWorkerVerticle(Class _class, int workers) {
        deployWorkerVerticle(_class, new JsonObject(), workers);
    }

    protected void deployWorkerVerticle(Class _class, JsonObject config){
        deployWorkerVerticle(_class, config, 1);
    }

    protected void deployWorkerVerticle(Class _class, JsonObject config, int workers){
        container.deployWorkerVerticle(_class.getTypeName(), config, workers);
    }

    protected void deployWorkerVerticle(Class _class, JsonObject config, int workers, Handler<AsyncResult<String>> callback){
        container.deployWorkerVerticle(_class.getTypeName(), config, workers, true, callback);
    }

    protected void deployWorkerVerticle(Class _class, JsonObject config, Handler<AsyncResult<String>> callback){
        deployWorkerVerticle(_class, config, 1, callback);
    }

    protected EventBus registerHandler(String target, Handler<? extends Message> callback){
        return vertx.eventBus().registerHandler(target, callback);
    }

    protected EventBus send(String target, JsonObject jsonObject, long timeout, Handler<AsyncResult<Message<JsonObject>>> callback){
        return vertx.eventBus().sendWithTimeout(target, jsonObject, timeout, callback);
    }
    
}
