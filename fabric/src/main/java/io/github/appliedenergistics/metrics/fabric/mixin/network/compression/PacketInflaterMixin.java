package io.github.appliedenergistics.metrics.fabric.mixin.network.compression;

import io.github.appliedenergistics.metrics.fabric.network.CompressionMetrics;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.PacketInflater;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * Records the overall compression rate for incoming packets.
 */
@Mixin(PacketInflater.class)
public class PacketInflaterMixin {

    @Unique
    private int inputSize;

    /**
     * Records the size of the original packet.
     */
    @Inject(method = "decode", at = @At("HEAD"))
    public void onDecodeStart(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list, CallbackInfo ci) {
        this.inputSize = byteBuf.readableBytes();
    }

    /**
     * This is within the first IF-branch that handles an uncompressed packet.
     */
    @ModifyArg(method = "decode", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 0))
    public Object onUncompressedPacket(Object decompressedBuffer) {
        CompressionMetrics.recordIncomingUncompressedPacket(inputSize);
        return decompressedBuffer;
    }

    /**
     * This is within the first IF-branch that handles a compressed packet.
     */
    @ModifyArg(method = "decode", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 1))
    public Object onCompressedPacket(Object decompressedBuffer) {
        CompressionMetrics.recordIncomingCompressedPacket(inputSize, ((ByteBuf) decompressedBuffer).readableBytes());
        return decompressedBuffer;
    }

}
