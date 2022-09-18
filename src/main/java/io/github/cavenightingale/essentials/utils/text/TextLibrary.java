package io.github.cavenightingale.essentials.utils.text;

import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class TextLibrary {
	public static void openStyles(TextRuntime runtime) {
		runtime.put("translate", (env, cb, args) -> TextLike.text(new TranslatableText(env.invoke(cb).toString())));
		runtime.put("color", (env, cb, args) -> {
			if (args.length != 1) {
				throw new TextRuntime.TextRuntimeException("Too many or too few arguments for \\color(color){}");
			}
			TextColor color = TextColor.parse(env.invoke(args[0]).toString().trim());
			return env.invoke(cb).apply(t -> t.styled(s -> s.withColor(color)));
		});
		runtime.put("format", (env, cb, args) -> {
			TextLike result = env.invoke(cb);
			for (TextFunction arg : args) {
				switch (env.invoke(arg).toString().trim()) {
					case "obfuscated" -> result.apply(t -> t.formatted(Formatting.OBFUSCATED));
					case "bold" -> result.apply(t -> t.formatted(Formatting.BOLD));
					case "strikethrough" -> result.apply(t -> t.formatted(Formatting.STRIKETHROUGH));
					case "underline" -> result.apply(t -> t.formatted(Formatting.UNDERLINE));
					case "italic" -> result.apply(t -> t.formatted(Formatting.ITALIC));
					default -> throw new TextRuntime.TextRuntimeException("Unknown formatting");
				}
			}
			return result;
		});
		// put all the formatting characters
		for (Formatting formatting : Formatting.values()) {
			runtime.put(String.valueOf(formatting.getCode()), (env, cb, args) -> {
				return env.invoke(cb).apply(t -> t.formatted(formatting));
			});
		}
		// click events
		for (ClickEvent.Action clickAction : ClickEvent.Action.values()) {
			runtime.put(clickAction.getName(), (env, cb, args) -> {
				if (args.length != 1) {
					throw new TextRuntime.TextRuntimeException(
							"Too many or too few arguments for \\" + clickAction.getName() + "(value){}");
				}
				String value = env.invoke(args[0]).toString();
				return env.invoke(cb)
						.apply(t -> t.styled(s -> s.withClickEvent(new ClickEvent(clickAction, value))));
			});
		}
		// hover events
		runtime.put("hover_text", (env, cb, args) -> {
			if (args.length != 1) {
				throw new TextRuntime.TextRuntimeException("Too many or too few arguments for \\hover_text(value){}");
			}
			Text text = env.invoke(args[0]).toText();
			return env.invoke(cb)
					.apply(t -> t.styled(s -> s.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, text))));
		});
		runtime.put("hover_entity", (env, cb, args) -> {
			if (args.length != 1) {
				throw new TextRuntime.TextRuntimeException("Too many or too few arguments for \\hover_entity(value){}");
			}
			if (env.invoke(args[0]) instanceof TextLike.EntityTextLike entity) {
				return env.invoke(cb).apply(t -> t.styled(
						s -> s.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ENTITY, entity.content()))));
			} else {
				throw new TextRuntime.TextRuntimeException("The value of hover_entity must be entity");
			}
		});
		runtime.put("hover_item", (env, cb, args) -> {
			if (args.length != 1) {
				throw new TextRuntime.TextRuntimeException("Too many or too few arguments for \\hover_entity(value){}");
			}
			if (env.invoke(args[0]) instanceof TextLike.ItemStackTextLike item) {
				return env.invoke(cb).apply(t -> t.styled(
						s -> s.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, item.content()))));
			} else {
				throw new TextRuntime.TextRuntimeException("The value of hover_item must be entity");
			}
		});
	}

	public static void openPages(TextRuntime runtime) {
		runtime.putVar("line", TextLike.string("\n"));
		runtime.putVar("page", TextLike.string("\f"));
	}
}
