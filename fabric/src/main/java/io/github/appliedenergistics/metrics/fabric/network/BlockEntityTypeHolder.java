package io.github.appliedenergistics.metrics.fabric.network;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

/**
 * This interface is mixed into the block entity update packet to record which block entity is
 * at the updated location before then recording the serialized size in the network thread.
 *
 * @see net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
 */
public interface BlockEntityTypeHolder {

    Identifier metrics_getBlockEntityTypeId();

    void metrics_setBlockEntityTypeId(Identifier id);

    /**
     * Returns the position in the world where the block entity should be updated.
     */
    BlockPos metrics_getBlockPos();

}
