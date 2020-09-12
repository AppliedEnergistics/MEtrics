package io.github.appliedenergistics.metrics.api;

import io.micrometer.core.instrument.MeterRegistry;

import java.lang.reflect.Method;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class Metrics {

    private static final int API_VERSION = 1;

    // This pattern will lazily initialize INSTANCE when it's first accessed
    private static class RegistryHolder {
        private static final MeterRegistry INSTANCE = resolveRegistry();

        private static MeterRegistry resolveRegistry() {
            try {
                Class<?> apiBindingClass = Class.forName("io.github.appliedenergistics.metrics.core.ApiBinding");
                checkSupportedApiVersion(apiBindingClass);

                Method createRegistryMethod = apiBindingClass.getMethod("createRegistry");
                return (MeterRegistry) createRegistryMethod.invoke(null);
            } catch (ClassNotFoundException ignored) {
                System.out.println("Loading a no-op Metrics backend since the metrics mod is not present.");
            } catch (Exception e) {
                System.err.println("The metrics plugin was present, but failed to obtain it's registry.");
                e.printStackTrace();
            }

            return new NoopMeterRegistry();
        }

        private static void checkSupportedApiVersion(Class<?> apiBindingClass) throws Exception {
            Method supportsApiVersionMethod = apiBindingClass.getMethod("supportsApiVersion", int.class);
            IntStream supportedVersions = IntStream.of((int[]) supportsApiVersionMethod.invoke(null, API_VERSION));
            if (supportedVersions.noneMatch(v -> v == API_VERSION)) {
                String supportedVersionsString = supportedVersions.mapToObj(String::valueOf).collect(Collectors.joining(", "));
                throw new Exception("This API version " + API_VERSION + " is not supported by the metrics mod that is present: " + supportedVersionsString);
            }
        }

    }

    public static synchronized MeterRegistry registry() {
        return RegistryHolder.INSTANCE;
    }

}
