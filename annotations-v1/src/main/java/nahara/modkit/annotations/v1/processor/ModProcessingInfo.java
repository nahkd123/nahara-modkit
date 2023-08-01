package nahara.modkit.annotations.v1.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import nahara.modkit.annotations.v1.Dependency;
import nahara.modkit.annotations.v1.Mod;
import net.fabricmc.loader.api.metadata.ModEnvironment;

public class ModProcessingInfo {
	public Mod modIndex;
	public final List<Dependency> dependencies = new ArrayList<>();
	public final List<String> commonEntryPoints = new ArrayList<>();
	public final List<String> clientEntryPoints = new ArrayList<>();
	public final ModMixinsInfo commonMixins = new ModMixinsInfo(false);
	public final ModMixinsInfo clientMixins = new ModMixinsInfo(true);

	public ComputedMixins computedCommonMixins, computedClientMixins;

	public void finalizeProcessing() {
		computedCommonMixins = commonMixins.compute(modIndex.modid());
		computedClientMixins = clientMixins.compute(modIndex.modid());
	}

	public Optional<JsonObject> createIndex() {
		if (modIndex == null) return Optional.empty();

		var root = new JsonObject();
		root.addProperty("schemaVersion", 1);
		root.addProperty("id", modIndex.modid());
		root.addProperty("name", modIndex.name().length() > 0? modIndex.name() : modIndex.modid());
		root.addProperty("version", modIndex.version());
		root.addProperty("environment", modIndex.modEnvironment() == ModEnvironment.UNIVERSAL? "*" : modIndex.modEnvironment().toString().toLowerCase());
		root.addProperty("license", modIndex.license().length() > 0? modIndex.license() : "UNLICENSED");
		root.addProperty("icon", modIndex.icon().length() > 0? modIndex.icon() : "assets/" + modIndex.modid() + "/icon.png");

		// TODO contact info

		var authors = new JsonArray();
		for (var author : modIndex.authors()) authors.add(new JsonPrimitive(author));
		root.add("authors", authors);

		var entryPoints = new JsonObject();
		entryPoints.add("main", commonEntryPoints.stream().collect(() -> new JsonArray(), (arr, str) -> arr.add(str), (a, b) -> a.addAll(b)));
		entryPoints.add("client", clientEntryPoints.stream().collect(() -> new JsonArray(), (arr, str) -> arr.add(str), (a, b) -> a.addAll(b)));
		root.add("entrypoints", entryPoints);

		var mixins = new JsonArray();
		computedCommonMixins.createForIndex().ifPresent(mixins::add);
		computedClientMixins.createForIndex().ifPresent(mixins::add);
		if (computedCommonMixins.getFlattenMixins().size() > 0 || computedClientMixins.getFlattenMixins().size() > 0) {
			root.add("mixins", mixins);
		}

		var depends = new JsonObject();
		for (var d : dependencies) depends.addProperty(d.value(), d.version());
		root.add("depends", depends);
		return Optional.of(root);
	}
}
