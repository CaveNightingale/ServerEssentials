package io.github.cavenightingale.essentials.protect.database.event;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public interface BlockSourcedEvent extends LoggedEvent {
	/**
	 * @return The blockstate of the source block
	 * */
	BlockState sourceBlockState();

	/**
	 * @return The position of the source block
	 * */
	BlockPos sourceBlockPos();
}
