package io.github.cavenightingale.essentials.utils.text;

import org.jetbrains.annotations.Nullable;

/**
 * Representing a basic renderable region, including blocks and arguments
 */
@FunctionalInterface
public interface TextFunction {
	TextLike execute(TextRuntime environment, @Nullable TextFunction callback, TextFunction... args)
			throws TextRuntime.TextRuntimeException;

	public static TextFunction.ConstantTextFunction ofConst(TextLike text) {
		return new ConstantTextFunction(text);
	}

	/**
	 * A special type of function
	 */
	public static record ConstantTextFunction(TextLike text) implements TextFunction {
		@Override
		public TextLike execute(TextRuntime environment, @Nullable TextFunction callback,
				TextFunction... args) {
			return text;
		}
	}
}