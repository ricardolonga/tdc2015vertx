package br.com.ricardolonga.tdc2015vertx;

import io.vertx.rxcore.java.eventbus.RxEventBus;
import io.vertx.rxcore.java.eventbus.RxMessage;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.core.json.JsonObject;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class ApplicationServer extends AppVerticle {

    private static long count;

    public void start() {
        final RxEventBus rxEventBus = new RxEventBus(vertx.eventBus());

        deployWorkerVerticle(Teste.class, 20);

//        VertxExecutor executor = new VertxExecutor(vertx);
//        When when = WhenFactory.createFor(() -> executor);
//        WhenEventBus whenEventBus = new DefaultWhenEventBus(vertx.eventBus(), when);

        RouteMatcher matcher = new RouteMatcher();

        matcher.get("/rx", req -> {
//            List<Promise<Message<JsonObject>>> promises = new ArrayList<>();
//
//            promises.add(whenEventBus.<JsonObject>send("teste2", new JsonObject().putString("message", "hello")));
//            promises.add(whenEventBus.<JsonObject>send("teste", new JsonObject().putString("message", "world")));
//
//            when.all(promises).then(
//                    replies -> {
//                        System.out.println(replies.toString());
//                        req.response().end(replies.toString());
//                        return null;
//                    },
//                    t -> {
//                        System.out.println(t.toString());
//                        return null;
//                    });


//            Observable.merge(Observable.create((Subscriber<? super Integer> s) -> {
//                // simulate latency
//                try {
//                    Thread.sleep(5000);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                s.onNext(1);
//                s.onCompleted();
//            }).subscribeOn(Schedulers.io()), Observable.create((Subscriber<? super Integer> s) -> {
//                // simulate latency
//                try {
//                    Thread.sleep(5000);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                s.onNext(2);
//                s.onCompleted();
//            }).subscribeOn(Schedulers.io())).forEach(n -> {
//                System.out.println("onNext..." + n.toString());
//            }, e -> {
//                System.out.println("onError..." + e.toString());
//            }, () -> {
//                System.out.println("onComplete...");
//                req.response().end();
//            });


            Observable<HttpServerRequest> from = Observable.from(req);

            Observable<RxMessage<JsonObject>> teste = from.flatMap(buffer -> rxEventBus.send("teste2", new JsonObject()));
            Observable<RxMessage<JsonObject>> teste2 = from.flatMap(buffer -> rxEventBus.send("teste", new JsonObject()));

            Observable.merge(teste.subscribeOn(Schedulers.io()), teste2.subscribeOn(Schedulers.io())).forEach(n -> System.out.println("onNext..." + n.toString()), e -> System.out.println("onError..." + e.toString()), () -> {
                System.out.println("onComplete...");
                req.response().end();
            });


//            /*
//            *
//             */
//            count++;
//
//            Observable<HttpServerRequest> from = Observable.from(req);
//
//            Observable<RxMessage<JsonObject>> updateObservable = from.flatMap(buffer -> {
//                System.out.println("buffer = " + buffer);
//
//                if (count % 2 == 0)
//                    return rxEventBus.send("teste", new JsonObject());
//
//                return rxEventBus.send("teste2", new JsonObject());
//            });
//
//            updateObservable.subscribe(jsonObjectRxMessage -> {
//                req.response().end(jsonObjectRxMessage.body().encodePrettily());
//            });
        });

        vertx.createHttpServer().requestHandler(matcher).listen(8080);
    }
}
