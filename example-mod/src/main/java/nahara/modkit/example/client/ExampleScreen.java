package nahara.modkit.example.client;

import nahara.modkit.gui.v1.NaharaScreen;
import nahara.modkit.gui.v1.layout.Anchor;
import nahara.modkit.gui.v1.layout.Axis;
import nahara.modkit.gui.v1.widget.included.Box;
import nahara.modkit.gui.v1.widget.included.DrawableContainer;
import nahara.modkit.gui.v1.widget.included.NaharaButton;
import nahara.modkit.gui.v1.widget.included.PannableDrawableContainer;
import nahara.modkit.gui.v1.widget.included.Textbox;
import net.minecraft.text.Text;

public class ExampleScreen extends NaharaScreen {
	public ExampleScreen() {
		super(Text.literal("Example Nahara Screen"));

		for (Anchor a : Anchor.values()) {
			root.add(new Box()
				.x(0).y(0).width(20).height(20)
				.layout(layout -> layout.setAnchor(a).setOrigin(a)));
			root.add(new NaharaButton()
				.label(Text.literal(a.toString()))
				.x(0).y(0).width(100).height(14)
				.layout(layout -> layout.setAnchor(a).setOrigin(a)));
		}

		root.add(new PannableDrawableContainer()
			.x(300).y(10).width(200).height(200)
			.add(new Box().x(0).y(0).width(20).height(20).backgroundColor(0xFFFF0000))
			.add(new Box().x(10).y(10).width(20).height(20).backgroundColor(0xFF00FF00))
			.add(new Box().x(20).y(20).width(20).height(20).backgroundColor(0xFF0000FF)
				.layout(layout -> layout.setFillAxes(Axis.Y)))
			.add(new NaharaButton().label(Text.literal("")).x(40).y(0).width(50).height(24))
			.add(new Textbox().content("Hello world!").x(40).y(25).width(100).height(24)
				.cursorFrom(6).cursorTo(11)));

		var boxThing = new DrawableContainer().x(10).y(10).width(100).height(100);
		boxThing.add(new NaharaButton() {
			@Override
			public boolean onMouseDown(int mouseX, int mouseY, float delta, int button) {
				if (super.onMouseDown(mouseX, mouseY, delta, button)) {
					boxThing.setX(boxThing.getX() - 1);
					return true;
				} else {
					return false;
				}
			}
		}
			.label(Text.literal("<")).x(0).y(0).width(14).height(14)
			.layout(layout -> layout.setAnchor(Anchor.MIDDLE_LEFT).setOrigin(Anchor.MIDDLE_LEFT)));
		boxThing.add(new NaharaButton() {
			@Override
			public boolean onMouseDown(int mouseX, int mouseY, float delta, int button) {
				if (super.onMouseDown(mouseX, mouseY, delta, button)) {
					boxThing.setX(boxThing.getX() + 1);
					return true;
				} else {
					return false;
				}
			}
		}
			.label(Text.literal(">")).x(0).y(0).width(14).height(14)
			.layout(layout -> layout.setAnchor(Anchor.MIDDLE_RIGHT).setOrigin(Anchor.MIDDLE_RIGHT)));
		boxThing.add(new NaharaButton() {
			int lastX, lastY;

			@Override
			public boolean onMouseDown(int mouseX, int mouseY, float delta, int button) {
				if (super.onMouseDown(mouseX, mouseY, delta, button)) {
					lastX = mouseX + globalX;
					lastY = mouseY + globalY;
					return true;
				} else {
					return false;
				}
			}

			@Override
			public boolean onMouseMove(int mouseX, int mouseY, float delta) {
				if (manager.getFocus() == this && isPressing()) {
					boxThing.setX(boxThing.getX() + (mouseX + globalX - lastX));
					boxThing.setY(boxThing.getY() + (mouseY + globalY - lastY));
					lastX = mouseX + globalX;
					lastY = mouseY + globalY;
					return true;
				} else {
					return super.onMouseMove(mouseX, mouseY, delta);
				}
			}
		}
			.label(Text.literal("DRAG")).x(0).y(0).width(24).height(24)
			.layout(layout -> layout.setAnchor(Anchor.MIDDLE).setOrigin(Anchor.MIDDLE)));
		root.add(boxThing);

		debugging = true;
	}
}
