package io.github.cavenightingale.essentials.utils.text;

import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import org.jetbrains.annotations.Nullable;

/**
 * Representing a basic renderable region, including blocks and arguments
 */
@FunctionalInterface
public interface TextFunction {
	TextLike execute(TextRuntime environment, @Nullable TextFunction callback, TextFunction... args)
			throws TextRuntime.TextRuntimeException;

	static TextFunction.ConstantTextFunction ofConst(TextLike text) {
		return new ConstantTextFunction(text);
	}

	/**
	 * A special type of function
	 */
	record ConstantTextFunction(TextLike text) implements TextFunction {
		@Override
		public TextLike execute(TextRuntime environment, @Nullable TextFunction callback,
				TextFunction... args) {
			return text;
		}
	}

	/**
	 * A simple function to concat the result of other functions
	 * */
	record ConcatTextFunction(TextCompiler.FunctionContext... calls) implements TextFunction {
		@Override
		public TextLike execute(TextRuntime environment, @Nullable TextFunction callback, TextFunction... args)
				throws TextRuntime.TextRuntimeException {
			MutableText text = new LiteralText("");
			for (TextCompiler.FunctionContext call : calls)
				text.append(call.invoke(environment).toText());
			return TextLike.text(text);
		}
	}
}