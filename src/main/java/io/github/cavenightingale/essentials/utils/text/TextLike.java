package io.github.cavenightingale.essentials.utils.text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.visitor.NbtTextFormatter;
import net.minecraft.nbt.visitor.StringNbtWriter;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.text.HoverEvent.EntityContent;
import net.minecraft.text.HoverEvent.ItemStackContent;

import com.mongodb.lang.Nullable;

public interface TextLike {
	MutableText toText();

	default TextLike apply(Function<MutableText, MutableText> func) {
		return text(func.apply(toText()));
	}

	/**
	 * This method returns a list of lines, each line is a Text
	 * TODO: testing required
	 */
	default List<Text> toLines() {
		List<Text> flatten = new ArrayList<>();
		flat(flatten, toText());
		List<List<Text>> lines = split(flatten, "\n");
		List<Text> result = new ArrayList<>();
		for (List<Text> line : lines) {
			LiteralText root = new LiteralText("");
			for (Text t : line) {
				root.append(t);
			}
			result.add(root);
		}
		return result;
	}

	/**
	 * This method returns a list of pages
	 * TODO: testing required
	 */
	default List<List<Text>> toPages() {
		List<Text> flatten = new ArrayList<>();
		flat(flatten, toText());
		List<List<Text>> result = new ArrayList<>();
		for (List<Text> page : split(flatten, "\f")) {
			List<Text> roots = new ArrayList<>();
			for (List<Text> line : split(page, "\f")) {
				LiteralText root = new LiteralText("");
				for (Text t : line) {
					root.append(t);
				}
				roots.add(root);
			}
			result.add(roots);
		}
		return result;
	}

	/**
	 * This class repesent a raw string
	 */
	public static record StringText(String string) implements TextLike {
		@Override
		public MutableText toText() {
			return new LiteralText(string);
		}

		@Override
		public String toString() {
			return string;
		}
	}

	/**
	 * This class represent entity information that can be used inhover event 
	 */
	public static record EntityTextLike(EntityContent content) implements TextLike {
		@Override
		public MutableText toText() {
			return content.name.shallowCopy();
		}

		@Override
		public String toString() {
			return content.name.getString();
		}

		@Nullable
		public Entity getEntity(MinecraftServer server) {
			for (ServerWorld world : server.getWorlds()) {
				Entity entity = world.getEntity(content.uuid);
				if (entity != null) {
					return entity;
				}
			}
			return null;
		}

		public boolean isPresent(MinecraftServer server) {
			return getEntity(server) != null;
		}
	}
	/**
	 * This class represent itemstack information that can be used inhover event 
	 */
	public static record ItemStackTextLike(ItemStackContent content) implements TextLike {
		public ItemStackTextLike(ItemStack stack) {
			this(new ItemStackContent(stack));
		}

		public ItemStack stack() {
			return content.asStack();
		}

		@Override
		public MutableText toText() {
			MutableText text = new TranslatableText(stack().getTranslationKey());
			return stack().getCount() == 1 ? text
					: text.append("*" + stack().getCount())
							.styled(s -> s.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, content)));
		}

		@Override
		public String toString() {
			return toText().getString();
		}
	}

	/**
	 * This class represent an alreadg-generated Text
	 */
	public static record MinecraftTextLike(Text text) implements TextLike {
		@Override
		public MutableText toText() {
			return text.shallowCopy();
		}

		@Override
		public String toString() {
			return text.getString();
		}
	}

	/**
	 * Used to hold nbt
	 */
	public static record NbtTextLike(NbtElement nbt) implements TextLike {
		@Override
		public MutableText toText() {
			return new NbtTextFormatter("", 0).apply(nbt).shallowCopy();
		}

		@Override
		public String toString() {
			return new StringNbtWriter().apply(nbt);
		}
	}

	/**
	 * This method return a TextLike to Text
	 * 
	 * @param text the text
	 * @return a view of the text
	 */
	static StringText string(String text) {
		return new StringText(text);
	}

	static EntityTextLike entity(EntityContent content) {
		return new EntityTextLike(content);
	}

	static EntityTextLike entity(Entity entity) {
		return entity(new EntityContent(entity.getType(), entity.getUuid(), entity.getDisplayName()));
	}

	static MinecraftTextLike text(Text text) {
		return new MinecraftTextLike(text);
	}

	static NbtTextLike nbt(NbtElement element) {
		return new NbtTextLike(element);
	}

	private static void flat(List<Text> list, Text text) {
		MutableText self = text.copy();
		if (!(self instanceof LiteralText literal && literal.getRawString().isEmpty())) {
			self.setStyle(text.getStyle());
			list.add(self);
		}
		for (Text sibling : text.getSiblings()) {
			flat(list, sibling);
		}
	}

	private static List<List<Text>> split(List<Text> texts, String seq) {
		ArrayList<List<Text>> result = new ArrayList<>();
		ArrayList<Text> buffer = new ArrayList<>();
		for (Text text : texts) {
			if (text instanceof LiteralText literal && literal.getRawString().contains(seq)) {
				String[] lines = literal.getRawString().split(seq, Integer.MAX_VALUE);
				buffer.add(new LiteralText(lines[0]).setStyle(literal.getStyle()));
				for (int i = 1; i < lines.length; i++) {
					result.add(buffer);
					buffer = new ArrayList<>();
					buffer.add(new LiteralText(lines[i]).setStyle(literal.getStyle()));
				}
			}
		}
		if (!buffer.isEmpty()) {
			result.add(buffer);
		}
		return result;
	}
}
