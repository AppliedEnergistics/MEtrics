package io.github.appliedenergistics.metrics.fabric.mixin.client.network;

import io.github.appliedenergistics.metrics.fabric.network.PacketMetrics;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    /**
     * We're injecting this early beacuse Fabric's Mixin will cancel the method and
     * cause us to not measure the packet.
     */
    @Inject(method = "onBlockEntityUpdate", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/world/ClientWorld;getBlockEntity(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/entity/BlockEntity;", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    public void onBlockEntityUpdate(BlockEntityUpdateS2CPacket packet, CallbackInfo ci, BlockPos blockPos,
            BlockEntity blockEntity) {
        PacketMetrics.recordIncomingBlockEntityUpdate(blockEntity, packet);
    }

}
