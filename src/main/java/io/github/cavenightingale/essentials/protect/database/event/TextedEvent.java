package io.github.cavenightingale.essentials.protect.database.event;

public interface TextedEvent extends LoggedEvent {
	/**
	 * @return What the client send or send to client
	 * */
	String text();
}
