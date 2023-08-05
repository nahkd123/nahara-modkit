package nahara.modkit.annotations.v1.processor.utils;

import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

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
}
