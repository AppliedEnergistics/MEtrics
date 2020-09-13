package io.github.appliedenergistics.metrics;

import io.github.appliedenergistics.metrics.api.Metrics;
import io.micrometer.core.instrument.*;
import io.micrometer.core.instrument.noop.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This test will try to register every possible type of metric with just the
 * API present on the class-path to ensure our Jar shading does not miss any
 * required dependencies.
 */
public class MeterTest {

    @Test
    void testGauge() {
        Gauge gauge = Gauge.builder("my-gauge", () -> 123)
                .baseUnit("unit")
                .description("some description")
                .tag("tag1", "value")
                .register(Metrics.registry());

        Assertions.assertEquals(NoopGauge.class, gauge.getClass());

        // The No-Op gauge will always return 0
        assertEquals(0, gauge.value());
    }

    @Test
    void testCounter() {
        Counter counter = Counter.builder("my-counter")
                .baseUnit("unit")
                .description("some description")
                .tag("tag1", "value")
                .register(Metrics.registry());

        Assertions.assertEquals(NoopCounter.class, counter.getClass());

        counter.increment();
        counter.increment(123);

        // The No-Op counter will always return 0
        assertEquals(0, counter.count());
    }

    @Test
    void testTimer() {
        Timer timer = Timer.builder("my-timer")
                .distributionStatisticBufferLength(123)
                .distributionStatisticExpiry(Duration.ofDays(1))
                .minimumExpectedValue(Duration.ZERO)
                .maximumExpectedValue(Duration.ofDays(1))
                .description("some description")
                .tag("tag1", "value")
                .register(Metrics.registry());

        Assertions.assertEquals(NoopTimer.class, timer.getClass());

        AtomicBoolean ran = new AtomicBoolean(false);
        timer.record(() -> ran.set(true));
        assertTrue(ran.get(), "Even the NoOp timer should still run the given runnables");

        // The No-Op counter will always return 0
        assertEquals(0, timer.count());
    }

    @Test
    void testSummary() {
        DistributionSummary summary = DistributionSummary.builder("my-summary")
                .distributionStatisticBufferLength(123)
                .distributionStatisticExpiry(Duration.ofDays(1))
                .minimumExpectedValue(0.0)
                .maximumExpectedValue(9999.0)
                .description("some description")
                .tag("tag1", "value")
                .register(Metrics.registry());

        Assertions.assertEquals(NoopDistributionSummary.class, summary.getClass());

        summary.record(1239);

        // The No-Op counter will always return 0
        assertEquals(0, summary.count());
    }

    @Test
    void testMeter() {
        Meter meter = Meter.builder("my-meter", Meter.Type.OTHER, new ArrayList<>())
                .description("some description")
                .tag("tag1", "value")
                .register(Metrics.registry());

        Assertions.assertEquals(NoopMeter.class, meter.getClass());

        // The No-Op counter will always be empty
        assertEquals(Collections.emptyList(), meter.measure());
    }

    @Test
    void testFunctionTimer() {
        FunctionTimer funcTimer = FunctionTimer
                .builder("my-function-timer", this, mt -> 123, mt -> 123, TimeUnit.SECONDS)
                .description("some description")
                .tag("tag1", "value")
                .register(Metrics.registry());

        Assertions.assertEquals(NoopFunctionTimer.class, funcTimer.getClass());

        // The No-Op function timer will always be empty
        assertEquals(0, funcTimer.count());
    }

    @Test
    void testFunctionCounter() {
        FunctionCounter funcCounter = FunctionCounter.builder("my-function-counter", null, mt -> 123)
                .description("some description")
                .tag("tag1", "value")
                .register(Metrics.registry());

        Assertions.assertEquals(NoopFunctionCounter.class, funcCounter.getClass());

        // The No-Op function counter will always be empty
        assertEquals(0, funcCounter.count());
    }

}
