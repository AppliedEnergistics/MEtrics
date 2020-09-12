package io.github.appliedenergistics.metrics.core;

import com.sun.net.httpserver.HttpServer;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Path;

public final class Startup {

    private static boolean initialized = false;

    public static synchronized void initialize(Path gameDir, boolean client) {
        if (initialized) {
            throw new IllegalStateException("Metrics mod was already initialized!");
        }
        initialized = true;

        PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        SharedRegistry.setRegistry(registry);

        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(client ? 9002 : 9001), 0);
            server.createContext("/metrics", httpExchange -> {
                String response = registry.scrape();
                httpExchange.sendResponseHeaders(200, response.getBytes().length);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            });

            new Thread(server::start).start();
        } catch (IOException e) {
            System.err.println("Failed to start Metric server");
            e.printStackTrace();
        }
    }

}
