package by.fxg.speceditor.ui;

import com.badlogic.gdx.graphics.Color;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.Game;
import by.fxg.speceditor.ui.SpecInterface.AppCursor;
import by.fxg.speceditor.ui.SpecInterface.UColor;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class UButton extends UIElement {
	private String name;
	private boolean enabled = true;
	private Color color;
	
	public UButton(String name, int x, int y, int width, int height) { this(name); this.setTransforms(x, y, width, height); }
	public UButton(String name) { 
		this.name = name;
		this.color = UColor.gray;
	}

	public void render(ShapeDrawer shape, Foster foster) {
		if (this.enabled) {
			shape.setColor(this.color);
			shape.filledRectangle(this.x, this.y, this.width, this.height);
			if (Game.get.getInput().isMouseDown(0, true) && this.isMouseOver()) {
				SpecInterface.setCursor(AppCursor.POINTING);
				shape.setColor(UColor.select);
				shape.rectangle(this.x, this.y, this.width, this.height, 2);
			} else if (this.isMouseOver()) {
				SpecInterface.setCursor(AppCursor.POINT);
				shape.setColor(UColor.overlay);
				shape.filledRectangle(this.x, this.y, this.width, this.height);
			}
		} else {
			if (this.isMouseOver()) SpecInterface.setCursor(AppCursor.UNAVAILABLE);
			shape.setColor(UColor.gray);
			shape.filledRectangle(this.x, this.y, this.width, this.height);
		}
		foster.setString(this.name).draw(this.x + this.width / 2, this.y + this.height / 2 + foster.getHalfHeight());
	}
	
	public UButton setName(String name) {
		this.name = name;
		return this;
	}
	
	public UButton setColor(Color color) {
		this.color = color;
		return this;
	}
	
	public UButton setEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}
	
	public UButton setTransforms(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		return this;
	}
	
	public boolean isPressed() {
		return this.enabled && Game.get.getInput().isMouseDown(0, false) && this.isMouseOver();
	}
	
	public boolean isEnabled() {
		return this.enabled;
	}
}
