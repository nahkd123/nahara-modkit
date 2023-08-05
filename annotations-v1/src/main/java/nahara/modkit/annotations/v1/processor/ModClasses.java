package nahara.modkit.annotations.v1.processor;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import nahara.modkit.annotations.v1.processor.autoreg.TinyMappingThing;
import nahara.modkit.annotations.v1.processor.utils.MirrorUtils;

public class ModClasses {
	// Fabric Loader
	public static final String PACKAGE_FABRIC = "net.fabricmc.api";
	public static final String FABRIC_UNIVERSAL_INIT = PACKAGE_FABRIC + ".ModInitializer";
	public static final String FABRIC_CLIENT_INIT = PACKAGE_FABRIC + ".ClientModInitializer";
	public static final String FABRIC_SERVER_INIT = PACKAGE_FABRIC + ".DedicatedServerModInitializer";

	private Elements elements;
	private Types types;
	private TinyMappingThing tiny;

	public final TypeElement fabric$ModInitializer;
	public final TypeElement fabric$ClientModInitializer;
	public final TypeElement fabric$DedicatedServerModInitializer;

	public final ResolveLater<TypeElement> mc$Identifier;
	public final ResolveLater<Element> mc$Registry$register;
	public final ResolveLater<VariableElement> mc$Registries$ITEM;
	public final ResolveLater<TypeElement> mc$Item;

	public ModClasses(Elements elements, Types types, TinyMappingThing tiny) {
		this.elements = elements;
		this.types = types;
		this.tiny = tiny;
		fabric$ModInitializer = elements.getTypeElement(FABRIC_UNIVERSAL_INIT);
		fabric$ClientModInitializer = elements.getTypeElement(FABRIC_CLIENT_INIT);
		fabric$DedicatedServerModInitializer = elements.getTypeElement(FABRIC_SERVER_INIT);

		mc$Identifier = tiny
				.addTarget(TinyMappingThing.classTarget("net.minecraft.class_2960"))
				.or(() -> elements.getTypeElement("net.minecraft.util.Identifier"))
				.map(v -> (TypeElement) v);
		mc$Registry$register = tiny
				.addTarget(TinyMappingThing.methodTarget("net.minecraft.class_2378", "method_10226"))
				.or(() -> MirrorUtils.findChild(elements.getTypeElement("net.minecraft.registry.Registry"), "register").get());
		mc$Registries$ITEM = tiny
				.addTarget(TinyMappingThing.fieldTarget("net.minecraft.class_7923", "field_41178"))
				.or(() -> MirrorUtils.findChild(elements.getTypeElement("net.minecraft.registry.Registries"), "ITEM").get())
				.map(v -> (VariableElement) v);
		mc$Item = tiny
				.addTarget(TinyMappingThing.classTarget("net.minecraft.class_1792"))
				.or(() -> elements.getTypeElement("net.minecraft.item.Item"))
				.map(v -> (TypeElement) v);
	}

	public Elements getElementsUtils() {
		return elements;
	}

	public VariableElement getRegistryFromEntryType(TypeMirror entryType) {
		if (types.isAssignable(entryType, mc$Item.tryResolve().get().asType())) return mc$Registries$ITEM.tryResolve().get();
		return null;
	}
}
