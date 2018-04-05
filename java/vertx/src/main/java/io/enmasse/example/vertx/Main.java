package io.enmasse.example.vertx;

import io.enmasse.example.common.AppCredentials;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;

import java.io.IOException;
import java.util.Optional;

public class Main {

    public static void main(String [] args) throws Exception {
        String address = Optional.ofNullable(System.getenv("ADDRESS")).orElse("myqueue");
        AppCredentials appCredentials = AppCredentials.create();
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new VertxConsumer(appCredentials, address), res1 -> {
            if (res1.succeeded()) {
                vertx.deployVerticle(new VertxProducer(appCredentials, address), res2 -> {
                    if (res2.failed()) {
                        vertx.close();
                        System.exit(1);
                    }
                });
            } else {
                vertx.close();
                System.exit(1);
            }
        });
    }

}
