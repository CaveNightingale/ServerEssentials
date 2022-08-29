package io.github.cavenightingale.essentials.protect.event;

import com.mojang.authlib.GameProfile;
import io.github.cavenightingale.essentials.protect.database.event.EntitySourcedEvent;
import io.github.cavenightingale.essentials.protect.database.event.EntityTargetedEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.Date;

public record LivingEntityInteractLivingEntityEvent(
		Date date,
		Identifier world,
		Vec3d location,
		GameProfile sourceEntity,
		GameProfile directEntity,
		ItemStack weapon,
		GameProfile targetEntity
) implements EntitySourcedEvent, EntityTargetedEvent {
}
