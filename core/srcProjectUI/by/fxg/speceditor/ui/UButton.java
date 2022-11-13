package by.fxg.speceditor.ui;

import com.badlogic.gdx.graphics.Color;

import by.fxg.pilesos.graphics.PilesosScissorStack;
import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.SpecEditor;
import by.fxg.speceditor.std.ui.SpecInterface;
import by.fxg.speceditor.std.ui.SpecInterface.AppCursor;
import by.fxg.speceditor.std.ui.SpecInterface.UColor;
import by.fxg.speceditor.std.ui.UIElement;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class UButton extends UIElement {
	protected String name;
	protected boolean enabled = true;
	protected Color color;
	
	public UButton(String name, float x, float y, float width, float height) { this(name); this.setTransforms(x, y, width, height); }
	public UButton(String name) { 
		this.name = name;
		this.color = UColor.elementDefaultColor;
	}

	public void render(ShapeDrawer shape, Foster foster) {
		prevColor = shape.getPackedColor();
		if (this.enabled) {
			shape.setColor(this.color);
			shape.filledRectangle(this.x, this.y, this.width, this.height);
			if (SpecEditor.get.getInput().isMouseDown(0, true) && this.isMouseOver()) {
				SpecInterface.setCursor(AppCursor.POINTING);
				shape.setColor(UColor.elementBoundsClicked);
				shape.rectangle(this.x, this.y + 1, this.width - 1, this.height - 1, 2);
			} else if (this.isMouseOver()) {
				SpecInterface.setCursor(AppCursor.POINT);
				shape.setColor(UColor.elementHover);
				shape.filledRectangle(this.x, this.y, this.width, this.height);
			}
		} else {
			if (this.isMouseOver()) SpecInterface.setCursor(AppCursor.UNAVAILABLE);
			shape.setColor(UColor.elementDefaultColor);
			shape.filledRectangle(this.x, this.y, this.width, this.height);
		}
		shape.getBatch().flush();
		if (PilesosScissorStack.instance.peekScissors(this.x, this.y, this.width, this.height)) {
			foster.setString(this.name).draw(this.x + this.width / 2, this.y + this.height / 2 - foster.getHalfHeight());
			shape.getBatch().flush();
			PilesosScissorStack.instance.popScissors();
		}
		shape.setColor(prevColor);
	}
	
	public String getName() { return this.name; }
	public UButton setName(String name) {
		this.name = name;
		return this;
	}
	
	public Color getColor() { return this.color; }
	public UButton setColor(Color color) {
		this.color = color;
		return this;
	}
	
	public boolean isEnabled() { return this.enabled; }
	public UButton setEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}
	
	public UButton setTransforms(float x, float y, float width, float height) {
		this.x = (int)x;
		this.y = (int)y;
		this.width = (int)width;
		this.height = (int)height;
		return this;
	}
	
	public boolean isPressed() {
		return this.enabled && SpecEditor.get.getInput().isMouseDown(0, false) && this.isMouseOver();
	}
}
