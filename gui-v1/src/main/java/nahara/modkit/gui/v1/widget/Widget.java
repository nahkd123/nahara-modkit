package nahara.modkit.gui.v1.widget;

import java.util.function.Consumer;

import nahara.modkit.gui.v1.NaharaScreen;
import net.minecraft.text.Text;

/**
 * <p>
 * A widget can be anything (it doesn't have to be {@link Drawable}).
 * </p>
 */
public interface Widget<T extends Widget<T>> {
	public void useManager(WidgetsManager manager);

	/**
	 * <p>
	 * Draw debug text. This will only be called when F3 debug screen is visible and
	 * debugging state in {@link NaharaScreen} is enabled (disabled by default).
	 * </p>
	 * 
	 * @param debugConsumer The consumer that draws a new line of text.
	 */
	default void drawDebugLines(Consumer<Object> debugConsumer) {
		debugConsumer.accept(Text.literal(this.getClass().getSimpleName()));
	}
}
