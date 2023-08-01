package nahara.modkit.annotations.v1.processor;

import java.util.ArrayList;
import java.util.List;

public class ModMixinsInfo {
	public final boolean isClient;
	public final List<String> mixins = new ArrayList<>();

	public ModMixinsInfo(boolean isClient) {
		this.isClient = isClient;
	}

	public ComputedMixins compute(String modid) {
		var out = new ComputedMixins(modid, isClient);
		mixins.forEach(v -> {
			var splits = v.split("\\.");
			out.findCommonPackage(v.substring(0, v.length() - splits[splits.length - 1].length() - 1));
		});

		mixins.forEach(out::addMixin);
		return out;
	}
}
