package io.github.cavenightingale.essentials.protect.database;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import static io.github.cavenightingale.essentials.Essentials.LOGGER;

public class LogErrorSubscriber<T> implements Subscriber<T> {
	private final String message;

	public LogErrorSubscriber(String message) {
		this.message = message;
	}


	@Override
	public void onSubscribe(Subscription s) {
	}

	@Override
	public void onNext(T item) {
	}

	@Override
	public void onError(Throwable throwable) {
		LOGGER.error(message, throwable);
	}

	@Override
	public void onComplete() {
	}
}
