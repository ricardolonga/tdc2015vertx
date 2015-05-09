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
import com.jetdrone.vertx.yoke.middleware.*;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.platform.Verticle;

/*
This is a simple Java verticle which receives `ping` messages on the event bus and sends back `pong` replies
 */
public class ApplicationServer extends Verticle {

    public void start() {

        new Yoke(vertx)
                .use(new Limit(4096))
                .use(new BasicAuth("admin", "123"))
                .use(new Router().all("/", request -> {
                    request.response().end("Bem vindo!");
                }).get("/hello", new Middleware() {
                    @Override
                    public void handle(YokeRequest request, Handler<Object> handler) {
                        if (request.getParameter("name") == null) {
                            request.response().setStatusCode(400).setContentType("text/plain").end("Faltou o parâmetro 'name'.");
                            return;
                        }
                        
                        handler.handle(null);
                    }
                }, new Middleware() {
                    @Override
                    public void handle(YokeRequest request, Handler<Object> handler) {
                        request.response().setContentType("text/plain").end("Bem vindo " + request.getParameter("name") + "!");
                        return;
                    }
                }))
                .listen(8080);

        container.logger().info("ApplicationServer started");

    }
}
