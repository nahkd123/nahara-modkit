package nahara.modkit.gui.v1.widget;

public interface Focusable<T extends Focusable<T>> {
	default void onFocusGain() {}

	default void onFocusLost() {}

	default void onCharacterTyped(char ch, int modifiers) {}

	default void onKeyDown(int keyCode, int scanCode, int modifiers) {}

	default void onKeyUp(int keyCode, int scanCode, int modifiers) {}
}
