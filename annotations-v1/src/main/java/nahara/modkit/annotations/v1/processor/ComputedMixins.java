package nahara.modkit.annotations.v1.processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.lang.model.SourceVersion;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import nahara.modkit.annotations.v1.Env;

public class ComputedMixins {
	private String commonPackage;
	private List<String> flattenMixins = new ArrayList<>();
	private String modid;
	private Env env;

	public ComputedMixins(String modid, Env env) {
		this.modid = modid;
		this.env = env;
	}

	public String getModid() {
		return modid;
	}

	public Env getEnv() {
		return env;
	}

	public String getCommonPackage() {
		return commonPackage;
	}

	public List<String> getFlattenMixins() {
		return Collections.unmodifiableList(flattenMixins);
	}

	public void addMixin(String mixin) {
		flattenMixins.add(mixin.substring(commonPackage.length() + 1));
	}

	public void findCommonPackage(String nextPackage) {
		if (commonPackage == null) {
			commonPackage = nextPackage;
			return;
		}

		var oldSplits = commonPackage.split("\\.");
		var newSplits = nextPackage.split("\\.");
		var newCommonPackage = "";

		for (int i = 0; i < Math.max(oldSplits.length, newSplits.length); i++) {
			if (i >= oldSplits.length) return;
			if (i >= newSplits.length) {
				commonPackage = nextPackage; // New package is shorter than common package
				return;
			}

			if (!oldSplits[i].equals(newSplits[i])) {
				commonPackage = newCommonPackage;
				return;
			}

			newCommonPackage += (newCommonPackage.isEmpty()? "" : ".") + newSplits[i];
		}
	}

	public Optional<JsonElement> createForIndex() {
		if (flattenMixins.isEmpty()) return Optional.empty();

		var config = new JsonPrimitive(getConfigName());
		if (env == Env.ALL) return Optional.of(config);

		var root = new JsonObject();
		root.add("config", config);
		root.addProperty("environment", env.toString().toLowerCase());
		return Optional.of(root);
	}

	public String getConfigName() {
		return modid + "." + (switch (env) {
		case CLIENT -> "client.";
		case SERVER -> "server.";
		default -> "";
		}) + "mixins.json";
	}

	public Optional<JsonObject> createConfig(SourceVersion version) {
		if (flattenMixins.isEmpty()) return Optional.empty();

		var root = new JsonObject();
		root.addProperty("required", true);
		root.addProperty("package", commonPackage);
		root.addProperty("compatibilityLevel", version.toString().replace("RELEASE", "JAVA"));
		root.add(switch (env) {
		case CLIENT -> "client";
		case SERVER -> "server";
		default -> "mixins";
		}, flattenMixins.stream().collect(() -> new JsonArray(), (arr, e) -> arr.add(e), (a, b) -> a.addAll(b)));

		var injectors = new JsonObject();
		injectors.addProperty("defaultRequire", 1);
		root.add("injectors", injectors);
		return Optional.of(root);
	}
}
