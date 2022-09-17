package io.github.cavenightingale.essentials;

import static io.github.cavenightingale.essentials.Essentials.LOGGER;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import com.google.gson.reflect.TypeToken;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;

import io.github.cavenightingale.essentials.commands.EssPermCommand;
import io.github.cavenightingale.essentials.commands.HomeCommand;
import io.github.cavenightingale.essentials.commands.MiscCommand;
import io.github.cavenightingale.essentials.commands.ServerEssentialsCommand;
import io.github.cavenightingale.essentials.commands.TpaCommand;
import io.github.cavenightingale.essentials.commands.WarpCommand;
import io.github.cavenightingale.essentials.utils.CommandNodeWithPermission;

public class EssentialsCommands {
	public static void onCommandRegister(CommandDispatcher<ServerCommandSource> dispatcher, boolean b) {
		WarpCommand.register(dispatcher);
		HomeCommand.register(dispatcher);
		TpaCommand.register(dispatcher);
		MiscCommand.register(dispatcher);
		EssPermCommand.register(dispatcher);
		ServerEssentialsCommand.register(dispatcher);
		registerAliases(dispatcher,new String[][]{
				{"tpask", "tpa"},
				{"tpyes", "tpaccept"},
				{"tpno", "tpdeny"},
		});
	}

	public static void registerAliases(CommandDispatcher<ServerCommandSource> dispatcher, String[][]args) {
		for(String[] alias: args) {
			dispatcher.register(CommandManager.literal(alias[0]).redirect(dispatcher.getRoot().getChild(alias[1])));
		}
	}

	public static void loadPermission(MinecraftServer server) {
		// does not apply this in onInitialize to ensure commands are fully registered
		CommandNode<?> root = server.getCommandManager().getDispatcher().getRoot();
		try (Reader reader = new BufferedReader(new FileReader("config/Essentials/permission.json", StandardCharsets.UTF_8))) {
			HashMap<String, String> perms = Essentials.GSON.fromJson(reader, new TypeToken<HashMap<String, String>>(){}.getType());
			for(Map.Entry<String, String> permNode : perms.entrySet()) {
				setPerms(root, permNode.getKey(), permNode.getValue());
			}
		} catch (IOException ignored) {
		}
	}

	public static void setPerms(CommandNode<?> root, String path, String perm) {
		String[] nodes = path.split("\\.");
		CommandNode<?> node = root;
		for(String s : nodes) {
			node = node.getChild(s);
			if (node == null)
				return;
		}
		((CommandNodeWithPermission) node).serveressential_setPermission(perm);
	}

	private static void saveCommandPermissions(CommandNode<?> node, String path, HashMap<String, String> map) {
		String perm = ((CommandNodeWithPermission)node).serveressential_getPermission();
		if(perm != null) {
			map.put(path, perm);
		}
		for(CommandNode<?>  child : node.getChildren()) {
			saveCommandPermissions(child, path + "." + child.getName(), map);
		}
	}

	public static void saveCommandPermissions(MinecraftServer server) {
		CommandNode<?> root = server.getCommandManager().getDispatcher().getRoot();
		HashMap<String, String> map = new HashMap<>();
		for(CommandNode<?>  child : root.getChildren()) {
			saveCommandPermissions(child, child.getName(), map);
		}
		try (Writer writer = new BufferedWriter(new FileWriter("config/Essentials/permission.json", StandardCharsets.UTF_8))) {
			Essentials.GSON.toJson(map, writer);
		} catch (IOException ex) {
			LOGGER.error("Can't save command permissions");
		}
	}

	public static void resendCommandTree(MinecraftServer server) {
		PlayerManager playerManager = server.getPlayerManager();
		for(ServerPlayerEntity player : playerManager.getPlayerList()) {
			playerManager.sendCommandTree(player);
		}
	}
}
