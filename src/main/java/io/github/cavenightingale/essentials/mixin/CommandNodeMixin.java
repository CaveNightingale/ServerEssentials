package io.github.cavenightingale.essentials.mixin;

import com.mojang.brigadier.tree.CommandNode;
import io.github.cavenightingale.essentials.misc.CommandSourceWithOutput;
import io.github.cavenightingale.essentials.utils.CommandNodeWithPermission;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(value = CommandNode.class, remap = false)
public class CommandNodeMixin implements CommandNodeWithPermission {
	@Shadow @Final private Predicate<Object> requirement;
	String permissionNode = null;

	@Override
	public void serveressential_setPermission(String node) {
		permissionNode = node;
	}

	@Override
	public String serveressential_getPermission() {
		return permissionNode;
	}

	@Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
	public void canUse(Object source, CallbackInfoReturnable<Boolean> cir) {
		if(source instanceof ServerCommandSource src && permissionNode != null && ((CommandSourceWithOutput) src).serveressentials_getOutput() instanceof PlayerEntity) {
			cir.setReturnValue(Permissions.check(src, permissionNode) && requirement.test(src.withLevel(5)));
		}
	}
}
