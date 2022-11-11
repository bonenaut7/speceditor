package by.fxg.speceditor.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;

import by.fxg.pilesos.graphics.PilesosScissorStack;
import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.SpecEditor;
import by.fxg.speceditor.std.ui.SpecInterface;
import by.fxg.speceditor.std.ui.SpecInterface.AppCursor;
import by.fxg.speceditor.std.ui.SpecInterface.UColor;
import by.fxg.speceditor.std.ui.UIElement;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class UHoldButton extends UIElement {
	public static final int NO_KEY = -65536;
	private int keyCode, targetTicks, ticks;
	private String name;
	private boolean enabled = true;
	private Color color;
	
	public UHoldButton(String name, int keyCode, int targetTicks, int x, int y, int width, int height) { this(name, keyCode, targetTicks); this.setTransforms(x, y, width, height); }
	public UHoldButton(String name, int keyCode, int targetTicks) {
		this.name = name;
		this.keyCode = keyCode;
		this.targetTicks = targetTicks;
		this.color = UColor.gray;
	}
	
	public void update() {
		if (this.isKeyTouched()) this.ticks++;
		else this.ticks = 0;
	}
	
	public void render(ShapeDrawer shape, Foster foster) {
		prevColor = shape.getPackedColor();
		if (this.enabled) {
			shape.setColor(this.color);
			shape.filledRectangle(this.x, this.y, this.width, this.height);
			if (this.ticks > 0) {
				if (this.isMouseOver()) SpecInterface.setCursor(AppCursor.POINTING);
				shape.setColor(UColor.elementBoundsClicked);
				shape.rectangle(this.x, this.y + 1, this.width - 1, this.height - 1, 2);
				shape.setColor(0.75f, 0.75f, 0.75f, 1.0f);
				shape.filledRectangle(this.x + 2, this.y + 2, this.width - 4, Interpolation.linear.apply(0, this.height - 4, Math.min(this.targetTicks, this.ticks) / (float)this.targetTicks));
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
	
	public Color getColor() { return this.color; }
	public UHoldButton setColor(Color color) {
		this.color = color;
		return this;
	}
	
	public boolean isEnabled() { return this.enabled; }
	public UHoldButton setEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}
	
	public UHoldButton setTransforms(float x, float y, float width, float height) {
		this.x = (int)x;
		this.y = (int)y;
		this.width = (int)width;
		this.height = (int)height;
		return this;
	}
	
	public boolean isPressed() {
		return this.enabled && this.isKeyTouched() && this.ticks == this.targetTicks;
	}
	
	private boolean isKeyTouched() {
		return this.enabled && (this.keyCode != NO_KEY && SpecEditor.get.getInput().isKeyboardDown(this.keyCode, true) && SpecInterface.isFocused(this) || SpecEditor.get.getInput().isMouseDown(0, true) && this.isMouseOver());
	}
}
