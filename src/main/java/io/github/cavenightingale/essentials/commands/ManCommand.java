package io.github.cavenightingale.essentials.commands;

import static io.github.cavenightingale.essentials.Essentials.LOGGER;
import static io.github.cavenightingale.essentials.utils.CommandPredicates.opLevel;
import static io.github.cavenightingale.essentials.utils.ServerTranslation.formats;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

import io.github.cavenightingale.essentials.utils.text.*;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
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

public class ManCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(CommandManager.literal("man").executes(s -> execMan(null, s.getSource(), "_index"))
				.then(CommandManager.argument("entry", StringArgumentType.string()).suggests(ManCommand::suggestManPage).executes(s -> execMan(StringArgumentType.getString(s, "entry"), s.getSource(), "_index"))));
		dispatcher.register(CommandManager.literal("manview").requires(opLevel(2))
				.then(CommandManager.argument("text", StringArgumentType.greedyString()).executes(s -> execManView(StringArgumentType.getString(s, "text"), s.getSource()))));
	}

	final static Pattern ENTRY_REGEX = Pattern.compile("^[a-z|0-9|\\\\.]*$");

	static boolean hasManPage() {
		return false;
	}

	static int execMan(@Nullable String entry, @NotNull ServerCommandSource src, String index) {
		CompletableFuture.runAsync(() -> {
			String actualEntry = entry;
			String actualIndex = index;
			if(actualEntry == null) {
				actualEntry = "";
			}
			if(!ENTRY_REGEX.matcher(actualEntry).matches() || actualEntry.startsWith(".")) {
				src.sendError(formats.manIllegalPath.format());
			} else {
				actualEntry = "/" + actualEntry.replace('.', '/');
				while(true) {
					Path path = Path.of("config/Essentials/manpages", actualEntry, actualIndex + ".txt");
					if(path.toFile().isFile()) {
						try(InputStream stream = new BufferedInputStream(new FileInputStream(path.toFile()))) {
							String shownEntry = actualEntry.substring(1).replace('/', '.');
							Entity sourceEntity = src.getEntity();
							TextRuntime env = new TextRuntime().applyModifier(TextLibrary::openStyles, TextLibrary::openPages, child -> {
								child.putVar("wantedEntry", TextLike.string(entry));
								child.putVar("shownEntry", TextLike.string(shownEntry));
								child.putVar("sourceEntity", sourceEntity != null ? TextLike.entity(sourceEntity) : TextLike.string("anonymous"));
							});
							List<Text> lines = TextCompiler.compile(new String(stream.readAllBytes(), StandardCharsets.UTF_8)).execute(env, null).toLines();
							for(Text line : lines) {
								src.sendFeedback(line, false);
							}
						} catch(IOException | TextRuntime.TextRuntimeException | TextCompiler.TextSyntaxException ex) {
							src.sendError(formats.manFailed.format());
							LOGGER.info("failed to load page " + actualEntry, ex);
						}
						return;
					} else {
						Path parent = Path.of(actualEntry).getParent();
						if(parent == null) {
							src.sendError(formats.manNotFound.format());
							return;
						} else {
							actualEntry = parent.toString();
							actualIndex = "_notfound";
						}
					}
				}
			}
		});
		return 1;
	}

	static CompletableFuture<Suggestions> suggestManPage(final CommandContext<ServerCommandSource> context, final SuggestionsBuilder builder) {
		return builder.buildFuture();
	}

	static int execManView(String text, ServerCommandSource src) {
		try {
			TextRuntime env = new TextRuntime().applyModifier(TextLibrary::openStyles, TextLibrary::openPages);
			List<Text> lines = TextCompiler.compile(text).execute(env, null).toLines();
			for(Text line : lines) {
				src.sendFeedback(line, false);
			}
		} catch(TextRuntime.TextRuntimeException | TextCompiler.TextSyntaxException ex) {
			src.sendError(new LiteralText(ex.getMessage()));
			LOGGER.info("failed to execute command", ex);
		}
		return 1;
	}
}
