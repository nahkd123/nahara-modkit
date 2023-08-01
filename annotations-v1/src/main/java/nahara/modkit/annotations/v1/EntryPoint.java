package nahara.modkit.annotations.v1;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * <p>Annotate your class with this annotation to automagically include it inside {@code fabric.mod.json}.</p>
 */
@Documented
@Retention(SOURCE)
@Target(TYPE)
public @interface EntryPoint {
}
