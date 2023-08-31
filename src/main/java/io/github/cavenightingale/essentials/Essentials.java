package io.github.cavenightingale.essentials;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.cavenightingale.essentials.commands.TpaCommand;
import io.github.cavenightingale.essentials.protect.GameEventLogger;
import io.github.cavenightingale.essentials.utils.Config;
import io.github.cavenightingale.essentials.utils.ServerTranslation;
import io.github.cavenightingale.essentials.utils.Warps;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Essentials implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("ServerEssentials");
	public static final Gson GSON = new GsonBuilder().setPrettyPrinting()
			.registerTypeAdapter(Warps.Warp.class, new Warps.Adapter())
			.registerTypeAdapter(ServerTranslation.Node.class, new ServerTranslation.Adapter()).create();
	public static GameRules.Key<GameRules.BooleanRule> gameruleCreeperGriefing;
	public static GameRules.Key<GameRules.BooleanRule> gameruleEndermanGriefing;
	public static GameRules.Key<GameRules.BooleanRule> gameruleGhastGriefing;

	@Override
	public void onInitialize() {
		if(Config.config.gameLogEnabled) {
			GameEventLogger.init();
			LOGGER.warn("*** Game Logging is still in early development ***");
		}
		CommandRegistrationCallback.EVENT.register(EssentialsCommands::onCommandRegister);
		ServerLifecycleEvents.SERVER_STARTED.register(EssentialsCommands::loadPermission);
		ServerTickEvents.END_SERVER_TICK.register(TpaCommand::tick);
		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, _ignored0, _ignored1) -> EssentialsCommands.loadPermission(server));
		
		gameruleCreeperGriefing = GameRuleRegistry.register("creeperGriefing", GameRules.Category.MOBS, GameRuleFactory.createBooleanRule(true));
		gameruleEndermanGriefing = GameRuleRegistry.register("endermanGriefing", GameRules.Category.MOBS, GameRuleFactory.createBooleanRule(true));
		gameruleGhastGriefing = GameRuleRegistry.register("ghastGriefing", GameRules.Category.MOBS, GameRuleFactory.createBooleanRule(true));
	}
}
