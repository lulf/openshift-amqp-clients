package io.enmasse.example.jms;

import io.enmasse.example.common.AppCredentials;
import io.vertx.core.Vertx;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Optional;

import javax.jms.JMSConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;

public class Main {

    public static void main(String [] args) throws Exception {
        String address = Optional.ofNullable(System.getenv("ADDRESS")).orElse("myqueue");
        AppCredentials appCredentials = AppCredentials.create();
 
        
        ConnectionFactory  connectionFactory = new
    }

    private static Context fromCredentials(AppCredentials credentials, String queueName) {
        
        Hashtable<Object, Object> env = new Hashtable<>();
        env.put("connectionfactory.myFactory", String.format("amqps://%s:%s@%s:%d", credentials.getUsername(), credentials.getPassword(), 
        Context context = new InitialContext(env);
        return context;
    }
}
