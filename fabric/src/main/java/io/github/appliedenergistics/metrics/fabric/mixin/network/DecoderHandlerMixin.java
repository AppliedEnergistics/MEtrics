package io.github.appliedenergistics.metrics.fabric.mixin.network;

import io.github.appliedenergistics.metrics.fabric.network.PacketMetrics;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.DecoderHandler;
import net.minecraft.network.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * This Mixin records statistics about packets being received from the other side (this applies to both server->client,
 * and client->server connections). The sizes recorded by this mixin are _uncompressed_ sizes, so the actual network
 * traffic will differ.
 */
@Mixin(DecoderHandler.class)
public class DecoderHandlerMixin {

    @Unique
    private int bufferSize;

    @Unique
    private int listSizeBeforeEnd;

    @Inject(method = "decode", at = @At("HEAD"))
    void recordBufferSize(ChannelHandlerContext context, ByteBuf byteBuf, List<Object> list, CallbackInfo ci) {
        bufferSize = byteBuf.readableBytes();
        listSizeBeforeEnd = list.size();
    }

    @Inject(method = "decode", at = @At("TAIL"))
    void recordMetric(ChannelHandlerContext context, ByteBuf byteBuf, List<Object> list, CallbackInfo ci) {
        if (list.size() == listSizeBeforeEnd || bufferSize == 0) {
            return; // No packet was read
        }

        Object packet = list.get(list.size() - 1);
        PacketMetrics.recordIncoming((Packet<?>) packet, bufferSize);
    }

}
