package io.github.appliedenergistics.metrics.core;

import io.micrometer.core.instrument.MeterRegistry;

/**
 * This class is loaded by
 * {@link io.github.appliedenergistics.metrics.api.Metrics} via reflection.
 */
public final class ApiBinding {

    private ApiBinding() {
    }

    public static boolean supportsApiVersion(int version) {
        return version == 1;
    }

    public static MeterRegistry getRegistry() {
        return SharedRegistry.registry();
    }

}
