package io.github.cavenightingale.essentials.utils;

import java.lang.reflect.Type;

import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ServerTranslation {

	public static ServerTranslation formats = Config.load(ServerTranslation.class, "translation");

	public record Node(String[] fmt) {
		public Node(String fmts) {
			this(fmts.split("\\{}", Integer.MAX_VALUE));
		}

		public MutableText format(Object... args) {
			return new LiteralText(formatAsString(args));
		}

		public String formatAsString(Object... args) {
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < Math.max(fmt.length, args.length); i++) {
				if(fmt.length > i)
					builder.append(fmt[i]);
				if(args.length > i)
					builder.append(args[i]);
			}
			return builder.toString();
		}
	}

	public static class Adapter implements JsonSerializer<Node>, JsonDeserializer<Node> {

		@Override
		public Node deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			return new Node(json.getAsString());
		}

		@Override
		public JsonElement serialize(Node src, Type typeOfSrc, JsonSerializationContext context) {
			return new JsonPrimitive(String.join("{}", src.fmt));
		}
	}

	public Node esspermCommandNotFoundToSet = new Node("找不到命令，无法设置权限"),
			esspermCommandNotFoundToGet = new Node("找不到命令，无法列举权限"),
			esspermCommandPermissionSetted = new Node("已设置命令{}的权限为{}"),
			esspermCommandPermissionSettedRecursion = new Node("已递归设置命令{}的权限为{}"),
			esspermCommandPermissionUseVanilla = new Node("命令{}使用原版权限系统"),
			esspermCommandPermissionUseModded = new Node("命令{}需要权限{}"),
			esspermUsermodOpChanged = new Node("已更改{}的管理员信息"),
			warpSetted = new Node("已设置传送点'{}'"),
			warpNotFound = new Node("找不到该传送点"),
			warpDeleted = new Node("已删除传送点'{}'"),
			warpLocation = new Node("位于{}({})"),
			tpaAccepted = new Node("请求已被接受"),
			tpaDenied = new Node("请求已被拒绝"),
			tpaCancelled = new Node("请求已被取消"),
			tpaFromMsg = new Node("正在请求传送到{}"),
			tpaFromMsgReverse = new Node("正在请求{}传送到你这里"),
			tpaToMsg = new Node("{}请求传送到你的位置，你有两分钟时间处理"),
			tpaToMsgReverse = new Node("{}请求你传送到其位置，你有两分钟时间处理"),
			tpaSuggestAccept = new Node("/tpaccept同意请求"),
			tpaSuggestDeny = new Node("/tpdeny拒绝请求"),
			miscFlyEnabled = new Node("飞行模式已开启"),
			miscFlyDisabled = new Node("飞行模式已关闭"),
			miscFlyUnsupported = new Node("你不能操作旁观或者创造模式的玩家"),
			miscAfkLeave = new Node("- {}暂时离开了"),
			miscAfkBack = new Node("+ {}回来了"),
			miscSitFailed = new Node("你现在不能坐下"),
			homeDeath = new Node("上次死亡位置"),
			homeSpawn = new Node("上次设置重生点位置"),
			sessUpdated = new Node("已尝试更新配置文件"),
			sessReloaded = new Node("已尝试重新载入配置文件");

}
