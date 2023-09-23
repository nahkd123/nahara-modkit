package nahara.modkit.gui.v1.widget.included;

import org.jetbrains.annotations.Nullable;

import nahara.modkit.gui.v1.widget.AbstractDrawable;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class Label extends AbstractDrawable<Label> {
	protected Text text = Text.empty();
	protected int color = 0xFFFFFFFF;
	protected boolean shadow = true;

	public Text getText() { return text; }

	public void setText(@Nullable Text text) { this.text = text == null ? Text.empty() : text; }

	public Label text(Text text) {
		setText(text);
		return this;
	}

	public int getColor() { return color; }

	public void setColor(int color) { this.color = color; }

	public Label color(int color) {
		setColor(color);
		return this;
	}

	public boolean hasShadow() {
		return shadow;
	}

	public void setShadow(boolean shadow) { this.shadow = shadow; }

	public Label shadow(boolean shadow) {
		setShadow(shadow);
		return this;
	}

	@Override
	public void onRender(DrawContext context, float mouseX, float mouseY, float delta) {
		TextRenderer renderer = manager.getTextRenderer();
		context.enableScissor(globalX, globalY, globalX + width, globalY + height);
		if (hasShadow()) context.drawText(renderer, getText().getString(), x, y, color, false);
		context.drawText(renderer, getText(), x, y, color, false);
		context.disableScissor();
	}
}
