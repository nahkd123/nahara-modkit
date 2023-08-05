package nahara.modkit.annotations.v1.processor.autoreg;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.lang.model.element.VariableElement;

import nahara.modkit.annotations.v1.processor.ModClasses;
import nahara.modkit.annotations.v1.processor.ModProcessingInfo;
import nahara.modkit.annotations.v1.processor.obj.Id;
import nahara.modkit.annotations.v1.processor.utils.MirrorUtils;

public class EntryPointGenerator {
	private ModProcessingInfo modProcessingInfo;
	private ModClasses modClasses;
	public final List<RegistryEntry> entries = new ArrayList<>();

	public EntryPointGenerator(ModProcessingInfo modProcessingInfo, ModClasses modClasses) {
		this.modProcessingInfo = modProcessingInfo;
		this.modClasses = modClasses;
	}

	public static String capitalizeModid(String input) {
		return Stream.of(input.split("[-_]"))
				.map(v -> Character.toUpperCase(v.charAt(0)) + v.substring(1))
				.collect(() -> new String[] { "" }, (src, add) -> src[0] += add, (a, b) -> a[0] += b[0])[0];
	}

	public String getEntryPointPackage() { return "nahara.generated." + modProcessingInfo.modIndex.modid().toLowerCase().replace('-', '_'); }
	public String getEntryPointName() { return capitalizeModid(modProcessingInfo.modIndex.modid()) + "Main"; }
	public String getEntryPointPath() { return getEntryPointPackage() + "." + getEntryPointName(); }

	public boolean shouldMakeClass() {
		return modProcessingInfo.modIndex != null && entries.size() > 0;
	}

	public void writeClass(Writer writer) throws IOException {
		var packageName = getEntryPointPackage();
		var entryPointName = getEntryPointName();

		writer.append("package " + packageName + " ;\n");
		writer.append('\n');
		writer.append("public class " + entryPointName + " implements " + modClasses.fabric$ModInitializer.getQualifiedName() + " {\n");
		writer.append("    @Override\n");
		writer.append("    public void onInitialize() {\n");
		for (var e : entries) writer.append("        ").append(e.makeRegisterInvocation(modProcessingInfo, modClasses)).append(";\n");
		writer.append("    }\n");
		writer.append("}\n");
	}

	public static record RegistryEntry(Id id, VariableElement fieldToRegister, VariableElement registry) {
		public String makeRegisterInvocation(ModProcessingInfo modProcessingInfo, ModClasses classes) {
			return MirrorUtils.makeInvocation(classes.mc$Registry$register.tryResolve().get(),
					MirrorUtils.makePath(registry),
					id.makeConstructor(modProcessingInfo, classes),
					MirrorUtils.makePath(fieldToRegister));
		}
	}
}
