package io.github.appliedenergistics.metrics.fabric.mixin.network;

import io.github.appliedenergistics.metrics.fabric.network.BlockEntityTypeHolder;
import io.github.appliedenergistics.metrics.fabric.network.PacketMetrics;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.Packet;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This Mixin can record certain world-related information about a packet before it is being sent off to the client.
 * <p>
 * This records the type of a block entity that triggered a block entity update, for example.
 */
@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {

    @Shadow
    public ServerPlayerEntity player;

    @Inject(method = "sendPacket", at = @At("HEAD"))
    public void onSendPacket(Packet<?> packet, CallbackInfo ci) {
        if (packet instanceof BlockEntityTypeHolder) {
            BlockEntityTypeHolder blockEntityTypeHolder = (BlockEntityTypeHolder) packet;
            Identifier id;
            try {
                BlockEntity blockEntity = this.player.world.getBlockEntity(blockEntityTypeHolder.metrics_getBlockPos());
                id = PacketMetrics.getBlockEntityId(blockEntity);
            } catch (Throwable ignored) {
                id = PacketMetrics.ID_ERROR;
            }
            blockEntityTypeHolder.metrics_setBlockEntityTypeId(id);
        }
    }

}
