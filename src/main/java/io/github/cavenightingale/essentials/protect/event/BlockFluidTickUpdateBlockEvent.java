package io.github.cavenightingale.essentials.protect.event;

import io.github.cavenightingale.essentials.protect.database.event.BlockSourcedEvent;
import io.github.cavenightingale.essentials.protect.database.event.BlockTargetedEvent;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Date;

public record BlockFluidTickUpdateBlockEvent(
		Date date,
		Identifier world,
		Vec3d location,
		BlockPos sourceBlockPos,
		BlockState sourceBlockState,
		BlockPos targetBlockPos,
		BlockState targetBlockState,
		BlockState previousBlockState
) implements BlockSourcedEvent, BlockTargetedEvent {
}
