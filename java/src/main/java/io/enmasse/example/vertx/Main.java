package io.enmasse.example.vertx;

import io.vertx.core.Vertx;

import java.io.IOException;
import java.util.Optional;

public class Main {

    public static void main(String [] args) throws IOException {
        String address = Optional.ofNullable(System.getenv("ADDRESS")).orElse("myqueue");
        AppCredentials appCredentials = AppCredentials.create();
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new VertxConsumer(appCredentials, address));
        vertx.deployVerticle(new VertxProducer(appCredentials, address));
    }

}
