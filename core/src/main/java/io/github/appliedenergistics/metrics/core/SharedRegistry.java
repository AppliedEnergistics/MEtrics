package io.github.appliedenergistics.metrics.core;

import io.micrometer.core.instrument.MeterRegistry;

/**
 * Contains the Meter registry used by all subsystems of the mod.
 */
public final class SharedRegistry {

    private static MeterRegistry registry = null;

    private SharedRegistry() {
    }

    public static synchronized MeterRegistry registry() {
        if (registry == null) {
            throw new IllegalStateException("Metrics mod was not initialized properly!");
        }
        return registry;
    }

    public static synchronized void setRegistry(MeterRegistry registry) {
        if (SharedRegistry.registry != null) {
            throw new IllegalStateException("Metrics mod was already initialized!");
        }
        SharedRegistry.registry = registry;
    }

}
