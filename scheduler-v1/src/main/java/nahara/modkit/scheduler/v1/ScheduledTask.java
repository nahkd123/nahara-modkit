package nahara.modkit.scheduler.v1;

import nahara.common.tasks.ManualTask;
import nahara.common.tasks.interfaces.TaskSupplier;

public class ScheduledTask<T> extends ManualTask<T> {
	private TaskSupplier<T> supplier;
	private int timestampTick;

	public ScheduledTask(TaskSupplier<T> supplier, int timestampTick) {
		this.supplier = supplier;
		this.timestampTick = timestampTick;
	}

	public TaskSupplier<T> getSupplier() {
		return supplier;
	}

	public int getTimestampTick() {
		return timestampTick;
	}
}
