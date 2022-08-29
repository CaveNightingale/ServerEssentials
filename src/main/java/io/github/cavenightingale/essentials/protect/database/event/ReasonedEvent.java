package io.github.cavenightingale.essentials.protect.database.event;

public interface ReasonedEvent extends LoggedEvent{
	/**
	 * @return The reason of the event
	 * */
	String reason();
}
