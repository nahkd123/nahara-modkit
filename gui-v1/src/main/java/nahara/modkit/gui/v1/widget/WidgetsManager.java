package nahara.modkit.gui.v1.widget;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;

/**
 * <p>
 * Manage widgets inside current screen.
 * </p>
 */
public interface WidgetsManager {
	/**
	 * <p>
	 * Attach focus state to widget. Focused widget will have events listening
	 * priority higher than other widgets.
	 * </p>
	 * 
	 * @param widget The widget to focus. Use {@code null} to detach currently
	 *               focused widget.
	 */
	public <T extends Focusable<T>> void useFocus(@Nullable T widget);

	public Focusable<?> getFocus();

	/**
	 * <p>
	 * Set the current hovering widget.
	 * </p>
	 * <p>
	 * Note that containers must give {@link #useHovering(Drawable)} priority to
	 * children widgets before using this on itself.
	 * </p>
	 * 
	 * @param widget The widget that user is currently hovering.
	 */
	public <T extends Drawable<T>> void useHovering(@Nullable T widget);

	public Drawable<?> getHovering();

	/**
	 * <p>
	 * Check if UI debugging mode is enabled and F3 debug HUD is opened.
	 * </p>
	 * <p>
	 * Nahara Modkit UI uses this to draw debug borders.
	 * </p>
	 * 
	 * @return true if in debugging mode.
	 */
	public boolean isDebugging();

	public TextRenderer getTextRenderer();

	public MinecraftClient getClient();
}
