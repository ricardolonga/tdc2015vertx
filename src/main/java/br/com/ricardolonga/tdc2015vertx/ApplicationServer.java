package br.com.ricardolonga.tdc2015vertx;

import br.com.ricardolonga.tdc2015vertx.shared.AppVerticle;
import io.vertx.rxcore.java.eventbus.RxEventBus;
import io.vertx.rxcore.java.eventbus.RxMessage;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.core.json.JsonObject;
import rx.Observable;
import rx.schedulers.Schedulers;

public class ApplicationServer extends AppVerticle {

    public void start() {
        final RxEventBus rxEventBus = new RxEventBus(vertx.eventBus());

        deployWorkerVerticle(RepositoryWorker.class, 20);
        deployWorkerVerticle(Repository2Worker.class, 20);

        RouteMatcher matcher = new RouteMatcher();

        matcher.get("/rx", req -> {
            Observable<RxMessage<JsonObject>> empresa = rxEventBus.send("emp", new JsonObject());

            empresa.forEach(empresaConsultadaJsonObject -> {
                Observable<RxMessage<Object>> obras = rxEventBus.send("obras", new JsonObject()).subscribeOn(Schedulers.io());
                Observable<RxMessage<Object>> funcionarios = rxEventBus.send("func", new JsonObject()).subscribeOn(Schedulers.io());

                Observable.merge(obras, funcionarios)
                        .forEach(objectRxMessage -> {
                            JsonObject next = (JsonObject) objectRxMessage.body();

                            if (next.getString("type").equalsIgnoreCase("func")) {
                                empresaConsultadaJsonObject.body().putArray("funcionarios", next.getArray("funcionarios"));
                            }

                            if (next.getString("type").equalsIgnoreCase("obras")) {
                                empresaConsultadaJsonObject.body().putArray("obras", next.getArray("obras"));
                            }
                        }, error -> {
                            System.out.println(" ############################################ ERROR ############################################ ");
                        }, () -> req.response().end(empresaConsultadaJsonObject.body().encodePrettily()));
            });
        });

        vertx.createHttpServer().requestHandler(matcher).listen(8080);
    }
}
