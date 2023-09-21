package nahara.modkit.gui.v1.widget;

/**
 * <p>
 * A widget can be anything (it doesn't have to be {@link Drawable}).
 * </p>
 */
public interface Widget<T extends Widget<T>> {
	public void useManager(WidgetsManager manager);
}
