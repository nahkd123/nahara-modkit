package nahara.modkit.annotations.v1;

import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

@Documented
@Retention(SOURCE)
public @interface Dependency {
	public String value();

	/**
	 * <p>Dependency version that follows semver format.</p>
	 * @return Dependency version.
	 */
	public String version() default "*";
}
