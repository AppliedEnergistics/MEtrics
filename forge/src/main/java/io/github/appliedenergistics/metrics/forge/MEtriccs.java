package io.github.appliedenergistics.metrics.forge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("metrics")
public class MEtriccs {

    private static final Logger LOGGER = LogManager.getLogger();

    public MEtriccs() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::construct);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void construct(final FMLConstructModEvent event) {
        LOGGER.info("MEtrics constructed");
    }
}
