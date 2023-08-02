package nahara.modkit.annotations.v1;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import net.fabricmc.loader.api.metadata.ModEnvironment;

/**
 * <p>Annotate your class or <b>static</b> method with this annotation to automagically include it inside
 * {@code fabric.mod.json}.</p>
 */
@Documented
@Retention(SOURCE)
@Target({ TYPE, METHOD })
public @interface EntryPoint {
	public ModEnvironment environment() default ModEnvironment.UNIVERSAL;
}
