package io.github.cavenightingale.essentials.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.cavenightingale.essentials.sit.SeatEntity;
import io.github.cavenightingale.essentials.utils.Config;
import io.github.cavenightingale.essentials.utils.PlayerData;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;

import static io.github.cavenightingale.essentials.utils.CommandPredicates.player;
import static io.github.cavenightingale.essentials.utils.ServerTranslation.formats;

public class MiscCommand {

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(CommandManager.literal("afk").requires(player(0)).executes(s -> execAfk(s.getSource().getPlayer())));
		dispatcher.register(CommandManager.literal("hat").requires(player(0)).executes(s -> execHat(s.getSource().getPlayer())));
		dispatcher.register(CommandManager.literal("ping").requires(player(0)).executes(s -> execPing(s.getSource())));
		dispatcher.register(CommandManager.literal("fly").requires(player(1)).executes(s -> execFly(s.getSource())));
		dispatcher.register(CommandManager.literal("sit").requires(s -> Config.config.sitEnabled).executes(s -> execSit(s.getSource(), 0.0))
				.then(CommandManager.argument("offset", DoubleArgumentType.doubleArg()).requires(player(2)).executes(s -> execSit(s.getSource(), DoubleArgumentType.getDouble(s, "offset")))));
	}

	static int execAfk(ServerPlayerEntity src) {
		PlayerData data = PlayerData.get(src);
		data.setServerEssentials_afk(!data.isServerEssentials_afk());
		return 0;
	}
	static int execHat(ServerPlayerEntity player) {
		ItemStack hand = player.getMainHandStack(), head = player.getInventory().getArmorStack(3);
		player.getInventory().setStack(player.getInventory().selectedSlot, head);
		player.getInventory().armor.set(3, hand);
		return 1;
	}

	static int execPing(ServerCommandSource src) throws CommandSyntaxException {
		src.sendFeedback(new LiteralText("Ping: " + src.getPlayer().pingMilliseconds + "ms"), false);
		return src.getPlayer().pingMilliseconds;
	}

	static int execFly(ServerCommandSource src) throws CommandSyntaxException {
		ServerPlayerEntity player = src.getPlayer();
		if(player.interactionManager.getGameMode().isSurvivalLike()) {
			PlayerAbilities abilities = player.getAbilities();
			abilities.allowFlying = !abilities.allowFlying;
			player.sendAbilitiesUpdate();
			src.sendFeedback((abilities.allowFlying ? formats.miscFlyEnabled : formats.miscFlyDisabled).format(), true);
			return abilities.allowFlying ? 1 : 2;
		} else {
			src.sendError(formats.miscFlyUnsupported.format());
			return 0;
		}
	}

	static int execSit(ServerCommandSource src, double offset) throws CommandSyntaxException {
		if(!SeatEntity.sit(src.getPlayer(), new BlockPos(src.getPosition()), offset)) {
			src.sendError(formats.miscSitFailed.format());
			return 0;
		}
		return 1;
	}
}
