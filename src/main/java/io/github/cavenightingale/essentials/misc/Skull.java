package io.github.cavenightingale.essentials.misc;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import static io.github.cavenightingale.essentials.utils.ServerTranslation.formats;

public class Skull {
	static CompletableFuture<ItemStack> createSkull(GameProfile profile) {
		CompletableFuture<ItemStack> itemFuture = new CompletableFuture<>();
		SkullBlockEntity.loadProperties(profile, profile1 -> {
			NbtCompound compound = new NbtCompound();
			compound.put("SkullOwner", NbtHelper.writeGameProfile(new NbtCompound(), profile1));
			ItemStack stack = new ItemStack(Items.PLAYER_HEAD);
			stack.setNbt(compound);
			itemFuture.complete(stack);
		});
		return itemFuture;
	}

	public static void give(Collection<GameProfile> profiles, ServerPlayerEntity entity, @Nullable ServerCommandSource src) {
		for(GameProfile profile : profiles) {
			createSkull(profile).thenAccept(itemStack -> {
				if (!entity.getInventory().insertStack(itemStack)) {
					entity.world.spawnEntity(new ItemEntity(entity.world, entity.getX(), entity.getY(), entity.getZ(), itemStack));
				}
				if (src != null) {
					src.sendFeedback(formats.skullGiven.format(entity.getGameProfile().getName(), profile.getName()), true);
				}
			});
		}
	}
}
