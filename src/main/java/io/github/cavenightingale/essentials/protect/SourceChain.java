package io.github.cavenightingale.essentials.protect;

import io.github.cavenightingale.essentials.Essentials;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SourceChain<T> {
	private SourceChain<?> previous;
	private Comment<T> comment;
	private T value;
	private SourceChain(SourceChain<?> previous, Comment<T> comment, T value) {
		this.previous = previous;
		this.comment = comment;
		this.value = value;
	}
	public static class Comment<T> {
		public static final Comment<Pair<LivingEntity, ItemStack>> SOURCE_ENTITY_PLACE = new Comment<>();
		public static final Comment<Pair<LivingEntity, ItemStack>> SOURCE_ENTITY_BREAK = new Comment<>();
		public static final Comment<Pair<LivingEntity, ItemStack>> SOURCE_ENTITY_INTERACT = new Comment<>();
		public static final Comment<Entity> DIRECT_ENTITY = new Comment<>();
		public static final Comment<BlockPos> NEIGHBOUR_BLOCK = new Comment<>();
		public static final Comment<BlockPos> SCHEDULED_TICK = new Comment<>();
		public static final Comment<BlockPos> RANDOM_TICK = new Comment<>();
		public static final Comment<BlockPos> FLUID_TICK = new Comment<>();
	}

	private static SourceChain<?> chain = null;

	public static <T> void push(T data, @NotNull Comment<T> comment) {
		chain = new SourceChain<>(chain, comment, data);
	}

	public static void pop(@NotNull Comment<?> comment) {
		if(chain.comment != comment) {
			Essentials.LOGGER.warn("Unmatched chain push and pop", new Exception());
		}
		SourceChain<?> chain1 = chain;
		while (chain1 != null && chain1.comment != comment) {
			chain1 = chain1.previous;
		}
		if(chain1 != null) {
			chain = chain1.previous;
		}
	}

	@Nullable
	@SuppressWarnings("unchecked")
	public static <T> T find(@NotNull Comment<T> comment) {
		SourceChain<?> chain1 = chain;
		while (chain1 != null && chain1.comment != comment) {
			chain1 = chain1.previous;
		}
		return chain1 == null ? null : (T) chain1.value;
	}
}
