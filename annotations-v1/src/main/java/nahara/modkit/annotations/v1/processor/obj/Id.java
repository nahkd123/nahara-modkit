package nahara.modkit.annotations.v1.processor.obj;

import nahara.modkit.annotations.v1.processor.ModClasses;
import nahara.modkit.annotations.v1.processor.ModProcessingInfo;
import nahara.modkit.annotations.v1.processor.utils.MirrorUtils;

public class Id {
	private String namespace;
	private String id;

	public Id(String namespace, String id) {
		this.namespace = namespace;
		this.id = id;
	}

	public Id(String str) {
		var splits = str.split("\\:", 2);

		if (splits.length == 1) {
			namespace = null;
			id = str;
		} else {
			namespace = splits[0];
			id = splits[1];
		}
	}

	public String getNamespace() {
		return namespace;
	}

	public String getId() {
		return id;
	}

	public String makeConstructor(ModProcessingInfo modProcessingInfo, ModClasses classes) {
		var namespace = this.namespace != null? this.namespace : modProcessingInfo.modIndex.modid();
		return "new " + MirrorUtils.makeInvocation(classes.mc$Identifier.tryResolve().get(), '"' + namespace + '"', '"' + id + '"');
	}

	@Override
	public String toString() {
		return namespace + ":" + id;
	}
}
