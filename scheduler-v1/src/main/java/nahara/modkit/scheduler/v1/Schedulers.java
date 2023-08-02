package nahara.modkit.scheduler.v1;

import nahara.modkit.annotations.v1.Dependencies;
import nahara.modkit.annotations.v1.Dependency;
import nahara.modkit.annotations.v1.Mod;
import net.minecraft.server.MinecraftServer;

@Mod(
		modid = "nahara-modkit-scheduler-v1",
		name = "Nahara's Modkit - Scheduler (Library API v1)",
		authors = "nahkd123",
		version = "${version}",
		license = "MIT")
@Dependencies({
	@Dependency(value = "fabricloader"),
	@Dependency(value = "minecraft", version = ">=1.20"),
	@Dependency(value = "java", version = ">=17")
})
public class Schedulers {
	public static Scheduler from(MinecraftServer server) {
		return ((SchedulerProvider) server).getScheduler();
	}
}
