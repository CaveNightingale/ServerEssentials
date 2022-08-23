package io.github.cavenightingale.essentials;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.cavenightingale.essentials.commands.TpaCommand;
import io.github.cavenightingale.essentials.utils.ServerTranslation;
import io.github.cavenightingale.essentials.utils.Warps;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.block.Block;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
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

	public static final TagKey<Block> SEAT_TAG = TagKey.of(Registry.BLOCK_KEY, new Identifier("essentials", "seat"));

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register(EssentialsCommands::onCommandRegister);
		ServerLifecycleEvents.SERVER_STARTED.register(EssentialsCommands::onServerStarted);
		ServerTickEvents.END_SERVER_TICK.register(TpaCommand::tick);

		gameruleCreeperGriefing = GameRuleRegistry.register("creeperGriefing", GameRules.Category.MOBS, GameRuleFactory.createBooleanRule(true));
		gameruleEndermanGriefing = GameRuleRegistry.register("endermanGriefing", GameRules.Category.MOBS, GameRuleFactory.createBooleanRule(true));
		gameruleGhastGriefing = GameRuleRegistry.register("ghastGriefing", GameRules.Category.MOBS, GameRuleFactory.createBooleanRule(true));
	}
}
