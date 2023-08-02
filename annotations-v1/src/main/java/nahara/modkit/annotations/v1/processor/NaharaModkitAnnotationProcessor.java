package nahara.modkit.annotations.v1.processor;

import java.io.IOException;
import java.util.List;
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
import javax.tools.Diagnostic.Kind;
import javax.tools.StandardLocation;

import com.google.auto.service.AutoService;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import nahara.modkit.annotations.v1.AutoMixin;
import nahara.modkit.annotations.v1.EntryPoint;
import nahara.modkit.annotations.v1.Mod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.ModInitializer;

@SupportedAnnotationTypes("nahara.modkit.annotations.v1.*")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@SupportedOptions("nahara.modkit.expand")
@AutoService(Processor.class)
public class NaharaModkitAnnotationProcessor extends AbstractProcessor {
	private ModProcessingInfo modProcessingInfo;
	private Expander expander;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		modProcessingInfo = new ModProcessingInfo();

		var expandString = processingEnv.getOptions().get("nahara.modkit.expand");
		expander = expandString != null? new Expander(expandString) : new Expander();
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		processModInfo(annotations, roundEnv);
		processEntryPoints(annotations, roundEnv);
		processAutoMixin(annotations, roundEnv);

		if (roundEnv.processingOver()) finalizeProcessing();
		return false;
	}

	private void processModInfo(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		for (var modElement : roundEnv.getElementsAnnotatedWith(Mod.class)) {
			if (modProcessingInfo.modIndex != null) processingEnv.getMessager().printMessage(Kind.WARNING, "There are more than 1 @Mod annotations declared; using modid = " + modElement.getAnnotation(Mod.class));
			modProcessingInfo.modIndex = modElement.getAnnotation(Mod.class);
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

				var universalModInit = processingEnv.getElementUtils().getTypeElement(ModInitializer.class.getName());
				var clientModInit = processingEnv.getElementUtils().getTypeElement(ClientModInitializer.class.getName());
				var serverModInit = processingEnv.getElementUtils().getTypeElement(DedicatedServerModInitializer.class.getName());

				if (processingEnv.getTypeUtils().isAssignable(qualifiedEntryPoint.asType(), universalModInit.asType())) entryPoints = modProcessingInfo.commonEntryPoints;
				if (processingEnv.getTypeUtils().isAssignable(qualifiedEntryPoint.asType(), clientModInit.asType())) entryPoints = modProcessingInfo.clientEntryPoints;
				if (processingEnv.getTypeUtils().isAssignable(qualifiedEntryPoint.asType(), serverModInit.asType())) entryPoints = modProcessingInfo.serverEntryPoints;
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

	private void finalizeProcessing() {
		modProcessingInfo.finalizeProcessing();
		modProcessingInfo.createIndex().ifPresent(obj -> createJsonResource("fabric.mod.json", obj));
		modProcessingInfo.computedCommonMixins.createConfig(processingEnv.getSourceVersion()).ifPresent(obj -> createJsonResource(modProcessingInfo.computedCommonMixins.getConfigName(), obj));
		modProcessingInfo.computedClientMixins.createConfig(processingEnv.getSourceVersion()).ifPresent(obj -> createJsonResource(modProcessingInfo.computedClientMixins.getConfigName(), obj));
		modProcessingInfo.computedServerMixins.createConfig(processingEnv.getSourceVersion()).ifPresent(obj -> createJsonResource(modProcessingInfo.computedServerMixins.getConfigName(), obj));
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
}
