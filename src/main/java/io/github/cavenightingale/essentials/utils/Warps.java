package io.github.cavenightingale.essentials.utils;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Objects;

import net.minecraft.network.packet.s2c.play.ExperienceBarUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import it.unimi.dsi.fastutil.floats.FloatFloatImmutablePair;

public class Warps extends HashMap<String, Warps.Warp> {
	public record Warp(String name, Identifier world, Vec3d loc, FloatFloatImmutablePair angle, String description) {
		public void teleport(ServerPlayerEntity player) {
			assert player.getServer() != null;
			ServerWorld world1 = player.getServer().getWorld(RegistryKey.of(Registry.WORLD_KEY, world));
			if(world1 != null) {
				player.teleport(world1, loc.x, loc.y, loc.z, angle.firstFloat(), angle.secondFloat());
				player.networkHandler.connection.send(new ExperienceBarUpdateS2CPacket(player.experienceProgress, player.totalExperience, player.experienceLevel));
			}
		}
	}

	public static class Adapter implements JsonSerializer<Warp>, JsonDeserializer<Warp> {

		@Override
		public Warp deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			try {
				JsonObject self = json.getAsJsonObject();
				String name = Objects.requireNonNull(self.get("name")).getAsString();
				Identifier id = new Identifier(Objects.requireNonNull(self.get("world")).getAsString());
				JsonArray loc = Objects.requireNonNull(self.get("loc")).getAsJsonArray();
				JsonArray angle = Objects.requireNonNull(self.get("angle")).getAsJsonArray();
				String desc = self.get("description") instanceof JsonPrimitive primitive && primitive.isString() ? primitive.getAsString() : null;
				return new Warp(name, id, new Vec3d(loc.get(0).getAsDouble(), loc.get(1).getAsDouble(), loc.get(2).getAsDouble()), new FloatFloatImmutablePair(angle.get(0).getAsFloat(), angle.get(1).getAsFloat()), desc);
			} catch (NullPointerException npe) {
				throw new JsonParseException(npe);
			}
		}

		@Override
		public JsonElement serialize(Warp src, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject object = new JsonObject();
			object.addProperty("name", src.name);
			object.addProperty("world", src.world.toString());
			JsonArray loc = new JsonArray();
			loc.add(src.loc.x);
			loc.add(src.loc.y);
			loc.add(src.loc.z);
			JsonArray angle = new JsonArray();
			angle.add(src.angle.leftFloat());
			angle.add(src.angle.rightFloat());
			object.add("loc", loc);
			object.add("angle", angle);
			if(src.description() != null)
				object.addProperty("description", src.description());
			return object;
		}
	}

	public static Warps warps = Config.load(Warps.class, "warps");

	public static void save() {
		Config.save(Warps.warps, "warps");
	}
}
