package io.github.cavenightingale.essentials.protect.database;

import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.visitor.StringNbtWriter;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;

import com.mojang.authlib.GameProfile;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.reactivestreams.client.ClientSession;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;

import io.github.cavenightingale.essentials.Essentials;
import io.github.cavenightingale.essentials.protect.database.event.BlockSourcedEvent;
import io.github.cavenightingale.essentials.protect.database.event.BlockTargetedEvent;
import io.github.cavenightingale.essentials.protect.database.event.EntityMiddledEvent;
import io.github.cavenightingale.essentials.protect.database.event.EntitySourcedEvent;
import io.github.cavenightingale.essentials.protect.database.event.EntityTargetedEvent;
import io.github.cavenightingale.essentials.protect.database.event.LoggedEvent;
import io.github.cavenightingale.essentials.protect.database.event.ReasonedEvent;
import io.github.cavenightingale.essentials.protect.database.event.TextedEvent;


public class DataBaseConnection implements AutoCloseable {
	MongoClient client = null;
	MongoDatabase db = null;

	MongoCollection<Document> gameLog = null;

	public void initialize() {
		ConnectionString url = new ConnectionString("mongodb://localhost:27017");
		MongoClientSettings settings = MongoClientSettings.builder()
				.applyConnectionString(url)
				.build();
		client = MongoClients.create(settings);
		db = client.getDatabase("minecraft");
		db.createCollection("game_log", new CreateCollectionOptions().capped(true).sizeInBytes(8 * 1024)).subscribe(new LogErrorSubscriber<>("Cannot create collection") {
			@Override
			public void onComplete() {
				db.getCollection("game_log").createIndex(Indexes.descending("date")).subscribe(new LogErrorSubscriber<>("Cannot create index"));
			}
		});
		gameLog = db.getCollection("game_log");
	}

	public void close() {
		if(client != null) {
			client.close();
			client = null;
			db = null;
			gameLog = null;
		}
	}

	private Document toDocument(GameProfile profile) {
		Document document = new Document();
		document.put("uuid", profile.getId().toString());
		document.put("name", profile.getName());
		return document;
	}

	private @NotNull Document toDocument(Vec3i pos) {
		Document document = new Document();
		document.put("x", pos.getX());
		document.put("y", pos.getY());
		document.put("z", pos.getZ());
		return document;
	}

	private @NotNull Document toDocument(ItemStack weapon) {
		Document document = new Document();
		document.put("id", Registry.ITEM.getId(weapon.getItem()).toString());
		document.put("count", weapon.getCount());
		if(weapon.hasNbt())
			document.put("tags", new StringNbtWriter().apply(weapon.getNbt()));
		return document;
	}

	public void write(@NotNull LoggedEvent event) {
		client.startSession().subscribe(new LogErrorSubscriber<>(" Can not open session") {
			@Override
			public void onNext(ClientSession session) {
				super.onNext(session);
				Document document = new Document();
				document.put("type", event.type());
				document.put("world", event.world().toString());
				document.put("date", event.date());
				Document location = new Document();
				location.put("x", event.location().x);
				location.put("y", event.location().y);
				location.put("z", event.location().z);
				document.put("location", location);
				if(event instanceof BlockSourcedEvent ev) {
					document.put("sourceBlockState", Block.STATE_IDS.getRawId(ev.sourceBlockState()));
					document.put("sourceBlockPos", toDocument(ev.sourceBlockPos()));
				}
				if(event instanceof BlockTargetedEvent ev) {
					document.put("targetBlockPos", toDocument(ev.targetBlockPos()));
					document.put("targetBlockState", Block.STATE_IDS.getRawId(ev.targetBlockState()));
					document.put("previousBlockState", Block.STATE_IDS.getRawId(ev.previousBlockState()));
				}
				if(event instanceof EntityMiddledEvent ev) {
					if(ev.directEntity() != null)
						document.put("directEntity", toDocument(ev.directEntity()));
				}
				if(event instanceof EntitySourcedEvent ev) {
					document.put("sourceEntity", toDocument(ev.sourceEntity()));
					if(ev.weapon() != null)
						document.put("weapon", toDocument(ev.weapon()));
				}
				if(event instanceof EntityTargetedEvent ev) {
					document.put("targetEntity", toDocument(ev.targetEntity()));
					if(ev.damage() != 0.0)
						document.put("damage", ev.damage());
				}
				if(event instanceof ReasonedEvent ev) {
					document.put("reason", ev.reason());
				}
				if(event instanceof TextedEvent ev) {
					document.put("text", ev.text());
				}
				gameLog.insertOne(session, document).subscribe(new LogErrorSubscriber<>("Can not save event"));
				session.close();
			}
		});
		Essentials.LOGGER.info("Event {}", event);
	}
}
