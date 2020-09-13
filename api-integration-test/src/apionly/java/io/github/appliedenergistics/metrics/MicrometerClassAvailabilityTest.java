package io.github.appliedenergistics.metrics;

import org.junit.jupiter.api.Test;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MicrometerClassAvailabilityTest {

    /**
     * This class should have been removed by the shadow jar plugin.
     */
    @Test
    void testSimpleRegistryIsMissing() {
        assertThrows(ClassNotFoundException.class,
                () -> Class.forName("io.micrometer.core.instrument.simple.SimpleMeterRegistry"));
    }

    /**
     * This class should still be available.
     */
    @Test
    void testMeterRegistryComesFromShadedJar() throws Exception {
        Class<?> klass = Class.forName("io.micrometer.core.instrument.MeterRegistry");

        URL jarUrl = klass.getProtectionDomain().getCodeSource().getLocation();
        Path jarPath = Paths.get(jarUrl.toURI());
        String filename = jarPath.getFileName().toString();

        // assert that it is actually from our shaded jar and not some accidental
        // transitive dependency
        assertTrue(filename.startsWith("metrics-api-"), "Expected name to start with metrics-api-: " + filename);
        assertTrue(filename.endsWith(".jar"), "Expected name to end with .jar:" + filename);
    }

}
