package io.github.appliedenergistics.metrics.fabric.mixin.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockEntityUpdateS2CPacket.class)
public interface BlockEntityUpdateS2CPacketAccessor {
    @Accessor("blockEntityType")
    int metrics_getBlockEntityType();

    @Accessor("tag")
    CompoundTag metrics_getTag();
}
