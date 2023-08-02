package nahara.modkit.scheduler.v1.mixin;

import java.util.function.BooleanSupplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import nahara.modkit.annotations.v1.AutoMixin;
import nahara.modkit.scheduler.v1.Scheduler;
import nahara.modkit.scheduler.v1.SchedulerProvider;
import net.minecraft.server.MinecraftServer;

@AutoMixin
@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements SchedulerProvider {
	@Unique private Scheduler scheduler$scheduler;

	@Override
	public Scheduler getScheduler() {
		if (scheduler$scheduler == null) scheduler$scheduler = new Scheduler((MinecraftServer) (Object) this);
		return scheduler$scheduler;
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void scheduler$tick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
		if (scheduler$scheduler == null) return;
		scheduler$scheduler.tick();
	}
}
