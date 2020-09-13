package io.github.appliedenergistics.metrics.fabric.mixin.network.compression;

import io.github.appliedenergistics.metrics.fabric.network.CompressionMetrics;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.PacketDeflater;
import net.minecraft.network.PacketInflater;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Records the overall compression rate for incoming packets.
 */
@Mixin(PacketDeflater.class)
public class PacketDeflaterMixin {

    @Shadow
    private int compressionThreshold;

    @Unique
    private int inputSize;

    @Unique
    private int initialOutputSize;

    /**
     * Records the size of the input, and potential size of existing data in the output
     */
    @Inject(method = "encode", at = @At("HEAD"))
    public void onEncodeStart(ChannelHandlerContext channelHandlerContext, ByteBuf input, ByteBuf output, CallbackInfo ci) {
        this.inputSize = input.readableBytes();
        this.initialOutputSize = output.readableBytes();
    }

    /**
     * Records the size of the input, and potential data in the output
     */
    @Inject(method = "encode", at = @At("HEAD"))
    public void onEncodeEnd(ChannelHandlerContext channelHandlerContext, ByteBuf input, ByteBuf output, CallbackInfo ci) {
        int finalOutputSize = output.readableBytes() - this.initialOutputSize;
        boolean compressed = this.inputSize >= this.compressionThreshold;
        if (compressed) {
            // + 1 because the varint 0/1 header is always sent
            CompressionMetrics.recordOutgoingCompressedPacket(finalOutputSize, inputSize + 1);
        } else {
            CompressionMetrics.recordOutgoingUncompressedPacket(finalOutputSize);
        }
    }

}
