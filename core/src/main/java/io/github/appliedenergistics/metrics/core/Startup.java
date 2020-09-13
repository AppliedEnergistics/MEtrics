package io.github.appliedenergistics.metrics.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import io.micrometer.core.instrument.Metrics;

public final class Startup {

    private static final String CONFIG_DIRECTORY = "config";
    private static final String METRICS_DIRECTORY = "metrics";
    private static final String SERVER_CONFIG_FILE = "server.properties";
    private static final String CLIENT_CONFIG_FILE = "client.properties";

    private static boolean initialized = false;

    public static synchronized void initialize(Path gameDir, boolean client) {
        if (initialized) {
            throw new IllegalStateException("Metrics mod was already initialized!");
        }
        initialized = true;

        // Use the global registry to attach multiple registry if desired
        SharedRegistry.setRegistry(Metrics.globalRegistry);

        Properties config = loadConfig(client, gameDir);

        InfluxFactory.init(client, config).map(Metrics.globalRegistry::add);
        PrometheusFactory.init(client, config).map(Metrics.globalRegistry::add);
    }

    private static Properties loadConfig(boolean client, Path gameDir) {
        Properties config = new Properties();
        String fileName = client ? CLIENT_CONFIG_FILE : SERVER_CONFIG_FILE;

        try {
            Path configDir = gameDir.resolve(CONFIG_DIRECTORY);
            Path metricsDir = configDir.resolve(METRICS_DIRECTORY);
            Files.createDirectories(metricsDir);
            Path configFile = metricsDir.resolve(fileName);

            extractConfigFiles(metricsDir);

            config.load(Files.newBufferedReader(configFile));
            return config;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return config;
    }

    /**
     * Extracts any default config file when necessary
     * 
     * @param metricsDir
     */
    private static void extractConfigFiles(Path metricsDir) {
        extractConfigFile(metricsDir, SERVER_CONFIG_FILE);
        extractConfigFile(metricsDir, CLIENT_CONFIG_FILE);
    }

    /**
     * Extracts a single config file
     * 
     * @param metricsDir
     * @param fileName
     */
    private static void extractConfigFile(Path metricsDir, String fileName) {
        try {
            Path configFile = metricsDir.resolve(fileName);
            if (!Files.exists(configFile)) {
                Files.copy(Startup.class.getResourceAsStream("/" + fileName), configFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
