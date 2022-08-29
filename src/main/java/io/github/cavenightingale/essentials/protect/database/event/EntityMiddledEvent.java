package io.github.cavenightingale.essentials.protect.database.event;

import com.mojang.authlib.GameProfile;
import org.jetbrains.annotations.Nullable;

public interface EntityMiddledEvent extends LoggedEvent {
	/**
	 * @return The middle entity, such as wolves, arrows, tnts, blaze fireballs
	 */
	@Nullable GameProfile directEntity();
}
