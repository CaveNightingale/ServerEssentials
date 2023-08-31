package io.github.cavenightingale.essentials.mixin;

import io.github.cavenightingale.essentials.misc.CommandSourceWithOutput;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerCommandSource.class)
public abstract class ServerCommandSourceMixin implements CommandSource, CommandSourceWithOutput {
    @Shadow @Final private CommandOutput output;

    @Override
    public CommandOutput serveressentials_getOutput() {
        return output;
    }
}
