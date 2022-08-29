package io.github.cavenightingale.essentials.protect.database.event;

import com.mojang.authlib.GameProfile;

public interface EntityTargetedEvent extends LoggedEvent {
	/**
	 * @return The effect of the event, may be same as {@link EntitySourcedEvent#sourceEntity()} from events like teleports.
	 * */
	GameProfile targetEntity();

	/**
	 * @return The damage caused
	 * */
	default double damage() {
		return 0.0;
	}
}
