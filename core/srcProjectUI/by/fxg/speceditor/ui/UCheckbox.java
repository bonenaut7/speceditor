package by.fxg.speceditor.ui;

import by.fxg.speceditor.Game;
import by.fxg.speceditor.std.ui.SpecInterface;
import by.fxg.speceditor.std.ui.UIElement;
import by.fxg.speceditor.std.ui.SpecInterface.AppCursor;
import by.fxg.speceditor.std.ui.SpecInterface.UColor;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class UCheckbox extends UIElement {
	private boolean enabled = true, value = false;

	public UCheckbox(int x, int y, int width, int height) { this(false, x, y, width, height); }
	public UCheckbox(boolean value, int x, int y, int width, int height) {
		this.value = value;
		this.setTransforms(x, y, width, height);
	}

	public void update() {
		if (this.enabled && Game.get.getInput().isMouseDown(0, false) && this.isMouseOver()) this.value = !this.value;
	}
	
	public void render(ShapeDrawer shape) {
		if (this.enabled) {
			shape.setColor(UColor.gray);
			if (this.isMouseOver()) {
				SpecInterface.setCursor(Game.get.getInput().isMouseDown(0, true) ? AppCursor.POINTING : AppCursor.POINT);
				shape.filledRectangle(this.x, this.y, this.width, this.height);
			} else shape.rectangle(this.x + 1, this.y + 1, this.width - 2, this.height - 2, 2);
		} else {
			if (this.isMouseOver()) SpecInterface.setCursor(AppCursor.UNAVAILABLE);
			shape.setColor(UColor.gray);
			shape.filledRectangle(this.x, this.y, this.width, this.height);
		}
		if (this.value) {
			shape.setColor(UColor.white);
			shape.line(this.x + 2, this.y + this.height / 2, this.x + this.width / 2, this.y + 2);
			shape.line(this.x + this.width / 2, this.y + 2, this.x + this.width - 2, this.y + this.height - 2);
		}
	}
	
	public UCheckbox setEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}
	
	public UCheckbox setValue(boolean value) {
		this.value = value;
		return this;
	}
	
	public UCheckbox setTransforms(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		return this;
	}
	
	public boolean isEnabled() {
		return this.enabled;
	}
	
	public boolean getValue() {
		return this.value;
	}
}
