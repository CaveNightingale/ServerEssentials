package io.github.cavenightingale.essentials.protect.database.event;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public interface BlockTargetedEvent extends LoggedEvent {
	/**
	 * @return Where the blockstate changed or interacted
	 * */
	BlockPos targetBlockPos();

	/**
	 * @return The blockstate after player interact
	 * */
	BlockState targetBlockState();

	/**
	 * @return The blockstate before modifcation, or null if nothing modified
	 * */
	@Nullable default BlockState previousBlockState() {
		return null;
	}
}
