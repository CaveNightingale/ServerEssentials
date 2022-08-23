package io.github.cavenightingale.essentials.sit;

import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.UUID;

public class SeatEntity extends ArmorStandEntity {

	private final BlockPos seatPos;
	private final BlockState seatState;

	public static final HashMap<BlockPos, UUID> sittingPlayer = new HashMap<>();
	public SeatEntity(EntityType<? extends ArmorStandEntity> entityType, World world, BlockPos pos) {
		super(entityType, world);
		seatPos = pos;
		seatState = world.getBlockState(seatPos);
	}

	public static boolean isSeat(BlockState state) {
		if(state.getBlock() instanceof SlabBlock) {
			return state.get(SlabBlock.TYPE) == SlabType.BOTTOM;
		} else if(state.getBlock() instanceof StairsBlock) {
			return state.get(StairsBlock.HALF) == BlockHalf.BOTTOM;
		}
		return false;
	}

	public static boolean sit(ServerPlayerEntity player, BlockPos pos, double offset) {
		assert player.world.getServer() != null;
		if(!player.isOnGround())
			return false;
		if(player.getVehicle() != null)
			return false;
		UUID prev = sittingPlayer.get(pos);
		if(prev != null) {
			ServerPlayerEntity prevPlayer = player.world.getServer().getPlayerManager().getPlayer(prev);
			if(prevPlayer != null) {
				prevPlayer.stopRiding();
			}
		}
		SeatEntity entity = new SeatEntity(EntityType.ARMOR_STAND, player.world, pos);
		entity.setNoGravity(true);
		Vec3d pos1 = Vec3d.ofCenter(pos);
		entity.teleport(pos1.getX(), pos1.getY() - 2.2 - offset, pos1.getZ());
		entity.setInvisible(true);
		player.world.spawnEntity(entity);
		player.startRiding(entity, true);
		return true;
	}

	@Override
	public boolean shouldSave() {
		return false;
	}

	// to avoid /kill affect
	@Override
	public boolean damage(DamageSource source, float amount) {
		return false;
	}

	@Override
	public void travel(Vec3d movementInput) {
	}

	@Override
	public void tick() {
		// check if the seat still exists, does not hook setBlockState for performance reason
		if(!world.getBlockState(seatPos).equals(seatState)) {
			getPassengerList().forEach(Entity::stopRiding);
		}
		if(getPassengerList().isEmpty()) {
			setRemoved(RemovalReason.KILLED);
			sittingPlayer.remove(seatPos);
		}
	}
}
