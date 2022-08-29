package io.github.cavenightingale.essentials.protect;

import io.github.cavenightingale.essentials.protect.event.*;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Date;

import static io.github.cavenightingale.essentials.protect.GameEventLogger.*;
import static io.github.cavenightingale.essentials.protect.GameEventLogger.logEvent;

public class InternalHandler {

	public static void onSetBlockState(ServerWorld world, BlockPos pos, BlockState state) {
		BlockState prev = world.getBlockState(pos);
		if(prev == state)
			return;
		BlockPos neighbour = SourceChain.find(SourceChain.Comment.NEIGHBOUR_BLOCK);
		if(neighbour != null) {
			logEvent(new BlockNeighbourUpdateBlockEvent(new Date(), world.getRegistryKey().getValue(), Vec3d.ofCenter(neighbour), neighbour, world.getBlockState(neighbour), pos, state, prev));
		} else {
			Pair<LivingEntity, ItemStack> placeSource = SourceChain.find(SourceChain.Comment.SOURCE_ENTITY_PLACE);
			if(placeSource != null) {
				logEvent(new LivingEntityPlaceBlockEvent(new Date(), world.getRegistryKey().getValue(), entityPos(placeSource.getLeft()), entityProfile(placeSource.getLeft()), placeSource.getRight(), pos, state, prev));
			} else {
				Pair<LivingEntity, ItemStack> breakSource = SourceChain.find(SourceChain.Comment.SOURCE_ENTITY_BREAK);
				if(breakSource != null) {
					logEvent(new LivingEntityBreakBlockEvent(new Date(), world.getRegistryKey().getValue(), entityPos(breakSource.getLeft()), entityProfile(breakSource.getLeft()), entityProfileNullable(SourceChain.find(SourceChain.Comment.DIRECT_ENTITY)), breakSource.getRight(), pos, state, prev));
				} else {
					Pair<LivingEntity, ItemStack> interactSource = SourceChain.find(SourceChain.Comment.SOURCE_ENTITY_INTERACT);
					if(interactSource != null) {
						logEvent(new LivingEntityInteractBlockEvent(new Date(), world.getRegistryKey().getValue(), entityPos(interactSource.getLeft()), entityProfile(interactSource.getLeft()), interactSource.getRight(), pos, state, prev));
					} else {
						BlockPos tickSource = SourceChain.find(SourceChain.Comment.SCHEDULED_TICK);
						if(tickSource != null) {
							logEvent(new BlockScheduledTickUpdateBlockEvent(new Date(), world.getRegistryKey().getValue(), Vec3d.ofCenter(pos), tickSource, world.getBlockState(tickSource), pos, state, prev));
						} else {
							BlockPos tickSource1 = SourceChain.find(SourceChain.Comment.RANDOM_TICK);
							if(tickSource1 != null) {
								logEvent(new BlockRandomTickUpdateBlockEvent(new Date(), world.getRegistryKey().getValue(), Vec3d.ofCenter(pos), tickSource1, world.getBlockState(tickSource1), pos, state, prev));
							} else {
								BlockPos tickSource2 = SourceChain.find(SourceChain.Comment.FLUID_TICK);
								if(tickSource2 != null) {
									logEvent(new BlockFluidTickUpdateBlockEvent(new Date(), world.getRegistryKey().getValue(), Vec3d.ofCenter(pos), tickSource2, world.getBlockState(tickSource2), pos, state, prev));
								} else {
									logEvent(new EnvironmentBlockStateChangeEvent(new Date(), world.getRegistryKey().getValue(), Vec3d.ofCenter(pos), pos, state, prev, "unknown"));
								}
							}
						}
					}
				}
			}
		}
	}
}
