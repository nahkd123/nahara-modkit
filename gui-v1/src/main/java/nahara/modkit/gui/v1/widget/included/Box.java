package nahara.modkit.gui.v1.widget.included;

import nahara.modkit.gui.v1.widget.AbstractDrawable;
import net.minecraft.client.gui.DrawContext;

/**
 * <p>
 * A box. You can create a pure solid box, a border box or both at the same
 * time.
 * </p>
 * 
 * @see #setBackgroundColor(int)
 * @see #setBorderColor(int)
 */
public class Box extends AbstractDrawable<Box> {
	protected int backgroundColor = 0xFFFFFFFF;
	protected int borderColor = 0x00000000;

	public int getBackgroundColor() { return backgroundColor; }

	public void setBackgroundColor(int backgroundColor) { this.backgroundColor = backgroundColor; }

	public Box backgroundColor(int backgroundColor) {
		setBackgroundColor(backgroundColor);
		return this;
	}

	public int getBorderColor() { return borderColor; }

	public void setBorderColor(int borderColor) { this.borderColor = borderColor; }

	public Box borderColor(int borderColor) {
		setBorderColor(borderColor);
		return this;
	}

	@Override
	public void onRender(DrawContext context, int mouseX, int mouseY, float delta) {
		if ((backgroundColor & 0xFF000000) != 0) context.fill(x, y, x + width, y + height, backgroundColor);
		if ((borderColor & 0xFF000000) != 0) context.drawBorder(x, y, width, height, borderColor);
	}
}
