package nahara.modkit.annotations.v1.processor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.StandardLocation;

import com.google.auto.service.AutoService;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import nahara.modkit.annotations.v1.AutoMixin;
import nahara.modkit.annotations.v1.AutoRegister;
import nahara.modkit.annotations.v1.Dependencies;
import nahara.modkit.annotations.v1.EntryPoint;
import nahara.modkit.annotations.v1.Mod;
import nahara.modkit.annotations.v1.processor.autoreg.EntryPointGenerator;
import nahara.modkit.annotations.v1.processor.autoreg.TinyMappingThing;
import nahara.modkit.annotations.v1.processor.obj.IOConsumer;
import nahara.modkit.annotations.v1.processor.obj.Id;

@SupportedAnnotationTypes("nahara.modkit.annotations.v1.*")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@SupportedOptions({ "nahara.modkit.expand", "inMapFileNamedIntermediary" })
@AutoService(Processor.class)
public class NaharaModkitAnnotationProcessor extends AbstractProcessor {
	private ModProcessingInfo modProcessingInfo;
	private Expander expander;
	private TinyMappingThing tiny;
	private ModClasses modClasses;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		tiny = new TinyMappingThing();
		modClasses = new ModClasses(processingEnv.getElementUtils(), processingEnv.getTypeUtils(), tiny);
		modProcessingInfo = new ModProcessingInfo(modClasses);

		var expandString = processingEnv.getOptions().get("nahara.modkit.expand");
		expander = expandString != null? new Expander(expandString) : new Expander();

		var namedIntermediary = processingEnv.getOptions().get("inMapFileNamedIntermediary");
		if (namedIntermediary != null) {
			try (var scanner = new Scanner(new File(namedIntermediary))) {
				tiny.processTinyMapping(scanner, processingEnv.getElementUtils());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				processingEnv.getMessager().printMessage(Kind.ERROR, "Nahara: Mapping not found: " + namedIntermediary);
			} catch (IOException e) {
				e.printStackTrace();
				processingEnv.getMessager().printMessage(Kind.ERROR, "Nahara: Error while reading mapping: " + namedIntermediary);
			}
		}
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		processModInfo(annotations, roundEnv);
		processEntryPoints(annotations, roundEnv);
		processAutoMixin(annotations, roundEnv);
		processAutoRegister(annotations, roundEnv);

		if (roundEnv.processingOver()) finalizeProcessing();
		return false;
	}

	private void processModInfo(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		for (var modElement : roundEnv.getElementsAnnotatedWith(Mod.class)) {
			if (modProcessingInfo.modIndex != null) processingEnv.getMessager().printMessage(Kind.WARNING, "There are more than 1 @Mod annotations declared; using modid = " + modElement.getAnnotation(Mod.class));
			modProcessingInfo.modIndex = modElement.getAnnotation(Mod.class);

			var deps = modElement.getAnnotation(Dependencies.class);
			if (deps != null) for (var dependency : deps.value()) {
				modProcessingInfo.dependencies.add(dependency);
			}
		}
	}

	private void processEntryPoints(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		for (var entryPoint : roundEnv.getElementsAnnotatedWith(EntryPoint.class)) {
			var annotation = entryPoint.getAnnotation(EntryPoint.class);
			String entryPointName;
			List<String> entryPoints = null;

			if (entryPoint.getKind() == ElementKind.METHOD) {
				if (!entryPoint.getModifiers().contains(Modifier.STATIC)) {
					processingEnv.getMessager().printMessage(Kind.WARNING, "@EntryPoint annotated element is not a static method, skipping", entryPoint);
					continue;
				}

				if (!(entryPoint.getEnclosingElement() instanceof TypeElement qualifiedEnclosing)) {
					processingEnv.getMessager().printMessage(Kind.WARNING, "@EntryPoint annotated element's enclosing element does not have a qualified name, skipping", entryPoint);
					continue;
				}

				entryPointName = qualifiedEnclosing.getQualifiedName() + "::" + entryPoint.getSimpleName();
			} else if (entryPoint.getKind().isClass()) {
				if (!(entryPoint instanceof TypeElement qualifiedEntryPoint)) {
					processingEnv.getMessager().printMessage(Kind.WARNING, "@EntryPoint annotated element does not have a qualified name, skipping", entryPoint);
					continue;
				}

				entryPointName = qualifiedEntryPoint.getQualifiedName().toString();

				if (processingEnv.getTypeUtils().isAssignable(qualifiedEntryPoint.asType(), modClasses.fabric$ModInitializer.asType())) entryPoints = modProcessingInfo.commonEntryPoints;
				if (processingEnv.getTypeUtils().isAssignable(qualifiedEntryPoint.asType(), modClasses.fabric$ClientModInitializer.asType())) entryPoints = modProcessingInfo.clientEntryPoints;
				if (processingEnv.getTypeUtils().isAssignable(qualifiedEntryPoint.asType(), modClasses.fabric$DedicatedServerModInitializer.asType())) entryPoints = modProcessingInfo.serverEntryPoints;
			} else {
				processingEnv.getMessager().printMessage(Kind.WARNING, "@EntryPoint annotated element is not a class or a method, skipping (how is it possible?)", entryPoint);
				continue;
			}

			if (entryPoints == null) {
				entryPoints = switch (annotation.environment()) {
				case CLIENT -> modProcessingInfo.clientEntryPoints;
				case SERVER -> modProcessingInfo.serverEntryPoints;
				default -> modProcessingInfo.commonEntryPoints;
				};
			}

			entryPoints.add(entryPointName);
		}
	}

	private void processAutoMixin(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		for (var mixin : roundEnv.getElementsAnnotatedWith(AutoMixin.class)) {
			if (!mixin.getModifiers().contains(Modifier.ABSTRACT)) {
				processingEnv.getMessager().printMessage(Kind.NOTE, "@AutoMixin on non-abstract class; you might instantiate this class accidentally", mixin);
			}

			if (!(mixin instanceof TypeElement mixinClass)) {
				processingEnv.getMessager().printMessage(Kind.WARNING, "@AutoMixin annotated element does not have a qualified name, skipping", mixin);
				continue;
			}

			var annotation = mixin.getAnnotation(AutoMixin.class);
			var mixinsStore = switch (annotation.environment()) {
			default -> modProcessingInfo.commonMixins;
			};

			mixinsStore.mixins.add(mixinClass.getQualifiedName().toString());
		}
	}

	private void processAutoRegister(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		for (var field : roundEnv.getElementsAnnotatedWith(AutoRegister.class)) {
			if (!(field instanceof VariableElement varField)) {
				processingEnv.getMessager().printMessage(Kind.WARNING, "@AutoRegister annotated element is not a field (how is it possible?)", field);
				continue;
			}
	
			if (!(field.getEnclosingElement() instanceof TypeElement)) {
				processingEnv.getMessager().printMessage(Kind.WARNING, "@AutoRegister annotated element's parent is not a class, skipping", field);
				continue;
			}

			if (!field.getModifiers().contains(Modifier.STATIC)) {
				processingEnv.getMessager().printMessage(Kind.WARNING, "@AutoRegister annotated element does not have static modifier, skipping", field);
				continue;
			}

			if (!field.getModifiers().contains(Modifier.FINAL)) {
				processingEnv.getMessager().printMessage(Kind.WARNING, "@AutoRegister annotated element does not have final modifier; you might change value of this field accidentally", field);
			}

			var regField = modClasses.getRegistryFromEntryType(field.asType());
			if (regField == null) {
				processingEnv.getMessager().printMessage(Kind.WARNING, "@AutoRegister annotated element's type is not supported, skipping", field);
				continue;
			}

			var annotation = field.getAnnotation(AutoRegister.class);
			Id id;

			if (annotation.id().isEmpty()) id = new Id(null, field.getSimpleName().toString().toLowerCase());
			else id = new Id(annotation.id());

			modProcessingInfo.entryPointGenerator.entries.add(new EntryPointGenerator.RegistryEntry(id, varField, regField));
		}
	}

	private void finalizeProcessing() {
		modProcessingInfo.finalizeProcessing();
		modProcessingInfo.createIndex().ifPresent(obj -> createJsonResource("fabric.mod.json", obj));
		modProcessingInfo.computedCommonMixins.createConfig(processingEnv.getSourceVersion()).ifPresent(obj -> createJsonResource(modProcessingInfo.computedCommonMixins.getConfigName(), obj));
		modProcessingInfo.computedClientMixins.createConfig(processingEnv.getSourceVersion()).ifPresent(obj -> createJsonResource(modProcessingInfo.computedClientMixins.getConfigName(), obj));
		modProcessingInfo.computedServerMixins.createConfig(processingEnv.getSourceVersion()).ifPresent(obj -> createJsonResource(modProcessingInfo.computedServerMixins.getConfigName(), obj));

		if (modProcessingInfo.entryPointGenerator.shouldMakeClass()) createClass(modProcessingInfo.entryPointGenerator.getEntryPointPath(), modProcessingInfo.entryPointGenerator::writeClass);
	}

	private void createJsonResource(String path, JsonElement json) {
		try {
			var gson = new GsonBuilder().disableHtmlEscaping().create(); // TODO cache this somewhere?
			var fileObj = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", path);
			var writer = fileObj.openWriter();
			writer.append(expander.process(gson.toJson(json)));
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			processingEnv.getMessager().printMessage(Kind.WARNING, "Failed to write to " + path + ": IOException thrown");
		}
	}

	private void createClass(String path, IOConsumer<Writer> consumer) {
		try {
			var fileObj = processingEnv.getFiler().createSourceFile(path);
			var writer = fileObj.openWriter();
			consumer.accept(writer);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			processingEnv.getMessager().printMessage(Kind.WARNING, "Failed to write class " + path + ": IOException");
		}
	}
}
