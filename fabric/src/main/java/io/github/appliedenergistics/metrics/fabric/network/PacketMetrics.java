package io.github.appliedenergistics.metrics.fabric.network;

import io.github.appliedenergistics.metrics.core.SharedRegistry;
import io.github.appliedenergistics.metrics.fabric.mixin.network.BlockEntityUpdateS2CPacketAccessor;
import io.github.appliedenergistics.metrics.fabric.mixin.network.CustomPayloadC2SPacketAccessor;
import io.github.appliedenergistics.metrics.fabric.mixin.network.CustomPayloadS2CPacketAccessor;
import io.micrometer.core.instrument.DistributionSummary;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PacketMetrics {

    private static final String METRIC_PACKETS_RECEIVED = "minecraft.packets.received";

    private static final String METRIC_PACKETS_SENT = "minecraft.packets.received";

    private static final String METRIC_BE_UPDATES_RECEIVED = "minecraft.be_updates.received";

    private static final String METRIC_BE_UPDATES_SENT = "minecraft.be_updates.received";

    private static final Map<Class<?>, DistributionSummary> outgoingVanillaTypeMetrics = new ConcurrentHashMap<>();

    private static final Map<Identifier, DistributionSummary> outgoingModdedTypeMetrics = new ConcurrentHashMap<>();

    private static final Map<String, DistributionSummary> outgoingBeTypeUpdateMetrics = new ConcurrentHashMap<>();

    private static final Map<Class<?>, DistributionSummary> incomingVanillaTypeMetrics = new ConcurrentHashMap<>();

    private static final Map<Identifier, DistributionSummary> incomingModdedTypeMetrics = new ConcurrentHashMap<>();

    private static final Map<String, DistributionSummary> incomingBeTypeUpdateMetrics = new ConcurrentHashMap<>();

    public static void recordOutgoing(Packet<?> packet, int uncompressedSize) {

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

        // Additionally, record block entity update types
        if (packet instanceof BlockEntityUpdateS2CPacket) {
            BlockEntityUpdateS2CPacketAccessor beUpdate = (BlockEntityUpdateS2CPacketAccessor) packet;
            int beTypeRawId = beUpdate.metrics_getBlockEntityType();
            String id;
            if (beTypeRawId < VANILLA_BE_TYPES.length) {
                id = VANILLA_BE_TYPES[beTypeRawId];
            } else if (beTypeRawId == 127) {
                // Raw id used by Fabric
                CompoundTag tag = beUpdate.metrics_getTag();
                if (tag == null) {
                    id = "unknown_" + beTypeRawId;
                } else {
                    id = tag.getString("id");
                }
            } else {
                id = "unknown_" + beTypeRawId;
            }
            incomingBeTypeUpdateMetrics
                    .computeIfAbsent(id, PacketMetrics::createIncomingBeTypeMetric)
                    .record(uncompressedSize);
        }
    }

    private static DistributionSummary createIncomingMetric(Identifier channelId) {
        return createMetric(METRIC_PACKETS_RECEIVED, channelId.toString());
    }

    private static DistributionSummary createIncomingMetric(Class<?> packetClass) {
        return createMetric(METRIC_PACKETS_RECEIVED, packetClass.getSimpleName());
    }

    private static DistributionSummary createIncomingBeTypeMetric(String type) {
        return createIdentifierMetric(METRIC_BE_UPDATES_RECEIVED, type);
    }

    private static DistributionSummary createOutgoingMetric(Identifier channelId) {
        return createMetric(METRIC_PACKETS_SENT, channelId.toString());
    }

    private static DistributionSummary createOutgoingMetric(Class<?> packetClass) {
        return createMetric(METRIC_PACKETS_SENT, packetClass.getSimpleName());
    }

    private static DistributionSummary createOutgoingBeTypeMetric(String type) {
        return createIdentifierMetric(METRIC_BE_UPDATES_SENT, type);
    }

    private static DistributionSummary createIdentifierMetric(String metricName, String serializedId) {
        String mod;
        int endOfMod = serializedId.indexOf(':');
        if (endOfMod > 0) {
            mod = serializedId.substring(0, endOfMod);
        } else {
            mod = "minecraft";
        }

        return DistributionSummary
                .builder(metricName) //
                .baseUnit("byte") //
                .description("Measures the uncompressed size of packets") //
                .tag("mod", mod) //
                .tag("type", serializedId) //
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
