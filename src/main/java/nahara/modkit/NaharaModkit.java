package nahara.modkit;

import nahara.modkit.annotations.v1.Dependencies;
import nahara.modkit.annotations.v1.Dependency;
import nahara.modkit.annotations.v1.Mod;

@Mod(
		modid = "nahara-modkit",
		name = "Nahara's Modkit",
		authors = "nahkd123",
		version = "${version}",
		license = "MIT")
@Dependencies({
	@Dependency(value = "fabricloader"),
	@Dependency(value = "minecraft", version = ">=1.20"),
	@Dependency(value = "java", version = ">=17")
})
public class NaharaModkit {
}
