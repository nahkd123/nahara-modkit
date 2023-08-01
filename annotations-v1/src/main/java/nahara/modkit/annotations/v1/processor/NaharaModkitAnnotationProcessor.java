package nahara.modkit.annotations.v1.processor;

import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.StandardLocation;
import javax.tools.Diagnostic.Kind;

import com.google.auto.service.AutoService;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;

import nahara.modkit.annotations.v1.AutoMixin;
import nahara.modkit.annotations.v1.EntryPoint;
import nahara.modkit.annotations.v1.Mod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;

@SupportedAnnotationTypes("nahara.modkit.annotations.v1.*")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public class NaharaModkitAnnotationProcessor extends AbstractProcessor {
	private ModProcessingInfo modProcessingInfo;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		modProcessingInfo = new ModProcessingInfo();

		System.out.println(processingEnv.getOptions());
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		for (var modElement : roundEnv.getElementsAnnotatedWith(Mod.class)) {
			if (modProcessingInfo.modIndex != null) processingEnv.getMessager().printMessage(Kind.WARNING, "There are more than 1 @Mod annotations declared; using modid = " + modElement.getAnnotation(Mod.class));
			modProcessingInfo.modIndex = modElement.getAnnotation(Mod.class);
		}

		for (var entryPoint : roundEnv.getElementsAnnotatedWith(EntryPoint.class)) {
			if (!entryPoint.getKind().isClass()) {
				processingEnv.getMessager().printMessage(Kind.WARNING, "@EntryPoint annotated element is not a class", entryPoint);
				continue;
			}

			if (!(entryPoint instanceof TypeElement entryPointClass)) {
				processingEnv.getMessager().printMessage(Kind.WARNING, "@EntryPoint annotated element does not have a qualified name, skipping", entryPoint);
				continue;
			}

			var universalModInit = processingEnv.getElementUtils().getTypeElement(ModInitializer.class.getName());
			var clientModInit = processingEnv.getElementUtils().getTypeElement(ClientModInitializer.class.getName());

			if (processingEnv.getTypeUtils().isAssignable(entryPoint.asType(), universalModInit.asType())) {
				modProcessingInfo.commonEntryPoints.add(entryPointClass.getQualifiedName().toString());
			} else if (processingEnv.getTypeUtils().isAssignable(entryPoint.asType(), clientModInit.asType())) {
				modProcessingInfo.clientEntryPoints.add(entryPointClass.getQualifiedName().toString());
			} else {
				processingEnv.getMessager().printMessage(Kind.WARNING, "@EntryPoint annotated element does not implement " + ModInitializer.class.getName() + " or " + ClientModInitializer.class.getName() + " interface", entryPoint);
			}
		}

		for (var mixin : roundEnv.getElementsAnnotatedWith(AutoMixin.class)) {
			if (!mixin.getModifiers().contains(Modifier.ABSTRACT)) {
				processingEnv.getMessager().printMessage(Kind.NOTE, "@AutoMixin on non-abstract class; you might instantiate this class accidentally", mixin);
			}

			if (!(mixin instanceof TypeElement mixinClass)) {
				processingEnv.getMessager().printMessage(Kind.WARNING, "@AutoMixin annotated element does not have a qualified name, skipping", mixin);
				continue;
			}

			var annotation = mixin.getAnnotation(AutoMixin.class);
			(annotation.isClient()? modProcessingInfo.clientMixins : modProcessingInfo.commonMixins).mixins.add(mixinClass.getQualifiedName().toString());
		}

		if (roundEnv.processingOver()) finalizeProcessing();
		return false;
	}

	private void finalizeProcessing() {
		modProcessingInfo.finalizeProcessing();
		modProcessingInfo.createIndex().ifPresent(obj -> createJsonResource("fabric.mod.json", obj));
		modProcessingInfo.computedCommonMixins.createConfig(processingEnv.getSourceVersion()).ifPresent(obj -> createJsonResource(modProcessingInfo.computedCommonMixins.getConfigName(), obj));
		modProcessingInfo.computedClientMixins.createConfig(processingEnv.getSourceVersion()).ifPresent(obj -> createJsonResource(modProcessingInfo.computedClientMixins.getConfigName(), obj));
	}

	private void createJsonResource(String path, JsonElement json) {
		try {
			var fileObj = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", path);
			var writer = new JsonWriter(fileObj.openWriter());
			new GsonBuilder().disableHtmlEscaping().create().toJson(json, writer);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			processingEnv.getMessager().printMessage(Kind.WARNING, "Failed to write to " + path + ": IOException thrown");
		}
	}
}
