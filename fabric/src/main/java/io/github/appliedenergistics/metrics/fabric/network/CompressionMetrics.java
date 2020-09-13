package io.github.appliedenergistics.metrics.fabric.network;

import io.github.appliedenergistics.metrics.core.SharedRegistry;
import io.micrometer.core.instrument.DistributionSummary;

/**
 * Records metrics about the efficiency of Minecraft's packet compression.
 */
public final class CompressionMetrics {

    private static final DistributionSummary PACKETS_SENT_UNCOMPRESSED = DistributionSummary
            .builder("minecraft.packets.sent.uncompressed_size") //
            .baseUnit("byte") //
            .description("Measures the size of uncompressed packets") //
            .register(SharedRegistry.registry());

    private static final DistributionSummary PACKETS_SENT_COMPRESSED = DistributionSummary
            .builder("minecraft.packets.sent.compressed_size") //
            .baseUnit("byte") //
            .description("Measures the size of compressed packets") //
            .register(SharedRegistry.registry());

    private static final DistributionSummary PACKETS_SENT_DECOMPRESSED = DistributionSummary
            .builder("minecraft.packets.sent.decompressed_size") //
            .baseUnit("byte") //
            .description("Measures the decompressed size of compressed packets") //
            .register(SharedRegistry.registry());

    private static final DistributionSummary PACKETS_RECEIVED_UNCOMPRESSED = DistributionSummary
            .builder("minecraft.packets.received.uncompressed_size") //
            .baseUnit("byte") //
            .description("Measures the uncompressed size of packets") //
            .register(SharedRegistry.registry());

    private static final DistributionSummary PACKETS_RECEIVED_COMPRESSED = DistributionSummary
            .builder("minecraft.packets.received.compressed_size") //
            .baseUnit("byte") //
            .description("Measures the compressed size of packets") //
            .register(SharedRegistry.registry());

    private static final DistributionSummary PACKETS_RECEIVED_DECOMPRESSED = DistributionSummary
            .builder("minecraft.packets.received.decompressed_size") //
            .baseUnit("byte") //
            .description("Measures the decompressed size of compressed packets") //
            .register(SharedRegistry.registry());

    private CompressionMetrics() {
    }

    public static void recordIncomingUncompressedPacket(int size) {
        PACKETS_RECEIVED_UNCOMPRESSED.record(size);
    }

    public static void recordIncomingCompressedPacket(int size, int uncompressedSize) {
        PACKETS_RECEIVED_COMPRESSED.record(size);
        PACKETS_RECEIVED_DECOMPRESSED.record(uncompressedSize);
    }

    public static void recordOutgoingUncompressedPacket(int size) {
        PACKETS_SENT_UNCOMPRESSED.record(size);
    }

    public static void recordOutgoingCompressedPacket(int size, int uncompressedSize) {
        PACKETS_SENT_COMPRESSED.record(size);
        PACKETS_SENT_DECOMPRESSED.record(uncompressedSize);
    }

}
