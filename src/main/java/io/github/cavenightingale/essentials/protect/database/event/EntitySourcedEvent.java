package io.github.cavenightingale.essentials.protect.database.event;

import com.mojang.authlib.GameProfile;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface EntitySourcedEvent extends LoggedEvent {
	/**
	 * @return The Entity source, such as attackers, miners
	 *          The game profile of a non-player entity consists of its type and uuid
	 * */
	GameProfile sourceEntity();

	/**
	 * @return The weapon entity used
	 * */
	@Nullable default ItemStack weapon() {
		return null;
	}
}
