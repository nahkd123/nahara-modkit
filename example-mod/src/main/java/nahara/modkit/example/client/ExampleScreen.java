package nahara.modkit.example.client;

import nahara.modkit.gui.v1.NaharaScreen;
import nahara.modkit.gui.v1.layout.Anchor;
import nahara.modkit.gui.v1.layout.Axis;
import nahara.modkit.gui.v1.widget.included.Box;
import nahara.modkit.gui.v1.widget.included.DrawableContainer;
import nahara.modkit.gui.v1.widget.included.FlowContainer;
import nahara.modkit.gui.v1.widget.included.Button;
import nahara.modkit.gui.v1.widget.included.PannableDrawableContainer;
import nahara.modkit.gui.v1.widget.included.ScrollProxy;
import nahara.modkit.gui.v1.widget.included.Textbox;
import net.minecraft.text.Text;

public class ExampleScreen extends NaharaScreen {
	public ExampleScreen() {
		super(Text.literal("Example Nahara Screen"));

		for (Anchor a : Anchor.values()) {
			root.add(new Box()
				.x(0).y(0).width(20).height(20)
				.layout(layout -> layout.setAnchor(a).setOrigin(a)));
			root.add(new Button()
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
			.add(new Button().label(Text.literal("")).x(40).y(0).width(50).height(24))
			.add(new Textbox().content("Hello world!").x(40).y(25).width(100).height(24)
				.cursorFrom(6).cursorTo(11))
			.add(new ScrollProxy(new FlowContainer().flowAxis(Axis.Y)
				.x(0).y(0).width(150)
				.add(
					new Button().label(Text.literal("Flow")).width(150).height(24),
					new Button().label(Text.literal("Container")).width(100).height(24),
					new Button().label(Text.literal("My")).width(100).height(24),
					new Button().label(Text.literal("Beloved")).width(100).height(24),
					new Button().label(Text.literal("Scroll down")).width(100).height(24),
					new Button().label(Text.literal("Test button")).width(100).height(24)))
				.scrollX(0).scrollY(0)
				.x(0).y(50).width(100).height(100)));

		var boxThing = new DrawableContainer().x(10).y(10).width(100).height(100);
		boxThing.add(new Button() {
			@Override
			public boolean onMouseDown(float mouseX, float mouseY, float delta, int button) {
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
		boxThing.add(new Button() {
			@Override
			public boolean onMouseDown(float mouseX, float mouseY, float delta, int button) {
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
		boxThing.add(new Button() {
			float lastX, lastY;

			@Override
			public boolean onMouseDown(float mouseX, float mouseY, float delta, int button) {
				if (super.onMouseDown(mouseX, mouseY, delta, button)) {
					lastX = mouseX + globalX;
					lastY = mouseY + globalY;
					return true;
				} else {
					return false;
				}
			}

			@Override
			public boolean onMouseMove(float mouseX, float mouseY, float delta) {
				if (manager.getFocus() == this && isPressing()) {
					boxThing.setX(boxThing.getX() + Math.round(mouseX + globalX - lastX));
					boxThing.setY(boxThing.getY() + Math.round(mouseY + globalY - lastY));
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
