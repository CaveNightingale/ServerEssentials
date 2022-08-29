package io.github.cavenightingale.essentials.protect.database.event;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.Date;

public interface LoggedEvent {
	/**
	 * @return The type string for serialize
	 * */
	default String type() {
		return getClass().getSimpleName();
	}

	/**
	 * @return when does this happened, actually where the source entity stand
	 * */
	Date date();

	/**
	 * @return Which world does this event happened in
	 * */
	Identifier world();

	/**
	 * @return The position of the event
	 * */
	Vec3d location();
}
