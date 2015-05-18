package br.com.ricardolonga.tdc2015vertx;
/*
 * Copyright 2013 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */

import com.jetdrone.vertx.yoke.Middleware;
import com.jetdrone.vertx.yoke.Yoke;
import com.jetdrone.vertx.yoke.middleware.BasicAuth;
import com.jetdrone.vertx.yoke.middleware.Limit;
import com.jetdrone.vertx.yoke.middleware.Router;
import com.jetdrone.vertx.yoke.middleware.YokeRequest;
import io.vertx.rxcore.RxSupport;
import io.vertx.rxcore.java.eventbus.RxEventBus;
import io.vertx.rxcore.java.eventbus.RxMessage;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;
import rx.Observable;
import rx.util.functions.Action1;
import rx.util.functions.Func1;

/*
This is a simple Java verticle which receives `ping` messages on the event bus and sends back `pong` replies
 */
public class ApplicationServer extends AppVerticle {

    public void start() {

        final RxEventBus rxEventBus = new RxEventBus(vertx.eventBus());

        deployWorkerVerticle(Teste.class, 20);




        // setup Routematcher
        RouteMatcher matcher = new RouteMatcher();



        // the matcher for the update
        matcher.get("/rx", new Handler<HttpServerRequest>() {
            public void handle(final HttpServerRequest req) {
                // first access the buffer as an observable. We do this this way, since
                // we want to keep using the matchhandler and we can't do that with rxHttpServer
                Observable<Buffer> reqDataObservable = RxSupport.toObservable(req);

                // after we have the body, we update the element in the database
                Observable<RxMessage<JsonObject>> updateObservable = reqDataObservable.flatMap(new Func1<Buffer, Observable<RxMessage<JsonObject>>>() {
                    @Override
                    public Observable<RxMessage<JsonObject>> call(Buffer buffer) {
                        System.out.println("buffer = " + buffer);
                        // create the message
                        JsonObject newObject = new JsonObject(buffer.getString(0, buffer.length()));
                        JsonObject matcher = new JsonObject().putString("_id", req.params().get("id"));
                        JsonObject json = new JsonObject().putString("collection", "zips")
                                .putString("action", "update")
                                .putObject("criteria", matcher)
                                .putBoolean("upsert", false)
                                .putBoolean("multi", false)
                                .putObject("objNew", newObject);

                        // and return an observable
                        return rxEventBus.send("teste", json);
                    }
                });

                // use the previous input again, so we could see whether the update was successful.
                Observable<RxMessage<JsonObject>> getLatestObservable = updateObservable.flatMap(new Func1<RxMessage<JsonObject>, Observable<RxMessage<JsonObject>>>() {
                    @Override
                    public Observable<RxMessage<JsonObject>> call(RxMessage<JsonObject> jsonObjectRxMessage) {
                        System.out.println("jsonObjectRxMessage = " + jsonObjectRxMessage);
                        // next we get the latest version from the database, after the update has succeeded
                        // this isn't dependent on the previous one. It just has to wait till the previous
                        // one has updated the database, but we could check whether the previous one was successfully
                        JsonObject matcher = new JsonObject().putString("_id", req.params().get("id"));
                        JsonObject json2 = new JsonObject().putString("collection", "zips")
                                .putString("action", "find")
                                .putObject("matcher", matcher);
                        return rxEventBus.send("teste", json2);
                    }
                });

                // after we've got the latest version we return this in the response.
                getLatestObservable.subscribe(new Action1<RxMessage<JsonObject>>() {
                    @Override
                    public void call(RxMessage<JsonObject> jsonObjectRxMessage) {
                        req.response().end(jsonObjectRxMessage.body().encodePrettily());
                    }
                });
            }
        });




        // create and run the server
        HttpServer server = vertx.createHttpServer().requestHandler(matcher).listen(8080);

        // output that the server is started
        container.logger().info("Webserver started, listening on port: 8888");













//        new Yoke(vertx)
//                .use(new Limit(4096))
//                .use(new BasicAuth("admin", "123"))
//                .use(new Router().all("/", request -> {
//                    request.response().end("Bem vindo!");
//                }).get("/hello", new Middleware() {
//                    @Override
//                    public void handle(YokeRequest request, Handler<Object> handler) {
//                        if (request.getParameter("name") == null) {
//                            request.response().setStatusCode(400).setContentType("text/plain").end("Faltou o parâmetro 'name'.");
//                            return;
//                        }
//
//                        handler.handle(null);
//                    }
//                }, new Middleware() {
//                    @Override
//                    public void handle(YokeRequest request, Handler<Object> handler) {
//                        request.response().setContentType("text/plain").end("Bem vindo " + request.getParameter("name") + "!");
//                        return;
//                    }
//                }).get("/rx", new Middleware() {
//                    @Override
//                    public void handle(YokeRequest yokeRequest, Handler<Object> handler) {
//                        Observable<Buffer> reqDataObservable = RxSupport.toObservable(yokeRequest);
//
//                        Observable<RxMessage<JsonObject>> updateObservable = reqDataObservable.flatMap(new Func1<Buffer, Observable<RxMessage<JsonObject>>>() {
//                            @Override
//                            public Observable<RxMessage<JsonObject>> call(Buffer buffer) {
//                                JsonObject newObject = new JsonObject(buffer.getString(0, buffer.length()));
//                                JsonObject matcher = new JsonObject().putString("_id", yokeRequest.params().get("id"));
//                                JsonObject json = new JsonObject().putString("collection", "zips")
//                                        .putString("action", "update")
//                                        .putObject("criteria", matcher)
//                                        .putBoolean("upsert", false)
//                                        .putBoolean("multi", false)
//                                        .putObject("objNew", newObject);
//
//                                return rxEventBus.send("teste", json);
//                            }
//                        });
//
//                        Observable<RxMessage<JsonObject>> getLatestObservable = updateObservable.flatMap(new Func1<RxMessage<JsonObject>, Observable<RxMessage<JsonObject>>>() {
//                            @Override
//                            public Observable<RxMessage<JsonObject>> call(RxMessage<JsonObject> jsonObjectRxMessage) {
//                                JsonObject matcher = new JsonObject().putString("_id", yokeRequest.params().get("id"));
//                                JsonObject json2 = new JsonObject().putString("collection", "zips")
//                                        .putString("action", "find")
//                                        .putObject("matcher", matcher);
//
//                                return rxEventBus.send("teste", json2);
//                            }
//                        });
//
//                        getLatestObservable.subscribe(new Action1<RxMessage<JsonObject>>() {
//                            @Override
//                            public void call(RxMessage<JsonObject> jsonObjectRxMessage) {
//                                yokeRequest.response().end(jsonObjectRxMessage.body().encodePrettily());
//                            }
//                        });
//                    }
//                }))
//                .listen(8080);
//
//        container.logger().info("ApplicationServer started");
    }
}
