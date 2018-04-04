package io.enmasse.example.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.net.JksOptions;
import io.vertx.core.net.PemKeyCertOptions;
import io.vertx.proton.ProtonClient;
import io.vertx.proton.ProtonClientOptions;
import io.vertx.proton.ProtonConnection;
import io.vertx.proton.ProtonSender;
import org.apache.qpid.proton.Proton;
import org.apache.qpid.proton.amqp.messaging.AmqpValue;
import org.apache.qpid.proton.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;

public class VertxProducer extends AbstractVerticle {
    private static final Logger log = LoggerFactory.getLogger(VertxProducer.class);
    private final AppCredentials credentials;
    private final String address;
    private final int timerInterval = 2000;
    private final AtomicLong counter = new AtomicLong(0);

    public VertxProducer(AppCredentials credentials, String address) {
        this.credentials = credentials;
        this.address = address;
    }

    @Override
    public void start(Future<Void> startPromise) {
        ProtonClient client = ProtonClient.create(vertx);

        ProtonClientOptions options = new ProtonClientOptions();
        if (credentials.getX509Certificate().exists()) {
            options.setPemKeyCertOptions(new PemKeyCertOptions()
                    .addCertPath(credentials.getX509Certificate().getAbsolutePath()))
                .setSsl(true)
                .setHostnameVerificationAlgorithm("");
        } else if (credentials.getJks().exists()) {
            options.setKeyStoreOptions(new JksOptions()
                    .setPath(credentials.getJks().getAbsolutePath()))
                .setSsl(true)
                .setHostnameVerificationAlgorithm("");
        }

        client.connect(options, credentials.getHostname(), credentials.getPort(), credentials.getUsername(), credentials.getPassword(), connection -> {
            if (connection.succeeded()) {
                log.info("Connected to {}:{}", credentials.getHostname(), credentials.getPort());
                ProtonConnection connectionHandle = connection.result();
                connectionHandle.open();

                ProtonSender sender = connectionHandle.createSender(address);
                sender.openHandler(link -> {
                    if (link.succeeded()) {
                        log.info("Sender attached to '{}'", address);
                        startPromise.complete();
                        vertx.setTimer(timerInterval, id -> sendNext(sender));
                    } else {
                        log.info("Error attaching to {}", address, link.cause());
                        startPromise.fail(link.cause());
                    }
                });
                sender.open();
            } else {
                log.info("Error connecting to {}:{}", credentials.getHostname(), credentials.getPort());
                startPromise.fail(connection.cause());
            }
        });
    }

    private void sendNext(ProtonSender sender) {
        Message message = Proton.message();
        message.setBody(new AmqpValue("Hello " + counter.incrementAndGet()));
        message.setAddress(address);
        sender.send(message);
        vertx.setTimer(timerInterval, id -> sendNext(sender));
    }
}
