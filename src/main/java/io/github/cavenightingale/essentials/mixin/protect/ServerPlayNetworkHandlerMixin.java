package io.github.cavenightingale.essentials.mixin.protect;

import io.github.cavenightingale.essentials.protect.GameEventLogger;
import io.github.cavenightingale.essentials.protect.event.ClientChatEvent;
import io.github.cavenightingale.essentials.protect.event.ClientQuitEvent;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Date;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
	@Shadow public ServerPlayerEntity player;

	@Inject(method = "onDisconnected", at = @At("HEAD"))
	public void onOnDisconnected(Text reason, CallbackInfo ci) {
		GameEventLogger.logEvent(new ClientQuitEvent(new Date(), player.getWorld().getRegistryKey().getValue(), new Vec3d(player.getX(), player.getY(), player.getZ()), player.getGameProfile(), reason.asString(), reason.asString()));
	}

	@Inject(method = "onChatMessage", at = @At("RETURN"))
	public void onOnChatMessage(ChatMessageC2SPacket packet, CallbackInfo ci) {
		GameEventLogger.logEvent(new ClientChatEvent(new Date(), player.getWorld().getRegistryKey().getValue(), new Vec3d(player.getX(), player.getY(), player.getZ()), player.getGameProfile(), packet.getChatMessage()));
	}
}
