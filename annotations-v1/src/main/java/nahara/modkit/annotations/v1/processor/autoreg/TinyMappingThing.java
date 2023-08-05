package nahara.modkit.annotations.v1.processor.autoreg;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;

import nahara.modkit.annotations.v1.processor.ResolveLater;

public class TinyMappingThing {
	private Map<String, ResolveLater<Element>> targets = new HashMap<>();

	public static String classTarget(String pathToClass) { return pathToClass; }
	public static String methodTarget(String pathToClass, String methodName) { return classTarget(pathToClass) + "#" + methodName + "()"; }
	public static String fieldTarget(String pathToClass, String fieldName) { return classTarget(pathToClass) + "#" + fieldName + ";"; }

	public ResolveLater<Element> addTarget(String target) {
		var r = new ResolveLater<Element>();
		targets.put(target, r);
		return r;
	}

	public static final Pattern PAT_CLASS = Pattern.compile("^c\\t(.+?)\\t(.+?)\\t(.+?)$");
	public static final Pattern PAT_FIELD = Pattern.compile("^f\\t(.+?)\\t(.+?)\\t(.+?)\\t(.+?)$");
	public static final Pattern PAT_METHOD = Pattern.compile("^m\\t(.+?)\\t(.+?)\\t(.+?)\\t(.+?)$");

	public void processTinyMapping(Scanner scanner, Elements elements) throws IOException {
		var header = scanner.nextLine();
		String currentClass = "java.lang.Object";
		String currentNamedClass = currentClass;

		while (scanner.hasNextLine()) {
			var line = scanner.nextLine().trim();
			Matcher matcher;

			if ((matcher = PAT_CLASS.matcher(line)).matches()) {
				var official = matcher.group(1).replaceAll("\\/", ".");
				var intermediary = matcher.group(2).replaceAll("\\/", ".");
				var named = matcher.group(3).replaceAll("\\/", ".");

				currentClass = intermediary;
				currentNamedClass = named;

				var resolver = targets.get(classTarget(currentClass));
				if (resolver != null) resolver.resolve(elements.getTypeElement(named));
			} else if ((matcher = PAT_FIELD.matcher(line)).matches()) {
				var type = matcher.group(1);
				var official = matcher.group(2);
				var intermediary = matcher.group(3);
				var named = matcher.group(4);

				var resolver = targets.get(fieldTarget(currentClass, intermediary));
				if (resolver != null) {
					var enclosing = elements.getTypeElement(currentNamedClass.replace('$', '.'));
					if (enclosing != null) enclosing.getEnclosedElements().stream()
					.filter(v -> v.getSimpleName().toString().equals(named))
					.findFirst()
					.ifPresent(resolver::resolve);
				}
			} else if ((matcher = PAT_METHOD.matcher(line)).matches()) {
				var signature = matcher.group(1);
				var official = matcher.group(2);
				var intermediary = matcher.group(3);
				var named = matcher.group(4);

				var resolver = targets.get(methodTarget(currentClass, intermediary));
				if (resolver != null) {
					var type = elements.getTypeElement(currentNamedClass.replace('$', '.'));
					if (type != null) type.getEnclosedElements().stream()
					.filter(v -> v.getSimpleName().toString().equals(named))
					.findFirst()
					.ifPresent(resolver::resolve);
				}
			}
		}
	}
}
