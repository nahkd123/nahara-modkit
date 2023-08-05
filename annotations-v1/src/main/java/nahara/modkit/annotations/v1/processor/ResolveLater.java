package nahara.modkit.annotations.v1.processor;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class ResolveLater<T> {
	private T resolved = null;
	private Supplier<Optional<T>> supplier;

	public ResolveLater(Supplier<Optional<T>> supplier) {
		this.supplier = supplier;
	}

	public ResolveLater() {
	}

	public void resolve(T obj) {
		if (resolved != null) return;
		if (obj == null) throw new IllegalArgumentException("object can't be null");
		resolved = obj;
	}

	public Optional<T> tryResolve() {
		if (supplier != null) return supplier.get();
		return Optional.ofNullable(resolved);
	}

	public <R> ResolveLater<R> map(Function<T, R> mapper) {
		return new ResolveLater<>(() -> tryResolve().map(mapper));
	}

	public ResolveLater<T> or(Supplier<T> supplier) {
		return new ResolveLater<T>(() -> {
			if (resolved == null) return Optional.ofNullable(supplier.get());
			return tryResolve();
		});
	}
}
