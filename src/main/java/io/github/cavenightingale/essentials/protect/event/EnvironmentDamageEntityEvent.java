package io.github.cavenightingale.essentials.protect.event;

import com.mojang.authlib.GameProfile;
import io.github.cavenightingale.essentials.protect.database.event.EntityMiddledEvent;
import io.github.cavenightingale.essentials.protect.database.event.EntityTargetedEvent;
import io.github.cavenightingale.essentials.protect.database.event.ReasonedEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.Date;

public record EnvironmentDamageEntityEvent(
		Date date,
		Identifier world,
		Vec3d location,
		GameProfile directEntity,
		GameProfile targetEntity,
		double damage,
		String reason
) implements EntityMiddledEvent, EntityTargetedEvent, ReasonedEvent {
}
