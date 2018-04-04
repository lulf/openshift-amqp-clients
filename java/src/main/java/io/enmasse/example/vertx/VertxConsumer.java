package io.enmasse.example.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.proton.*;
import org.apache.qpid.proton.amqp.messaging.AmqpValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VertxConsumer extends AbstractVerticle {
    private static final Logger log = LoggerFactory.getLogger(VertxConsumer.class);
    private final AppCredentials credentials;
    private final String address;

    public VertxConsumer(AppCredentials credentials, String address) {
        this.credentials = credentials;
        this.address = address;
    }

    @Override
    public void start(Future<Void> startPromise) {
        ProtonClient client = ProtonClient.create(vertx);
        client.connect(credentials.getHostname(), credentials.getPort(), credentials.getUsername(), credentials.getPassword(), connection -> {
            if (connection.succeeded()) {
                log.info("Connected to {}:{}", credentials.getHostname(), credentials.getPort());
                ProtonConnection connectionHandle = connection.result();
                connectionHandle.open();

                ProtonReceiver receiver = connectionHandle.createReceiver(address);
                receiver.handler((protonDelivery, message) -> {
                    String payload = (String) ((AmqpValue)message.getBody()).getValue();
                    log.info("Received '{}'", payload);
                });
                receiver.openHandler(link -> {
                    if (link.succeeded()) {
                        log.info("Receiver attached to '{}'", address);
                        startPromise.complete();
                    } else {
                        log.info("Error attaching to {}", address, link.cause());
                        startPromise.fail(link.cause());
                    }
                });
                receiver.closeHandler(link -> log.info("Receiver for {} closed", address));
                receiver.open();
            } else {
                log.info("Error connecting to {}:{}", credentials.getHostname(), credentials.getPort());
                startPromise.fail(connection.cause());
            }
        });
    }
}
