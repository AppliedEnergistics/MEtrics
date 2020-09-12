package io.github.appliedenergistics.metrics.fabric.mixin.startup;

import io.github.appliedenergistics.metrics.core.Startup;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraft.Bootstrap;
import net.minecraft.server.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This mixin will run right before {@link Bootstrap#initialize()} does, and captures the game directory
 * to initialize the metrics subsystems before any constructors are run.
 */
@Mixin(Main.class)
public class ServerStartupMixin {

    @Inject(method = "main", at = @At(value = "INVOKE", target = "Lnet/minecraft/Bootstrap;initialize()V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void beforeInitialize(String[] args, CallbackInfo ci, OptionParser optionParser) {
        // The client uses this option to determine where the game is
        OptionSpec<String> gameDirArg = optionParser.accepts("universe").withRequiredArg().defaultsTo(".");

        // We have to reparse the args, otherwise we'd need to capture all the options locally, which sucks
        OptionSet optionSet = optionParser.parse(args);
        Path gameDir = Paths.get(optionSet.valueOf(gameDirArg)).toAbsolutePath();

        Startup.initialize(gameDir, false);
    }

}
