package io.github.appliedenergistics.metrics.fabric.mixin.network;

import io.github.appliedenergistics.metrics.fabric.network.BlockEntityTypeHolder;
import io.github.appliedenergistics.metrics.fabric.network.PacketSize;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

/**
 * We mix this into the block entity update packet because the on-wire type is
 * only used as a hint to drop update packets when they don't match the
 * block-entity at the target location.
 */
@Mixin(BlockEntityUpdateS2CPacket.class)
public class BlockEntityUpdateS2CPacketMixin implements PacketSize, BlockEntityTypeHolder {

    @Unique
    private int uncompressedSize;

    @Unique
    private Identifier blockEntityTypeId;

    @Shadow
    private BlockPos pos;

    @Override
    public int metrics_getUncompressedSize() {
        return uncompressedSize;
    }

    @Override
    public void metrics_setUncompressedSize(int uncompressedSize) {
        this.uncompressedSize = uncompressedSize;
    }

    @Override
    public Identifier metrics_getBlockEntityTypeId() {
        return blockEntityTypeId;
    }

    @Override
    public void metrics_setBlockEntityTypeId(Identifier id) {
        this.blockEntityTypeId = id;
    }

    @Override
    public BlockPos metrics_getBlockPos() {
        return pos;
    }

}
