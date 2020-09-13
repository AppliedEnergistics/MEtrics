package io.github.appliedenergistics.metrics.fabric.network;

/**
 * This interface is mixed into certain packets to keep track of their original
 * size when serialized
 */
public interface PacketSize {

    int metrics_getUncompressedSize();

    void metrics_setUncompressedSize(int uncompressedSize);

}
