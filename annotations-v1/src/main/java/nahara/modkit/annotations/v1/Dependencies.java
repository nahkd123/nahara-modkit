package nahara.modkit.annotations.v1;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * <p>Declare a list of dependencies that your mod requires.</p>
 */
@Documented
@Retention(SOURCE)
@Target(TYPE)
public @interface Dependencies {
	public Dependency[] value() default {};
}
