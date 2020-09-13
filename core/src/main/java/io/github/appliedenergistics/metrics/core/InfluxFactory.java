package io.github.appliedenergistics.metrics.core;

import java.time.Duration;
import java.util.Optional;
import java.util.Properties;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.influx.InfluxConfig;
import io.micrometer.influx.InfluxMeterRegistry;

public class InfluxFactory {

    private static final String CONFIG_PREFIX = "influx.";
    private static final String CONFIG_ENABLED = CONFIG_PREFIX + "enabled";
    private static final String CONFIG_SERVER = CONFIG_PREFIX + "server";
    private static final String CONFIG_DATABASE = CONFIG_PREFIX + "database";
    private static final String CONFIG_USERNAME = CONFIG_PREFIX + "user";
    private static final String CONFIG_PASSWORD = CONFIG_PREFIX + "password";
    private static final String CONFIG_STEP = CONFIG_PREFIX + "step";

    public static Optional<MeterRegistry> init(boolean client, Properties config) {
        if (!Boolean.valueOf(config.getProperty(CONFIG_ENABLED, "false"))) {
            return Optional.empty();
        }

        String server = config.getProperty(CONFIG_SERVER, "http://localhost:8086");
        String database = config.getProperty(CONFIG_DATABASE, "minecraft");
        String username = config.getProperty(CONFIG_USERNAME, "");
        String password = config.getProperty(CONFIG_PASSWORD, "");
        Duration step = Duration.ofSeconds(Integer.parseInt(config.getProperty(CONFIG_STEP, "1")));

        InfluxConfig influxConfig = new Config(server, database, username, password, step);
        return Optional.of(new InfluxMeterRegistry(influxConfig, Clock.SYSTEM));
    }

    private static class Config implements InfluxConfig {
        private final String server;
        private final String database;
        private final String user;
        private final String password;
        private final Duration step;

        public Config(String server, String database, String user, String password, Duration step) {
            this.server = server;
            this.database = database;
            this.user = user;
            this.password = password;
            this.step = step;
        }

        @Override
        public String uri() {
            return server;
        }

        @Override
        public String db() {
            return database;
        }

        @Override
        public String userName() {
            return user;
        }

        @Override
        public String password() {
            return password;
        }

        @Override
        public Duration step() {
            return step;
        }

        @Override
        public String get(String key) {
            return null;
        }
    }
}
