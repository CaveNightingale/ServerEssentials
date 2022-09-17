package io.github.cavenightingale.essentials.protect.database;

import java.util.HashMap;
import java.util.Map;

import io.github.cavenightingale.essentials.protect.database.event.LoggedEvent;
import io.github.cavenightingale.essentials.protect.event.BlockDamageEntityEvent;
import io.github.cavenightingale.essentials.protect.event.BlockFluidTickUpdateBlockEvent;
import io.github.cavenightingale.essentials.protect.event.BlockNeighbourUpdateBlockEvent;
import io.github.cavenightingale.essentials.protect.event.BlockRandomTickUpdateBlockEvent;
import io.github.cavenightingale.essentials.protect.event.BlockScheduledTickUpdateBlockEvent;
import io.github.cavenightingale.essentials.protect.event.ClientChatEvent;
import io.github.cavenightingale.essentials.protect.event.ClientJoinEvent;
import io.github.cavenightingale.essentials.protect.event.ClientQuitEvent;
import io.github.cavenightingale.essentials.protect.event.EnvironmentBlockStateChangeEvent;
import io.github.cavenightingale.essentials.protect.event.EnvironmentDamageEntityEvent;
import io.github.cavenightingale.essentials.protect.event.LivingEntityBreakBlockEvent;
import io.github.cavenightingale.essentials.protect.event.LivingEntityDamageLivingEntityEvent;
import io.github.cavenightingale.essentials.protect.event.LivingEntityDespawnEvent;
import io.github.cavenightingale.essentials.protect.event.LivingEntityDieEvent;
import io.github.cavenightingale.essentials.protect.event.LivingEntityInteractBlockEvent;
import io.github.cavenightingale.essentials.protect.event.LivingEntityInteractLivingEntityEvent;
import io.github.cavenightingale.essentials.protect.event.LivingEntityPlaceBlockEvent;
import io.github.cavenightingale.essentials.protect.event.LivingEntitySpawnEvent;

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
