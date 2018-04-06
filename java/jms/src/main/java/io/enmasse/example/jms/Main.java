package io.enmasse.example.jms;

import io.enmasse.example.common.AppCredentials;

import java.util.Hashtable;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class Main {

    public static void main(String [] args) throws Exception {
        String address = Optional.ofNullable(System.getenv("ADDRESS")).orElse("myqueue");
        AppCredentials appCredentials = AppCredentials.create();

        Context context = fromCredentials(appCredentials, address);

        ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup("factoryLookup");
        Destination destination = (Destination) context.lookup("destinationLookup");

        JMSConsumer consumer = new JMSConsumer(connectionFactory, destination);
        JMSProducer producer = new JMSProducer(connectionFactory, destination);

        Executor executor = Executors.newFixedThreadPool(2);
        executor.execute(consumer);
        executor.execute(producer);
    }

    private static Context fromCredentials(AppCredentials credentials, String queueName) throws NamingException {
        
        Hashtable<Object, Object> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.qpid.jms.jndi.JmsInitialContextFactory");
        env.put("connectionfactory.factoryLookup", String.format("amqps://%s:%d?jms.username=%s&jms.password=%s&transport.verifyHost=false&transport.trustAll=true&amqp.saslMechanisms=PLAIN", credentials.getHostname(), credentials.getPort(), credentials.getUsername(), credentials.getPassword()));
        env.put("queue.destinationLookup", queueName);
        return new InitialContext(env);
    }
}
