package nahara.modkit.gui.v1.widget;

public class AbstractWidget<T extends AbstractWidget<T>> implements Widget<AbstractWidget<T>> {
	protected WidgetsManager manager;

	@Override
	public void useManager(WidgetsManager manager) {
		this.manager = manager;
	}
}
