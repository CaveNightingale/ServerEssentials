package io.github.cavenightingale.essentials.protect.event;

import com.mojang.authlib.GameProfile;
import io.github.cavenightingale.essentials.protect.database.event.BlockSourcedEvent;
import io.github.cavenightingale.essentials.protect.database.event.EntityMiddledEvent;
import io.github.cavenightingale.essentials.protect.database.event.EntityTargetedEvent;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Date;

public record BlockDamageEntityEvent(
		Date date,
		Identifier world,
		Vec3d location,
		BlockPos sourceBlockPos,
		BlockState sourceBlockState,
		GameProfile directEntity,
		GameProfile targetEntity,
		double damage
) implements BlockSourcedEvent, EntityMiddledEvent, EntityTargetedEvent {
}
