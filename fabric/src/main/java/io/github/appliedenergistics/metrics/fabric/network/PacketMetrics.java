package io.github.appliedenergistics.metrics.fabric.network;

import io.github.appliedenergistics.metrics.core.SharedRegistry;
import io.github.appliedenergistics.metrics.fabric.mixin.network.CustomPayloadC2SPacketAccessor;
import io.github.appliedenergistics.metrics.fabric.mixin.network.CustomPayloadS2CPacketAccessor;
import io.micrometer.core.instrument.DistributionSummary;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PacketMetrics {

    public static final Identifier ID_NONE = new Identifier("metrics:none");

    public static final Identifier ID_ERROR = new Identifier("metrics:error");

    public static final Identifier ID_UNIDENTIFIABLE = new Identifier("metrics:unidentifiable");

    private static final String METRIC_PACKETS_RECEIVED = "minecraft.packets.received.payload_size";

    private static final String METRIC_PACKETS_SENT = "minecraft.packets.sent.payload_size";

    private static final String METRIC_BE_UPDATES_RECEIVED = "minecraft.be_updates.received";

    private static final String METRIC_BE_UPDATES_SENT = "minecraft.be_updates.received";

    private static final Map<Class<?>, DistributionSummary> outgoingVanillaTypeMetrics = new ConcurrentHashMap<>();

    private static final Map<Identifier, DistributionSummary> outgoingModdedTypeMetrics = new ConcurrentHashMap<>();

    private static final Map<Identifier, DistributionSummary> outgoingBeTypeUpdateMetrics = new ConcurrentHashMap<>();

    private static final Map<Class<?>, DistributionSummary> incomingVanillaTypeMetrics = new ConcurrentHashMap<>();

    private static final Map<Identifier, DistributionSummary> incomingModdedTypeMetrics = new ConcurrentHashMap<>();

    private static final Map<Identifier, DistributionSummary> incomingBeTypeUpdateMetrics = new ConcurrentHashMap<>();

    public static void recordIncomingBlockEntityUpdate(BlockEntity blockEntity, BlockEntityUpdateS2CPacket packet) {
        int uncompressedSize = ((PacketSize) packet).metrics_getUncompressedSize();
        Identifier id = getBlockEntityId(blockEntity);
        incomingBeTypeUpdateMetrics
                .computeIfAbsent(id, PacketMetrics::createIncomingBeTypeMetric)
                .record(uncompressedSize);
    }

    public static Identifier getBlockEntityId(BlockEntity blockEntity) {
        if (blockEntity == null) {
            return ID_NONE;
        } else {
            try {
                Identifier id = Registry.BLOCK_ENTITY_TYPE.getId(blockEntity.getType());
                if (id == null) {
                    return ID_UNIDENTIFIABLE;
                }
                return id;
            } catch (Throwable t) {
                return ID_ERROR;
            }
        }
    }

    public static void recordOutgoing(Packet<?> packet, int uncompressedSize) {

        if (packet instanceof PacketSize) {
            ((PacketSize) packet).metrics_setUncompressedSize(uncompressedSize);
        }

        DistributionSummary summary;

        if (packet instanceof CustomPayloadS2CPacket) {
            // Since we're in the _encoder_, we assume that the identifier is set since this should be serverbound
            Identifier channel = ((CustomPayloadS2CPacketAccessor) packet).metrics_getChannel();
            if (channel == null) {
                return; // Cannot record this, server sending a client->server packet ???
            }
            summary = outgoingModdedTypeMetrics.computeIfAbsent(channel, PacketMetrics::createOutgoingMetric);
        } else if (packet instanceof CustomPayloadC2SPacket) {
            // Since we're in the _encoder_, we assume that the identifier is set since this should be serverbound
            Identifier channel = ((CustomPayloadC2SPacketAccessor) packet).metrics_getChannel();
            if (channel == null) {
                return; // Cannot record this, server sending a client->server packet ???
            }

            summary = outgoingModdedTypeMetrics.computeIfAbsent(channel, PacketMetrics::createOutgoingMetric);
        } else {
            summary = outgoingVanillaTypeMetrics.computeIfAbsent(packet.getClass(), PacketMetrics::createOutgoingMetric);
        }

        summary.record(uncompressedSize);

        // Additionally, record block entity update types
        if (packet instanceof BlockEntityTypeHolder) {
            BlockEntityTypeHolder blockEntityTypeHolder = (BlockEntityTypeHolder) packet;
            Identifier id = blockEntityTypeHolder.metrics_getBlockEntityTypeId();
            outgoingBeTypeUpdateMetrics
                    .computeIfAbsent(id, PacketMetrics::createOutgoingBeTypeMetric)
                    .record(uncompressedSize);
        }
    }

    /**
     * Contains readable names for the built-in BE types used by Vanilla (1.16.2)
     */
    private static final String[] VANILLA_BE_TYPES = {
            "unknown_0", // 0
            "mob_spawner", // 1
            "command_block", // 2
            "beacon", // 3
            "skull", // 4
            "conduit", // 5
            "banner", // 6
            "structure_block", // 7
            "end_gateway", // 8
            "sign", // 9
            "unknown_10", // 10
            "bed", // 11
            "jigsaw", // 12
            "campfire", // 13
            "beehive", // 14
    };

    public static void recordIncoming(Packet<?> packet, int uncompressedSize) {
        // Record the current packet size in case later handlers can get a more detailed sub-type
        if (packet instanceof PacketSize) {
            ((PacketSize) packet).metrics_setUncompressedSize(uncompressedSize);
        }

        DistributionSummary summary;
        if (packet instanceof CustomPayloadS2CPacket) {
            // Since we're in the _encoder_, we assume that the identifier is set since this should be serverbound
            Identifier channel = ((CustomPayloadS2CPacketAccessor) packet).metrics_getChannel();
            if (channel == null) {
                return; // Cannot record this, server sending a client->server packet ???
            }
            summary = incomingModdedTypeMetrics.computeIfAbsent(channel, PacketMetrics::createIncomingMetric);
        } else if (packet instanceof CustomPayloadC2SPacket) {
            // Since we're in the _encoder_, we assume that the identifier is set since this should be serverbound
            Identifier channel = ((CustomPayloadC2SPacketAccessor) packet).metrics_getChannel();
            if (channel == null) {
                return; // Cannot record this, server sending a client->server packet ???
            }

            summary = incomingModdedTypeMetrics.computeIfAbsent(channel, PacketMetrics::createIncomingMetric);
        } else {
            summary = incomingVanillaTypeMetrics.computeIfAbsent(packet.getClass(), PacketMetrics::createIncomingMetric);
        }

        summary.record(uncompressedSize);
    }

    private static DistributionSummary createIncomingMetric(Identifier channelId) {
        return createMetric(METRIC_PACKETS_RECEIVED, channelId.toString());
    }

    private static DistributionSummary createIncomingMetric(Class<?> packetClass) {
        return createMetric(METRIC_PACKETS_RECEIVED, VanillaPacketTypes.getName(packetClass));
    }

    private static DistributionSummary createIncomingBeTypeMetric(Identifier typeId) {
        return createIdentifierMetric(METRIC_BE_UPDATES_RECEIVED, typeId);
    }

    private static DistributionSummary createOutgoingMetric(Identifier channelId) {
        return createMetric(METRIC_PACKETS_SENT, channelId.toString());
    }

    private static DistributionSummary createOutgoingMetric(Class<?> packetClass) {
        return createMetric(METRIC_PACKETS_SENT, VanillaPacketTypes.getName(packetClass));
    }

    private static DistributionSummary createOutgoingBeTypeMetric(Identifier typeId) {
        return createIdentifierMetric(METRIC_BE_UPDATES_SENT, typeId);
    }

    private static DistributionSummary createIdentifierMetric(String metricName, Identifier id) {
        return DistributionSummary
                .builder(metricName) //
                .baseUnit("byte") //
                .description("Measures the uncompressed size of packets") //
                .tag("mod", id.getNamespace()) //
                .tag("type", id.toString()) //
                .register(SharedRegistry.registry());
    }

    private static DistributionSummary createMetric(String metricName, String typeName) {
        return DistributionSummary
                .builder(metricName) //
                .baseUnit("byte") //
                .description("Measures the uncompressed size of packets") //
                .tag("type", typeName) //
                .register(SharedRegistry.registry());
    }

}
