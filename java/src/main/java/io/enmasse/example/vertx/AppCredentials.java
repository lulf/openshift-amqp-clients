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
    private final File x509Certificate;
    private final File jksCertificate;

    public AppCredentials(String hostname, int port, String username, String password, File x509Certificate, File jksCertificate) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;
        this.x509Certificate = x509Certificate;
        this.jksCertificate = jksCertificate;
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

    public File getX509Certificate() {
        return x509Certificate;
    }

    public File getJksCertificate() {
        return jksCertificate;
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
        File x509Certificate = new File(SECRETS_PATH, "certificate.pem");
        File jksCertificate = new File(SECRETS_PATH, "certificate.jks");
        return new AppCredentials(hostname, port, username, password, x509Certificate, jksCertificate);
    }

    private static final String SECRETS_PATH = "/etc/app-credentials";

    private static final String readSecretFile(String filename) throws IOException {
        File secretDir = new File(SECRETS_PATH);
        File file = new File(secretDir, filename);
        if (!file.exists()) {
            throw new IllegalStateException("Unable to find secret " + file.getAbsolutePath());
        }
        return new String(Files.readAllBytes(file.toPath()));
    }

    private static boolean isOnKube() {
        return new File("/var/run/secrets/kubernetes.io/serviceaccount").exists();
    }

    public static AppCredentials fromProperties() throws IOException {
        Properties properties = loadProperties("config.properties");
        return new AppCredentials(
                properties.getProperty("hostname"),
                Integer.parseInt(properties.getProperty("port")),
                properties.getProperty("username"),
                properties.getProperty("password"),
                new File(properties.getProperty("certificate.pem")),
                new File(properties.getProperty("certificate.jks")));
    }

    private static Properties loadProperties(String resource) throws IOException {
        Properties properties = new Properties();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream stream = loader.getResourceAsStream(resource);
        properties.load(stream);
        return properties;
    }
}
