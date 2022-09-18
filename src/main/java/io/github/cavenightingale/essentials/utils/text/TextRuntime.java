package io.github.cavenightingale.essentials.utils.text;

import java.util.HashMap;

import org.jetbrains.annotations.Nullable;

import io.github.cavenightingale.essentials.utils.text.TextCompiler.FunctionContext;

public class TextRuntime {
	public static class TextRuntimeException extends Exception {
		public TextRuntimeException(String reason) {
			super(reason);
		}
	
		public TextRuntimeException(String reason, Throwable cause) {
			super(reason, cause);
		}
	}

	final HashMap<String, TextFunction> functions = new HashMap<>();
	final int depth;
	@Nullable
	final TextRuntime parent;
	public static final int MAX_DEPTH = 128;

	TextRuntime(TextRuntime parent) throws TextRuntime.TextRuntimeException {
		this.parent = parent;
		depth = parent.depth + 1;
		if (depth > MAX_DEPTH) {
			throw new TextRuntime.TextRuntimeException("Stack overflow");
		}
	}

	public TextRuntime() {
		this.parent = null;
		this.depth = 0;
	}

	@Nullable
	public TextFunction get(String name) {
		TextFunction func = functions.get(name);
		return func != null ? func : parent != null ? parent.get(name) : null;
	}

	public void put(String name, TextFunction func) {
		functions.put(name, func);
	}

	public void putVar(String name, TextLike func) {
		functions.put(name, (env, cb, args) -> func);
	}

	public TextLike invoke(FunctionContext context, TextRuntime.EnvironmentModifier... modifiers) throws TextRuntime.TextRuntimeException {
		return context.invoke(new TextRuntime(this).applyModifier(modifiers));
	}

	public TextLike invoke(@Nullable TextFunction func, TextRuntime.EnvironmentModifier... modifiers)
			throws TextRuntime.TextRuntimeException {
		if (func == null)
			throw new TextRuntime.TextRuntimeException("function must not be null");
		return func.execute(new TextRuntime(this).applyModifier(modifiers), null);
	}
	
	public interface EnvironmentModifier {
		void modify(TextRuntime env);
	}

	public TextRuntime applyModifier(TextRuntime.EnvironmentModifier... modifiers) {
		for (TextRuntime.EnvironmentModifier modifier : modifiers) {
			modifier.modify(this);
		}
		return this;
	}
}