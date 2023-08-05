package nahara.modkit.annotations.v1.processor;

import java.util.ArrayList;
import java.util.List;

import nahara.modkit.annotations.v1.Env;

public class ModMixinsInfo {
	private Env env;
	public final List<String> mixins = new ArrayList<>();

	public ModMixinsInfo(Env env) {
		this.env = env;
	}

	public ComputedMixins compute(String modid) {
		var out = new ComputedMixins(modid, env);
		mixins.forEach(v -> {
			var splits = v.split("\\.");
			out.findCommonPackage(v.substring(0, v.length() - splits[splits.length - 1].length() - 1));
		});

		mixins.forEach(out::addMixin);
		return out;
	}
}
