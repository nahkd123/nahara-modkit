package nahara.modkit.annotations.v1.processor.utils;

import java.util.Optional;
import java.util.function.Supplier;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

public class MirrorUtils {
	public static String makePath(Element target) {
		if (target == null) return "";
		if (target instanceof PackageElement pkg) return pkg.getQualifiedName().toString();
		if (target instanceof TypeElement type) return type.getQualifiedName().toString();

		return makePath(target.getEnclosingElement()) + "." + target.getSimpleName();
	}

	public static String makeInvocation(Element method, String... params) {
		return makePath(method) + "(" + String.join(", ", params) + ")";
	}

	public static Optional<Element> findChild(Element e, String name) {
		return e.getEnclosedElements().stream()
				.filter(v -> v.getSimpleName().toString().equals(name))
				.map(v -> (Element) v)
				.findFirst();
	}

	@SafeVarargs
	public static Optional<ExecutableElement> findMethod(Element e, String name, Supplier<TypeMirror>... params) {
		return e.getEnclosedElements().stream()
				.filter(v -> v.getSimpleName().toString().equals(name))
				.filter(v -> v instanceof ExecutableElement)
				.map(v -> (ExecutableElement) v)
				.filter(v -> {
					var ref = v.getParameters();
					if (ref.size() != params.length) return false;
					for (int i = 0; i < ref.size(); i++) {
						if (params[i] == null) continue;
						if (ref.get(i).asType() != params[i].get()) return false;
					}

					return true;
				})
				.findFirst();
	}
}
