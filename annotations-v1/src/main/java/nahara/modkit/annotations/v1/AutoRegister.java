package nahara.modkit.annotations.v1;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * <p>Annotate your static field with this annotation to get it automatically registered to global registry.</p>
 * <p>Supported registries: {@code Registries.ITEM}</p>
 * <p>Components that's annotated with this annotation will be registered inside generated entry point, which is
 * located in {@code nahara.generated.<modid>.<ModId>Main}</p>
 */
@Documented
@Retention(SOURCE)
@Target(FIELD)
public @interface AutoRegister {
	/**
	 * <p>ID of your component in {@code <namespace>:<id>} format. If {@code <namespace>:} is not present,
	 * the namespace will be your mod ID. If this value is empty, the generated ID will be {@code <modid>:<field name>}.</p>
	 * @return ID to register in generated entry point.
	 */
	public String id() default "";
}
