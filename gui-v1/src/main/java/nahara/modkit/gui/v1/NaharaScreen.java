package nahara.modkit.gui.v1;

import org.jetbrains.annotations.Nullable;

import nahara.modkit.gui.v1.widget.Drawable;
import nahara.modkit.gui.v1.widget.Focusable;
import nahara.modkit.gui.v1.widget.WidgetsManager;
import nahara.modkit.gui.v1.widget.included.DrawableContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public abstract class NaharaScreen extends Screen implements WidgetsManager {
	protected DrawableContainer root = new DrawableContainer();
	protected Drawable<?> hovering = null;
	protected Focusable<?> focusing = null;
	protected boolean debugging = false;

	protected NaharaScreen(Text title) {
		super(title);
	}

	@Override
	protected void init() {
		root.useManager(this);
		root.useComputedGeometry(0, 0, width, height, 0, 0);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		root.useManager(this);
		root.onMouseMove(mouseX, mouseY, delta);
		root.onRender(context, mouseX, mouseY, delta);

		if (isDebugging()) {
			context.drawText(textRenderer, "Hovering " + hovering, 0, 0, 0xFF7F7F, true);
			context.drawText(textRenderer, "Focusing " + focusing, 0, 8, 0xFF7F7F, true);
			context.drawText(textRenderer, "X " + mouseX + "; Y " + mouseY, 0, 16, 0xFF7F7F, true);
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		int mX = (int) Math.round(mouseX);
		int mY = (int) Math.round(mouseY);
		if (focusing != null && ((Drawable<?>) focusing).onMouseDown(mX, mY, 0, button)) return true;
		return root.onMouseDown(mX, mY, 0, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		int mX = (int) Math.round(mouseX);
		int mY = (int) Math.round(mouseY);
		if (focusing != null && ((Drawable<?>) focusing).onMouseUp(mX, mY, 0, button)) return true;
		return root.onMouseUp(mX, mY, 0, button);
	}

	@Override
	public <T extends Drawable<T>> void useHovering(@Nullable T widget) {
		hovering = widget;
	}

	@Override
	public Drawable<?> getHovering() { return hovering; }

	@Override
	public <T extends Focusable<T>> void useFocus(@Nullable T widget) {
		if (focusing != null) focusing.onFocusLost();
		if ((focusing = widget) != null) widget.onFocusGain();
	}

	@Override
	public Focusable<?> getFocus() { return focusing; }

	@Override
	public boolean isDebugging() { return debugging && client.options.debugEnabled; }

	@Override
	public TextRenderer getTextRenderer() { return textRenderer; }

	@Override
	public MinecraftClient getClient() { return client; }
}
