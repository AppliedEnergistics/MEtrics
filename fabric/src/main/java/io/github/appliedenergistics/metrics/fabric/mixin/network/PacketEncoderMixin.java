package io.github.appliedenergistics.metrics.fabric.mixin.network;

import io.github.appliedenergistics.metrics.fabric.network.PacketMetrics;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketEncoder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This Mixin records statistics about packets being sent to the other side (this applies to both server->client,
 * and client->server connections). The sizes recorded by this mixin are _uncompressed_ sizes, so the actual network
 * traffic will differ.
 */
@Mixin(PacketEncoder.class)
public class PacketEncoderMixin {

    @Inject(method = "encode", at = @At("TAIL"))
    void encode(ChannelHandlerContext context, Packet<?> packet, ByteBuf byteBuf, CallbackInfo ci) {
        PacketMetrics.recordOutgoing(packet, byteBuf.readableBytes());
    }

}
