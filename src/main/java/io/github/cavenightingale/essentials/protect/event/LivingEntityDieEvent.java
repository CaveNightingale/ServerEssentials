package io.github.cavenightingale.essentials.protect.event;

import com.mojang.authlib.GameProfile;
import io.github.cavenightingale.essentials.protect.database.event.EntitySourcedEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.Date;

public record LivingEntityDieEvent(
		Date date,
		Identifier world,
		Vec3d location,
		GameProfile sourceEntity
) implements EntitySourcedEvent {
}
