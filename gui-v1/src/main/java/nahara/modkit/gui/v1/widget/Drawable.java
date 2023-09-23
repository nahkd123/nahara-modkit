package nahara.modkit.gui.v1.widget;

import java.util.function.Consumer;

import nahara.modkit.gui.v1.layout.Layout;
import nahara.modkit.gui.v1.widget.included.DrawableContainer;
import net.minecraft.client.gui.DrawContext;

/**
 * <p>
 * A widget that can be drawn is called a {@link Drawable}. Drawables must also
 * implements {@link Widget}.
 * </p>
 */
// TODO should we move geometry stuffs like getX() to Geometry<T>?
public interface Drawable<T extends Drawable<T>> {
	/**
	 * <p>
	 * Return true if this {@link Drawable} needs to be recomputed.
	 * </p>
	 * <p>
	 * This will be changed when you call methods that modify the visual of this
	 * component, like using {@link #setWidth(int)} for example.
	 * </p>
	 * <p>
	 * This will be used by
	 * {@link DrawableContainer#onRender(DrawContext, int, int, float)} to recompute
	 * geometry before rendering to screen.
	 * </p>
	 * 
	 * @return Recompute request state.
	 */
	public boolean geometryRecomputeNeeded();

	public int getX();

	public void setX(int x);

	@SuppressWarnings("unchecked")
	default T x(int x) {
		setX(x);
		return (T) this;
	}

	public int getY();

	public void setY(int y);

	@SuppressWarnings("unchecked")
	default T y(int y) {
		setY(y);
		return (T) this;
	}

	public int getWidth();

	public void setWidth(int width);

	@SuppressWarnings("unchecked")
	default T width(int width) {
		setWidth(width);
		return (T) this;
	}

	public int getHeight();

	public void setHeight(int height);

	@SuppressWarnings("unchecked")
	default T height(int height) {
		setHeight(height);
		return (T) this;
	}

	public boolean isVisible();

	public void setVisible(boolean visible);

	@SuppressWarnings("unchecked")
	default T visible(boolean visible) {
		setVisible(visible);
		return (T) this;
	}

	/**
	 * <p>
	 * Attach the computed geometry to this {@link Drawable}. The
	 * {@link #onRender(DrawContext, int, int, float)} method must renders this
	 * {@link Drawable} within this geometry constraint.
	 * </p>
	 * <p>
	 * This method will be called by {@link Layout#applyTo(Drawable, int, int)}.
	 * </p>
	 * 
	 * @param x       X position, relative to parent container.
	 * @param y       Y position, relative to parent container.
	 * @param width   Width of computed geometry.
	 * @param height  Height of computed geometry.
	 * @param globalX The global X position of this {@link Drawable}.
	 * @param globalY The global Y position of this {@link Drawable}.
	 */
	public void useComputedGeometry(int x, int y, int width, int height, int globalX, int globalY);

	public void getComputedGeometry(int[] toArray);

	/**
	 * <p>
	 * Get the auto-layout for this {@link Drawable}. Auto-layout instances will
	 * automatically apply position and size to this {@link Drawable} when needed.
	 * </p>
	 * <p>
	 * Modify the layout by calling setters inside {@link Layout}.
	 * </p>
	 * 
	 * @return The auto-layout instance.
	 */
	public Layout getLayout();

	/**
	 * <p>
	 * Apply layout from {@link Consumer} and return this {@link Drawable}
	 * immediately.
	 * </p>
	 * 
	 * @param consumer The consumer that will modify {@link Layout}.
	 * @return this.
	 */
	@SuppressWarnings("unchecked")
	default T layout(Consumer<Layout> consumer) {
		consumer.accept(getLayout());
		return (T) this;
	}

	/**
	 * <p>
	 * Will be called when draw is needed.
	 * </p>
	 * 
	 * @param context The draw context.
	 * @param mouseX  User's mouse X position.
	 * @param mouseY  User's mouse Y position.
	 * @param delta   Progress from previous to next tick (from {@code 0.0} to
	 *                {@code 1.0}). Should be used for animations.
	 */
	default void onRender(DrawContext context, float mouseX, float mouseY, float delta) {}

	/**
	 * <p>
	 * Called when user pressed down their mouse button.
	 * </p>
	 * 
	 * @param mouseX User's mouse X position.
	 * @param mouseY User's mouse Y position.
	 * @param delta  Progress from previous to next tick. Can be used for
	 *               timestamping (if you need accurate timing, but why?)
	 * @param button The button that user had pressed.
	 * @return true if the widget is clicked successfully. By default, this will
	 *         returns true if user's mouse is inside the bounding box of this
	 *         widget.
	 */
	default boolean onMouseDown(float mouseX, float mouseY, float delta, int button) {
		return testGeometry(mouseX, mouseY);
	}

	/**
	 * <p>
	 * Called when user released their mouse button.
	 * </p>
	 * 
	 * @param mouseX User's mouse X position.
	 * @param mouseY User's mouse Y position.
	 * @param delta  Progress from previous to next tick. Can be used for
	 *               timestamping (if you need accurate timing, but why?)
	 * @param button The button that user had released.
	 * @return true if the widget is clicked successfully. By default, this will
	 *         returns true if user's mouse is inside the bounding box of this
	 *         widget.
	 */
	default boolean onMouseUp(float mouseX, float mouseY, float delta, int button) {
		return testGeometry(mouseX, mouseY);
	}

	/**
	 * <p>
	 * Called when user moved their mouse. This does not requires the mouse button
	 * to be held down.
	 * </p>
	 * 
	 * @param mouseX User's mouse X position.
	 * @param mouseY User's mouse Y position.
	 * @param delta  Progress from previous to next tick. Can be used for
	 *               timestamping (if you need accurate timing, but why?)
	 * @return true if the widget is clicked successfully. By default, this will
	 *         returns true if user's mouse is inside the bounding box of this
	 *         widget.
	 */
	default boolean onMouseMove(float mouseX, float mouseY, float delta) {
		return testGeometry(mouseX, mouseY);
	}

	/**
	 * <p>
	 * Called when user scrolled while hovering this {@link Drawable}.
	 * </p>
	 * 
	 * @param mouseX User's mouse X position.
	 * @param mouseY User's mouse Y position.
	 * @param deltaX Amount of pixels scrolled by user in horizontal.
	 * @param deltaY Amount of pixels scrolled by user in vertical.
	 * @return true if this {@link Drawable} can be scrolled and you want to prevent
	 *         scroll events from emitting to other drawables.
	 */
	default boolean onMouseScroll(float mouseX, float mouseY, float deltaX, float deltaY) {
		return false;
	}

	/**
	 * <p>
	 * Test if the point is inside this {@link Drawable}.
	 * </p>
	 * 
	 * @param pointX Point X.
	 * @param pointY Point Y.
	 * @return true if the point is located inside this {@link Drawable}.
	 */
	default boolean testGeometry(float pointX, float pointY) {
		if (!isVisible()) return false;
		int[] geom = new int[4];
		getComputedGeometry(geom);
		return pointX >= geom[0] && pointX < geom[0] + geom[2] && pointY >= geom[1] && pointY < geom[1] + geom[3];
	}
}
