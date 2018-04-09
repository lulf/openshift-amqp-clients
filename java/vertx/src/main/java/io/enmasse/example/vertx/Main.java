package io.enmasse.example.vertx;

import io.enmasse.example.common.AppCredentials;
import io.vertx.core.CompositeFuture;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

import java.io.IOException;
import java.util.Optional;

public class Main {

    public static void main(String [] args) throws Exception {
        String address = Optional.ofNullable(System.getenv("ADDRESS")).orElse("myqueue");
        AppCredentials appCredentials = AppCredentials.create();
        Vertx vertx = Vertx.vertx();
        Future<Void> producer = Future.future();
        Future<Void> consumer = Future.future();
        CompositeFuture starts = CompositeFuture.all(producer, consumer);
        vertx.deployVerticle(new VertxConsumer(appCredentials, address), res1 -> {
            if (res1.succeeded()) {
                consumer.complete();
                vertx.deployVerticle(new VertxProducer(appCredentials, address), res2 -> {
                    if (res2.succeeded()) {
                        producer.complete();
                    } else {
                        producer.fail(res2.cause());
                    }
                });
            } else {
                consumer.fail(res1.cause());
            }
        });

        starts.setHandler(result -> {
            System.err.println("Error starting vert.x example");
            try {
                Thread.sleep(10_000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.exit(1);
        });
    }

}
