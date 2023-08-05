package nahara.modkit.annotations.v1;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * <p>Annotate your static field with this annotation to get it automatically registered to global registry.</p>
 * <p>Supported registries: {@code Registries.ITEM}</p>
 */
@Documented
@Retention(SOURCE)
@Target(FIELD)
public @interface AutoRegister {
	public String id() default "";
}
