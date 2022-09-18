package io.github.cavenightingale.essentials.utils.text;

import java.util.ArrayList;

import org.jetbrains.annotations.Nullable;

import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;

import com.mojang.brigadier.StringReader;

/**
 * This class is used to preprocess minecraft text
 * We wants to make configure easier
 * '\' marks as a beginning of the function name, '{' marks as a start of the
 * callback and '}' mark as an end of the callback
 * '(' marks the beginning of the argument list and ')' marks the end of the
 * argument list, the argument list is optional
 * Rerference to a function without calling it only allowed when it's used as an
 * argument
 * Use '#' as comment
 * To print '\' to minecraft you can use '\\', so as '\{', '\}', '\(', '\)',
 * '\,', '\'is optional when it's not ambiguous
 * Examples
 * {@code \hover_text(hello world){\color(red){hello}}}
 */
public class TextCompiler {
	public static class TextSyntaxException extends Exception {
		public TextSyntaxException(String reason) {
			super(reason);
		}
	}

	static class TextFunctionReference {
		@Nullable
		String name;
		@Nullable
		TextFunction function;

		TextFunction get(TextRuntime env) throws TextRuntime.TextRuntimeException {
			if (function != null)
				return function;
			TextFunction func = env.get(name);
			if (func != null)
				return func;
			else
				throw new TextRuntime.TextRuntimeException("cannot find function " + name);
		}

		TextFunctionReference(String name) {
			this.name = name;
		}

		TextFunctionReference(TextFunction func) {
			this.function = func;
		}
	}

	static record FunctionContext(TextFunctionReference func, @Nullable TextFunction callback,
			TextFunction... args) {
		TextLike invoke(TextRuntime env) throws TextRuntime.TextRuntimeException {
			return func.get(env).execute(new TextRuntime(env), callback, args);
		}
	}

	static record ConcatTextFunction(FunctionContext... calls) implements TextFunction {
		@Override
		public TextLike execute(TextRuntime environment, @Nullable TextFunction callback, TextFunction... args)
				throws TextRuntime.TextRuntimeException {
			MutableText text = new LiteralText("");
			for (FunctionContext call : calls)
				text.append(call.invoke(environment).toText());
			return TextLike.text(text);
		}
	}

	private static String readFunctionName(StringReader reader) throws TextSyntaxException {
		StringBuilder sb = new StringBuilder();
		while (reader.canRead() && ((reader.peek() <= 'z' && reader.peek() >= 'a') ||
				(reader.peek() <= 'Z' && reader.peek() >= 'A') ||
				(reader.peek() <= '9' && reader.peek() >= '0') ||
				reader.peek() == '_' || reader.peek() == '$')) {
			sb.append(reader.peek());
			reader.skip();
		}
		check(reader);
		return sb.toString();
	}

	private static void check(StringReader reader) throws TextSyntaxException {
		if (!reader.canRead())
			throw new TextSyntaxException("Early end of file");
	}

	private static FunctionContext parseContext(String funcname, StringReader reader) throws TextSyntaxException {
		if (reader.peek() == '(') {
			ArrayList<TextFunction> innerFunction = new ArrayList<>();
			do {
				reader.skip();
				reader.skipWhitespace();
				check(reader);
				innerFunction.add(parseArgument(reader));
			} while (reader.peek() == ',');
			reader.skipWhitespace();
			check(reader);
			if (reader.peek() != ')')
				throw new TextSyntaxException("Unmatched (");
			reader.skip();
			reader.skipWhitespace();
			check(reader);
			if (reader.peek() != '{')
				throw new TextSyntaxException("Expect {");
			reader.skip();
			TextFunction callback = parseBlock(reader, false);// it will be skipped later
			return new FunctionContext(new TextFunctionReference(funcname), callback,
					innerFunction.toArray(new TextFunction[0]));
		} else if (reader.peek() == '{') {
			reader.skip();
			TextFunction callback = parseBlock(reader, false);
			check(reader);
			return new FunctionContext(new TextFunctionReference(funcname), callback);
		} else {
			throw new TextSyntaxException("Unexpected character " + reader.peek());
		}
	}

	private static TextFunction parseArgument(StringReader reader) throws TextSyntaxException {
		ArrayList<FunctionContext> contexts = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		check(reader);
		while (reader.peek() != ',' && reader.peek() != ')') {
			if (reader.peek() == '\\') {
				reader.skip();
				if (!reader.canRead())
					throw new TextSyntaxException("Early end of file");
				switch (reader.peek()) {
					case '\\', '{', '}', '(', ')', ',' -> sb.append(reader.peek());
					default -> {
						String funcname = readFunctionName(reader);
						reader.skipWhitespace();
						if (reader.peek() == ',' || reader.peek() == ')') {
							if (sb.toString().isBlank()) {
								TextFunctionReference ref = new TextFunctionReference(funcname);
								reader.skip();
								return (env, cb, args) -> ref.get(env).execute(env, cb, args);
							} else {
								throw new TextSyntaxException("Unexpected " + sb);
							}
						} else {
							if (!sb.isEmpty()) {
								contexts.add(new FunctionContext(
										new TextFunctionReference(TextFunction.ofConst(TextLike.string(sb.toString()))),
										null));
								sb = new StringBuilder();
							}
							contexts.add(parseContext(funcname, reader));
						}
					}
				}
			} else {
				sb.append(reader.peek());
			}
			reader.skip();
			check(reader);
		}
		if (!sb.isEmpty())
			contexts.add(new FunctionContext(
					new TextFunctionReference(TextFunction.ofConst(TextLike.string(sb.toString()))), null));
		return new TextCompiler.ConcatTextFunction(contexts.toArray(new FunctionContext[0]));
	}

	private static TextFunction parseBlock(StringReader reader, boolean isFile) throws TextSyntaxException {
		ArrayList<FunctionContext> contexts = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		while (reader.canRead() && (reader.peek() != '}' || isFile)) {
			if (reader.peek() == '\\') {
				reader.skip();
				if (!reader.canRead())
					throw new TextSyntaxException("Early end of file");
				switch (reader.peek()) {
					case '\\', '{', '}', '(', ')', ',' -> sb.append(reader.peek());
					default -> {
						String funcname = readFunctionName(reader);
						reader.skipWhitespace();
						if (!sb.isEmpty()) {
							contexts.add(new FunctionContext(
									new TextFunctionReference(TextFunction.ofConst(TextLike.string(sb.toString()))),
									null));
							sb = new StringBuilder();
						}
						contexts.add(parseContext(funcname, reader));
					}
				}
			} else {
				sb.append(reader.peek());
			}
			reader.skip();
		}
		if (!sb.isEmpty())
			contexts.add(new FunctionContext(
					new TextFunctionReference(TextFunction.ofConst(TextLike.string(sb.toString()))), null));
		return new TextCompiler.ConcatTextFunction(contexts.toArray(new FunctionContext[0]));
	}

	public static TextFunction compile(String text) throws TextSyntaxException {
		return parseBlock(new StringReader((text + "\n").replaceAll("#[\\s\\S]?\n", "").replaceAll("\n", "")), true);
	}
}
