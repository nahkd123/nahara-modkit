package nahara.modkit.gui.v1.widget.included;

import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import nahara.modkit.gui.v1.widget.AbstractDrawable;
import nahara.modkit.gui.v1.widget.Focusable;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class Textbox extends AbstractDrawable<Textbox> implements Focusable<Textbox> {
	{
		width = 100;
		height = 24;
	}

	protected String content = "";
	protected int scroll = 0, cursorFrom = 0, cursorTo = 0;
	private boolean dragging = false;

	public String getContent() { return content; }

	public void setContent(@Nullable String content) {
		this.content = content == null ? "" : content;
		cursorFrom(cursorFrom).cursorTo(cursorTo);
	}

	public Textbox content(String content) {
		setContent(content);
		return this;
	}

	public int getScroll() { return scroll; }

	public void setScroll(int scroll) { this.scroll = scroll; }

	public Textbox scroll(int scroll) {
		setScroll(scroll);
		return this;
	}

	public int getCursorFrom() { return cursorFrom; }

	public void setCursorFrom(int cursorFrom) {
		this.cursorFrom = Math.max(Math.min(cursorFrom, getContent().length()), 0);
	}

	public Textbox cursorFrom(int cursorFrom) {
		setCursorFrom(cursorFrom);
		return this;
	}

	public int getCursorTo() { return cursorTo; }

	public void setCursorTo(int cursorTo) { this.cursorTo = Math.max(Math.min(cursorTo, getContent().length()), 0); }

	public Textbox cursorTo(int cursorTo) {
		setCursorTo(cursorTo);
		return this;
	}

	@Override
	public void onRender(DrawContext context, float mouseX, float mouseY, float delta) {
		boolean focused = manager.getFocus() == this;
		boolean underscore = cursorFrom == cursorTo && cursorTo >= getContent().length();
		int fontHeight = manager.getTextRenderer().fontHeight;

		String beforeSel = getContent().substring(0, Math.min(cursorFrom, cursorTo));
		String selected = getContent().substring(Math.min(cursorFrom, cursorTo), Math.max(cursorFrom, cursorTo));
		String afterSel = getContent().substring(Math.max(cursorFrom, cursorTo));
		int beforeSelPx = manager.getTextRenderer().getWidth(beforeSel);
		int selectedPx = manager.getTextRenderer().getWidth(selected);
		int cursorOffset = manager.getTextRenderer().getWidth(getContent().substring(0, cursorTo));

		context.fill(x, y, x + width, y + height, 0xFF000000);
		context.drawBorder(x, y, width, height, focused ? 0xFFFFFFFF : 0xFF7F7F7F);

		context.enableScissor(globalX, globalY, globalX + width - 1, globalY + height);
		context.fill(
			x + 5 - scroll + beforeSelPx, y + (height - fontHeight) / 2 - 1,
			x + 5 - scroll + beforeSelPx + selectedPx, y + (height + fontHeight) / 2 + 1,
			focused ? 0xFFFFFFFF : 0xFFDFDFDF);

		context.drawText(
			manager.getTextRenderer(), beforeSel,
			x + 5 - scroll,
			y + (height - fontHeight) / 2 + 1,
			0xFFFFFF, false);
		context.drawText(
			manager.getTextRenderer(), selected,
			x + 5 - scroll + beforeSelPx,
			y + (height - fontHeight) / 2 + 1,
			0x5F5FFF, false);
		context.drawText(
			manager.getTextRenderer(), afterSel,
			x + 5 - scroll + beforeSelPx + selectedPx,
			y + (height - fontHeight) / 2 + 1,
			0xFFFFFF, false);

		if (!underscore) context.fill(
			x + 5 - scroll + cursorOffset, y + (height - fontHeight) / 2 - 1,
			x + 5 - scroll + cursorOffset + 1, y + (height + fontHeight) / 2 + 1,
			cursorFrom == cursorTo ? (focused ? 0xFFFFFFFF : 0x7FFFFFFF) : 0xFF5F5FFF);
		else context.fill(
			x + 5 - scroll + cursorOffset, y + (height + fontHeight) / 2,
			x + 5 - scroll + cursorOffset + 5, y + (height + fontHeight) / 2 + 1,
			focused ? 0xFFFFFFFF : 0x7FFFFFFF);

		context.disableScissor();
	}

	@Override
	public boolean onMouseMove(float mouseX, float mouseY, float delta) {
		if (dragging) {
			String trimmed = manager.getTextRenderer().trimToWidth(getContent(), Math.round(mouseX - x - 4 + scroll));
			setCursorTo(trimmed.length());
			return true;
		}

		if (super.onMouseMove(mouseX, mouseY, delta)) {
			manager.useHovering(this);
			return true;
		} else {
			if (manager.getHovering() == this) manager.useHovering(null);
			return false;
		}
	}

	@Override
	public boolean onMouseDown(float mouseX, float mouseY, float delta, int button) {
		if (super.onMouseDown(mouseX, mouseY, delta, button)) {
			manager.useFocus(this);
			String trimmed = manager.getTextRenderer().trimToWidth(getContent(), Math.round(mouseX - x - 4 + scroll));
			cursorFrom(trimmed.length()).cursorTo(trimmed.length());
			dragging = true;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean onMouseUp(float mouseX, float mouseY, float delta, int button) {
		if (manager.getFocus() == this) dragging = false;
		return super.onMouseUp(mouseX, mouseY, delta, button);
	}

	@Override
	public void onCharacterTyped(char ch, int modifiers) {
		boolean isCtrl = (modifiers & GLFW.GLFW_MOD_CONTROL) != 0;
		if (isCtrl) return;

		String beforeSel = getContent().substring(0, Math.min(cursorFrom, cursorTo));
		String afterSel = getContent().substring(Math.max(cursorFrom, cursorTo));
		setContent(beforeSel + ch + afterSel);
		cursorFrom = cursorTo = beforeSel.length() + 1;
	}

	@Override
	public void onKeyDown(int keyCode, int scanCode, int modifiers) {
		boolean isShift = (modifiers & GLFW.GLFW_MOD_SHIFT) != 0;
		boolean isCtrl = (modifiers & GLFW.GLFW_MOD_CONTROL) != 0;
		String beforeSel = getContent().substring(0, Math.min(cursorFrom, cursorTo));
		String selected = getContent().substring(Math.min(cursorFrom, cursorTo), Math.max(cursorFrom, cursorTo));
		String afterSel = getContent().substring(Math.max(cursorFrom, cursorTo));

		switch (keyCode) {
		case GLFW.GLFW_KEY_LEFT:
			if (isShift) {
				setCursorTo(getCursorTo() - 1);
				break;
			}
			if (cursorFrom == cursorTo) {
				setCursorTo(getCursorTo() - 1);
				setCursorFrom(getCursorTo());
				break;
			}
			if (cursorFrom < cursorTo) cursorTo = cursorFrom;
			if (cursorTo < cursorFrom) cursorFrom = cursorTo;
			break;
		case GLFW.GLFW_KEY_RIGHT:
			if (isShift) {
				setCursorTo(getCursorTo() + 1);
				break;
			}
			if (cursorFrom == cursorTo) {
				setCursorTo(getCursorTo() + 1);
				setCursorFrom(getCursorTo());
				break;
			}
			if (cursorFrom > cursorTo) cursorTo = cursorFrom;
			if (cursorTo > cursorFrom) cursorFrom = cursorTo;
			break;
		case GLFW.GLFW_KEY_BACKSPACE:
			if (selected.length() > 0) {
				setContent(beforeSel + afterSel);
				setCursorFrom(beforeSel.length());
				setCursorTo(beforeSel.length());
			} else if (beforeSel.length() > 0) {
				setContent(beforeSel.substring(0, beforeSel.length() - 1) + afterSel);
				setCursorFrom(beforeSel.length() - 1);
				setCursorTo(beforeSel.length() - 1);
			}
			break;
		case GLFW.GLFW_KEY_DELETE:
			if (selected.length() > 0) {
				setContent(beforeSel + afterSel);
				setCursorFrom(beforeSel.length());
				setCursorTo(beforeSel.length());
			} else if (afterSel.length() > 0) {
				setContent(beforeSel + afterSel.substring(1));
				setCursorFrom(beforeSel.length());
				setCursorTo(beforeSel.length());
			}
			break;
		case GLFW.GLFW_KEY_HOME:
			if (isShift) setCursorTo(0);
			else cursorFrom(0).cursorTo(0);
			break;
		case GLFW.GLFW_KEY_END:
			if (isShift) setCursorTo(getContent().length());
			else cursorFrom(getContent().length()).cursorTo(getContent().length());
			break;
		case GLFW.GLFW_KEY_A:
			if (!isCtrl) break;
			cursorFrom(0).cursorTo(getContent().length());
			break;
		case GLFW.GLFW_KEY_C:
			if (!isCtrl) break;
			manager.getClient().keyboard.setClipboard(selected);
			break;
		case GLFW.GLFW_KEY_V:
			if (!isCtrl) break;
			String newHead = beforeSel + manager.getClient().keyboard.getClipboard();
			setContent(newHead + afterSel);
			setCursorFrom(newHead.length());
			setCursorTo(newHead.length());
			break;
		case GLFW.GLFW_KEY_X:
			if (!isCtrl) break;
			manager.getClient().keyboard.setClipboard(selected);
			setContent(beforeSel + afterSel);
			setCursorFrom(beforeSel.length());
			setCursorTo(beforeSel.length());
			break;
		default:
			break;
		}
	}

	@Override
	public void drawDebugLines(Consumer<Object> debugConsumer) {
		debugConsumer.accept(Text.literal("Textbox = ")
			.append(Text.literal(content).styled(s -> s.withColor(Formatting.AQUA))));
	}
}
