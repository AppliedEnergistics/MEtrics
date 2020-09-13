package io.github.appliedenergistics.metrics.core;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.Properties;

import com.sun.net.httpserver.HttpServer;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;

public class PrometheusFactory {

    private static final String CONFIG_PREFIX = "prometheus.";
    private static final String CONFIG_ENABLED = CONFIG_PREFIX + "enabled";
    private static final String CONFIG_ENDPOINT = CONFIG_PREFIX + "endpoint";
    private static final String CONFIG_SERVER_PORT = CONFIG_PREFIX + "server_port";
    private static final String CONFIG_CLIENT_PORT = CONFIG_PREFIX + "client_port";

    private static Thread HTTP_SERVER_THREAD = null;

    public static Optional<MeterRegistry> init(boolean client, Properties config) {
        if (!Boolean.valueOf(config.getProperty(CONFIG_ENABLED, "false"))) {
            return Optional.empty();
        }

        if (HTTP_SERVER_THREAD != null) {
            throw new IllegalStateException("Cannot start multiple HTTP Servers");
        }

        PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

        try {
            String endpoint = config.getProperty(CONFIG_ENDPOINT, "/metrics");
            int serverPort = Integer.valueOf(config.getProperty(CONFIG_SERVER_PORT, "9001"));
            int clientPort = Integer.valueOf(config.getProperty(CONFIG_CLIENT_PORT, "9002"));

            HttpServer server = HttpServer.create(new InetSocketAddress(client ? clientPort : serverPort), 0);
            server.createContext(endpoint, httpExchange -> {
                String response = registry.scrape();
                httpExchange.sendResponseHeaders(200, response.getBytes().length);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            });

            HTTP_SERVER_THREAD = new Thread(server::start);
            HTTP_SERVER_THREAD.setDaemon(true);
            HTTP_SERVER_THREAD.start();
        } catch (IOException e) {
            System.err.println("Failed to start Metric server");
            e.printStackTrace();
        }

        return Optional.of(registry);
    }

}
