package io.github.cavenightingale.essentials.protect.database;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import io.github.cavenightingale.essentials.protect.database.event.LoggedEvent;
import io.github.cavenightingale.essentials.protect.event.*;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

import java.util.HashMap;
import java.util.Map;

public class LoggedEventRegistry {
	static Map<String, Class<? extends LoggedEvent>> registeredEventClass = new HashMap<>();
	public static void register(Class<? extends LoggedEvent> child) {
		register(child.getSimpleName(), child);
	}

	/**
	 * @param name The name of the type, must match the string returned by {@link LoggedEvent#type()}
	 * */
	public static void register(String name, Class<? extends LoggedEvent> child) {
		if(registeredEventClass.containsKey(name))
			throw new IllegalStateException(name + " has been already registered as " + registeredEventClass.get(name));
		registeredEventClass.put(name, child);
	}

	static {
		register(BlockDamageEntityEvent.class);
		register(BlockNeighbourUpdateBlockEvent.class);
		register(BlockRandomTickUpdateBlockEvent.class);
		register(BlockScheduledTickUpdateBlockEvent.class);
		register(BlockFluidTickUpdateBlockEvent.class);
		register(ClientJoinEvent.class);
		register(ClientChatEvent.class);
		register(ClientQuitEvent.class);
		register(EnvironmentBlockStateChangeEvent.class);
		register(EnvironmentDamageEntityEvent.class);
		register(LivingEntitySpawnEvent.class);
		register(LivingEntityDieEvent.class);
		register(LivingEntityDespawnEvent.class);
		register(LivingEntityInteractLivingEntityEvent.class);
		register(LivingEntityDamageLivingEntityEvent.class);
		register(LivingEntityPlaceBlockEvent.class);
		register(LivingEntityInteractBlockEvent.class);
		register(LivingEntityBreakBlockEvent.class);
	}
}
