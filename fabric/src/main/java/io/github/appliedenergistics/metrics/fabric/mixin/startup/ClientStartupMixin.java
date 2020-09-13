package io.github.appliedenergistics.metrics.fabric.mixin.startup;

import io.github.appliedenergistics.metrics.core.Startup;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraft.Bootstrap;
import net.minecraft.client.main.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.File;
import java.nio.file.Path;

/**
 * This mixin will run right before {@link Bootstrap#initialize()} does, and
 * captures the game directory to initialize the metrics subsystems before any
 * constructors are run.
 */
@Mixin(Main.class)
public class ClientStartupMixin {

    @Inject(method = "main", at = @At(value = "INVOKE", target = "Lnet/minecraft/Bootstrap;initialize()V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void beforeInitialize(String[] args, CallbackInfo ci, OptionParser optionParser) {
        // The client uses this option to determine where the game is
        OptionSpec<File> gameDirArg = optionParser.accepts("gameDir").withRequiredArg().ofType(File.class)
                .defaultsTo(new File("."));

        // We have to reparse the args, otherwise we'd need to capture all the options
        // locally, which sucks
        OptionSet optionSet = optionParser.parse(args);
        Path gameDir = optionSet.valueOf(gameDirArg).toPath().toAbsolutePath();

        Startup.initialize(gameDir, true);
    }

}
