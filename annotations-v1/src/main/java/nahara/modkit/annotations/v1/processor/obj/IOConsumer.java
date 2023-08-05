package nahara.modkit.annotations.v1.processor.obj;

import java.io.IOException;

@FunctionalInterface
public interface IOConsumer<T> {
	public void accept(T obj) throws IOException;
}
