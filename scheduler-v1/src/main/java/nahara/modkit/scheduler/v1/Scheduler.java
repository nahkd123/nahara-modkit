package nahara.modkit.scheduler.v1;

import java.util.ArrayList;
import java.util.List;

import nahara.common.tasks.Task;
import nahara.common.tasks.interfaces.TaskSupplier;
import net.minecraft.server.MinecraftServer;

public class Scheduler {
	private MinecraftServer server;
	private List<ScheduledTask<?>> scheduled = new ArrayList<>();
	private boolean iterating = false;
	private List<ScheduledTask<?>> pending = new ArrayList<>();

	public Scheduler(MinecraftServer server) {
		this.server = server;
	}

	public MinecraftServer getServer() {
		return server;
	}

	public <T> Task<T> schedule(int delayTicks, TaskSupplier<T> supplier) {
		var task = new ScheduledTask<T>(supplier, server.getTicks() + delayTicks);
		(iterating? pending : scheduled).add(task);
		return task;
	}

	public Task<Void> schedule(int delayTicks, Runnable runnable) {
		return schedule(delayTicks, () -> {
			runnable.run();
			return null;
		});
	}

	public <T> Task<T> scheduleNextTick(TaskSupplier<T> supplier) {
		return schedule(0, supplier);
	}

	public Task<Void> scheduleNextTick(Runnable runnable) {
		return schedule(0, runnable);
	}

	public Task<Void> wait(int ticks) {
		return schedule(ticks, () -> null);
	}

	@SuppressWarnings("unchecked")
	public void tick() {
		var now = server.getTicks();
		var iter = scheduled.iterator();

		iterating = true;
		while (iter.hasNext()) {
			var task = iter.next();

			if (now >= task.getTimestampTick()) {
				iter.remove();

				try {
					var obj = task.getSupplier().get();
					((ScheduledTask<Object>) task).resolveSuccess(obj);
				} catch (Throwable e) {
					task.resolveFailure(e);
				}
			}
		}
		iterating = false;

		if (pending.size() > 0) {
			scheduled.addAll(pending);
			pending.clear();
		}
	}
}
