package io.github.cavenightingale.essentials.mixin;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.fabricmc.fabric.impl.registry.sync.RegistrySyncManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

@Mixin(RegistrySyncManager.class)
public class FabricRegistrySyncManagerMixin {
	private static Map<Identifier, Object2IntMap<Identifier>> cached = null;
	@Redirect(method = "sendPacket(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/fabricmc/fabric/impl/registry/sync/packet/RegistryPacketHandler;)V", at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/impl/registry/sync/RegistrySyncManager;createAndPopulateRegistryMap(ZLjava/util/Map;)Ljava/util/Map;"))
	private static @Nullable Map<Identifier, Object2IntMap<Identifier>> applyCachedMap(boolean classType, @Nullable Map<Identifier, Object2IntMap<Identifier>> id) {
		return cached == null ? (cached = RegistrySyncManager.createAndPopulateRegistryMap(classType, id)) : cached;
	}
}
