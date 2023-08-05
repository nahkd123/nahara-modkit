package nahara.modkit.annotations.v1;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * <p>Annotate any of your class with this annotation to supply additional details for {@code fabric.mod.json}.</p>
 */
@Documented
@Retention(SOURCE)
@Target(TYPE)
public @interface Mod {
	public String modid();
	public String version();
	public String name() default "";
	public String description() default "";
	public String[] authors() default {};
	public String license() default "";
	public Env modEnvironment() default Env.ALL;
	public String icon() default "";
}
