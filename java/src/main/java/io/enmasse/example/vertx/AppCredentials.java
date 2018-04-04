package io.enmasse.example.vertx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Properties;

public class AppCredentials {
    private static final Logger log = LoggerFactory.getLogger(AppCredentials.class);
    private final String hostname;
    private final int port;
    private final String username;
    private final String password;

    public AppCredentials(String hostname, int port, String username, String password) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public static AppCredentials create() throws IOException {
        if (isOnKube()) {
            log.info("Loading configuration from secret");
            return fromSystem();
        } else {
            log.info("Loading configuration from properties");
            return fromProperties();
        }
    }

    public static AppCredentials fromSystem() throws IOException {
        String hostname = readSecretFile("host");
        int port = Integer.parseInt(readSecretFile("port"));
        String username = readSecretFile("username");
        String password = readSecretFile("password");
        return new AppCredentials(hostname, port, username, password);
    }

    private static final String readSecretFile(String filename) throws IOException {
        File file = new File(SECRETS_PATH, filename);
        return new String(Files.readAllBytes(file.toPath()));
    }

    private static final String SECRETS_PATH = "/etc/app-credentials";

    private static boolean isOnKube() {
        return new File(SECRETS_PATH).exists();
    }

    public static AppCredentials fromProperties() throws IOException {
        Properties properties = loadProperties("config.properties");
        return new AppCredentials(
                properties.getProperty("hostname"),
                Integer.parseInt(properties.getProperty("port")),
                properties.getProperty("username"),
                properties.getProperty("password"));
    }

    private static Properties loadProperties(String resource) throws IOException {
        Properties properties = new Properties();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream stream = loader.getResourceAsStream(resource);
        properties.load(stream);
        return properties;
    }
}
