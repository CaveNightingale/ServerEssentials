package io.github.cavenightingale.essentials.protect.event;

import com.mojang.authlib.GameProfile;
import io.github.cavenightingale.essentials.protect.database.event.BlockTargetedEvent;
import io.github.cavenightingale.essentials.protect.database.event.EntitySourcedEvent;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Date;

public record LivingEntityPlaceBlockEvent(
		Date date,
        Identifier world,
        Vec3d location,
        GameProfile sourceEntity,
        ItemStack weapon,
        BlockPos targetBlockPos,
        BlockState targetBlockState,
        BlockState previousBlockState
) implements EntitySourcedEvent, BlockTargetedEvent {
}
