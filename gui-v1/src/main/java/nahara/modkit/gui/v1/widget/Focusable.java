package nahara.modkit.gui.v1.widget;

public interface Focusable<T extends Focusable<T>> {
	default void onFocusGain() {}

	default void onFocusLost() {}
}
