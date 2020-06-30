package com.embibe.userAuth.utils;

import com.embibe.userAuth.App;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;

public class HttpUtils<T> {
    public static WebClient client;

    static {
        WebClientOptions webClientOptions = new WebClientOptions()
                .setMaxPoolSize(1000)
                .setIdleTimeout(5)
                .setKeepAlive(false)
                .setMaxWaitQueueSize(-1);
        client = WebClient.create(App.vertx, webClientOptions);
    }

    public static <T> Promise<T> post(String url, Class<T> clazz, JsonObject reqBody) {
        Promise promise = Promise.promise();
        Long start = System.currentTimeMillis();
        client.postAbs(url)
                .putHeader("Content-Type", "application/json")
                .sendJsonObject(reqBody, handler -> {
                    System.out.println("time: " + (System.currentTimeMillis() - start));
                    if (handler.succeeded()) {
                        if (200 != handler.result().statusCode()) {
                            promise.fail(new Exception("Failed to get details"));
                            return;
                        }
                        HttpResponse<Buffer> response = handler.result();
                        T t;
                        if (String.class.equals(clazz)) {
                            t = (T) response.bodyAsString();
                        } else {
                            t = response.bodyAsJson(clazz);
                        }
                        promise.complete(t);
                    } else {
                        promise.fail(new Exception("Failed to get details"));
                        handler.cause().printStackTrace();
                    }
                });
        return promise;
    }
}