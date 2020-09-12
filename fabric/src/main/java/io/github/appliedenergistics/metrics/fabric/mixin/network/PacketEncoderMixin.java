package io.github.appliedenergistics.metrics.fabric.mixin.network;

import io.github.appliedenergistics.metrics.core.SharedRegistry;
import io.micrometer.core.instrument.DistributionSummary;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketEncoder;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(PacketEncoder.class)
public class PacketEncoderMixin {

    @Unique
    private static final Map<String, DistributionSummary> vanillaTypeMetrics = new ConcurrentHashMap<>();

    @Unique
    private static final Map<Identifier, DistributionSummary> moddedTypeMetrics = new ConcurrentHashMap<>();

    @Shadow
    private NetworkSide side;

    @Inject(method = "encode", at = @At("TAIL"))
    void encode(ChannelHandlerContext context, Packet<?> packet, ByteBuf byteBuf, CallbackInfo ci) {

        DistributionSummary summary;

        if (packet instanceof CustomPayloadS2CPacket) {
            // Since we're in the _encoder_, we assume that the identifier is set since this should be serverbound
            Identifier channel = ((CustomPayloadS2CPacketAccessor) packet).metrics_getChannel();
            if (channel == null) {
                return; // Cannot record this, server sending a client->server packet ???
            }
            summary = moddedTypeMetrics.computeIfAbsent(channel, this::createMetric);
        } else if (packet instanceof CustomPayloadC2SPacket) {
            // Since we're in the _encoder_, we assume that the identifier is set since this should be serverbound
            Identifier channel = ((CustomPayloadC2SPacketAccessor) packet).metrics_getChannel();
            if (channel == null) {
                return; // Cannot record this, server sending a client->server packet ???
            }

            summary = moddedTypeMetrics.computeIfAbsent(channel, this::createMetric);
        } else {
            String typeName = packet.getClass().getSimpleName();
            summary = vanillaTypeMetrics.computeIfAbsent(typeName, this::createMetric);
        }

        summary.record(byteBuf.readableBytes());
    }

    @Unique
    private DistributionSummary createMetric(Identifier channelId) {
        return createMetric(channelId.toString());
    }

    @Unique
    private DistributionSummary createMetric(Class<?> packetClass) {
        return createMetric(packetClass.getSimpleName());
    }

    @Unique
    private DistributionSummary createMetric(String packetTypeName) {
        return DistributionSummary
                .builder("minecraft.packets." + side.name().toLowerCase()) //
                .baseUnit("byte") //
                .description("Measures the uncompressed size of Minecraft packets of this type") //
                .tag("type", packetTypeName) //
                .register(SharedRegistry.registry());
    }

}
