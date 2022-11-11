package by.fxg.speceditor.ui;

import by.fxg.speceditor.SpecEditor;
import by.fxg.speceditor.std.ui.SpecInterface;
import by.fxg.speceditor.std.ui.SpecInterface.AppCursor;
import by.fxg.speceditor.std.ui.SpecInterface.UColor;
import by.fxg.speceditor.std.ui.UIElement;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class UCheckbox extends UIElement {
	protected boolean enabled = true;
	protected boolean value = false;

	public UCheckbox() { this(false); }
	public UCheckbox(int x, int y, int width, int height) { this(false); this.setTransforms(x, y, width, height); }
	public UCheckbox(boolean value, int x, int y, int width, int height) { this(value); this.setTransforms(x, y, width, height); }
	public UCheckbox(boolean value) {
		this.value = value;
		this.setTransforms(x, y, width, height);
	}

	public void update() {
		if (this.enabled && SpecEditor.get.getInput().isMouseDown(0, false) && this.isMouseOver()) {
			this.setValue(!this.getValue());
		}
	}
	
	public void render(ShapeDrawer shape) {
		prevColor = shape.getPackedColor();
		if (this.enabled) {
			shape.setColor(UColor.elementDefaultColor);
			shape.rectangle(this.x, this.y + 1, this.width - 1, this.height - 1, 2);
			if (this.isMouseOver()) {
				SpecInterface.setCursor(SpecEditor.get.getInput().isMouseDown(0, true) ? AppCursor.POINTING : AppCursor.POINT);
				shape.setColor(UColor.elementHover);
				shape.filledRectangle(this.x, this.y, this.width, this.height);
			}	
		} else {
			if (this.isMouseOver()) SpecInterface.setCursor(AppCursor.UNAVAILABLE);
			shape.setColor(UColor.elementDefaultColor);
			shape.filledRectangle(this.x, this.y, this.width, this.height);
		}
		if (this.value) {
			shape.setColor(UColor.elementIntensiveColor);
			shape.line(this.x + 2, this.y + this.height / 2, this.x + this.width / 2, this.y + 2);
			shape.line(this.x + this.width / 2, this.y + 2, this.x + this.width - 2, this.y + this.height - 2);
		}
		shape.setColor(prevColor);
	}
	
	public boolean isEnabled() { return this.enabled; }
	public UCheckbox setEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}
	
	public boolean getValue() { return this.value; }
	public UCheckbox setValue(boolean value) {
		this.value = value;
		return this;
	}
	
	public UCheckbox setTransforms(float x, float y, float width, float height) {
		this.x = (int)x;
		this.y = (int)y;
		this.width = (int)width;
		this.height = (int)height;
		return this;
	}
}
