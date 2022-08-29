package io.github.cavenightingale.essentials.protect.event;

import io.github.cavenightingale.essentials.protect.database.event.BlockTargetedEvent;
import io.github.cavenightingale.essentials.protect.database.event.ReasonedEvent;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Date;

public record EnvironmentBlockStateChangeEvent(
		Date date,
		Identifier world,
		Vec3d location,
		BlockPos targetBlockPos,
		BlockState targetBlockState,
		BlockState previousBlockState,
		String reason
) implements BlockTargetedEvent, ReasonedEvent {
}
