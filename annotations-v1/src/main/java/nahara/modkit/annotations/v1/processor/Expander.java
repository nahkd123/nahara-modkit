package nahara.modkit.annotations.v1.processor;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Expander {
	private static final Pattern PATTERN = Pattern.compile("\\$\\{(\\w+?)\\}");
	private Map<String, String> pairs = new HashMap<>();

	public Expander(String optionString) {
		for (var pairStr : optionString.split(";")) {
			var splits = pairStr.split("\\:", 2);
			pairs.put(splits[0], splits[1]);
		}
	}

	public Expander() {
	}

	public Map<String, String> getPairs() {
		return pairs;
	}

	public String process(String in) {
		return PATTERN.matcher(in).replaceAll(result -> {
			var key = result.group(1);
			var value = pairs.get(key);
			return Matcher.quoteReplacement(value != null? value : ("${" + key + "}"));
		});
	}
}
