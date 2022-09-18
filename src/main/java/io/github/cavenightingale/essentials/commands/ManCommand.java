package io.github.cavenightingale.essentials.commands;

import static io.github.cavenightingale.essentials.Essentials.LOGGER;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import io.github.cavenightingale.essentials.utils.text.TextCompiler;
import io.github.cavenightingale.essentials.utils.text.TextCompiler.TextSyntaxException;
import io.github.cavenightingale.essentials.utils.text.TextLibrary;
import io.github.cavenightingale.essentials.utils.text.TextRuntime;

public class ManCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(CommandManager.literal("man").requires(s -> hasManPage()).executes(s -> execMan(null, s.getSource()))
				.then(CommandManager.argument("entry", StringArgumentType.string()).suggests(ManCommand::suggestManPage).executes(s -> execMan(StringArgumentType.getString(s, "entry"), s.getSource()))));
		dispatcher.register(CommandManager.literal("manview")
				.then(CommandManager.argument("text", StringArgumentType.greedyString()).executes(s -> execManView(StringArgumentType.getString(s, "text"), s.getSource()))));
	}

	static boolean hasManPage() {
		return false;
	}

	static int execMan(@Nullable String entry, @NotNull ServerCommandSource src) {
		// TODO: implement
		return 1;
	}

	static CompletableFuture<Suggestions> suggestManPage(final CommandContext<ServerCommandSource> context, final SuggestionsBuilder builder) {
		return builder.buildFuture();
	}

	static int execManView(String text, ServerCommandSource src) {
		try {
			TextRuntime env = new TextRuntime().applyModifier(TextLibrary::openStyles);
			src.sendFeedback((TextCompiler.compile(text).execute(env, null).toText()), false);
		} catch(TextRuntime.TextRuntimeException | TextSyntaxException ex) {
			src.sendError(new LiteralText(ex.getMessage()));
			LOGGER.info("failed to execute command", ex);
		}
		return 1;
	}
}
