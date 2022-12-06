package by.fxg.speceditor.std.ui;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;

import by.fxg.speceditor.std.ui.SpecInterface.UColor;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class STDScrollArea extends UIElement {
	private boolean isVertical, isHorizontal;
	private int vertScrollArea, horizScrollArea, verticalStep, horizontalStep, verticalValue, horizontalValue;

	public void update() {
		if (this.isMouseOver()) {
			if (this.isVertical && this.isHorizontal) {
				if (this.getInput().isMouseScrolled(true)) {
					if (!this.getInput().isKeyboardDown(Keys.SHIFT_LEFT, true)) {
						this.verticalValue += this.getInput().isKeyboardDown(Keys.CONTROL_LEFT, true) ? this.getVerticalScrollArea() / 10 : this.verticalStep;
					} else {
						this.horizontalValue += this.getInput().isKeyboardDown(Keys.CONTROL_LEFT, true) ? this.getHorizontalScrollArea() / 10 : this.horizontalStep;
					}
				} else if (this.getInput().isMouseScrolled(false)) {
					if (!this.getInput().isKeyboardDown(Keys.SHIFT_LEFT, true)) {
						this.verticalValue -= this.getInput().isKeyboardDown(Keys.CONTROL_LEFT, true) ? this.getVerticalScrollArea() / 10 : this.verticalStep;
					} else {
						this.horizontalValue -= this.getInput().isKeyboardDown(Keys.CONTROL_LEFT, true) ? this.getHorizontalScrollArea() / 10 : this.horizontalStep;
					}
				}
			} else {
				if (this.isVertical) {
					if (this.getInput().isMouseScrolled(true)) this.verticalValue += this.getInput().isKeyboardDown(Keys.SHIFT_LEFT, true) ? this.getVerticalScrollArea() / 10 : this.verticalStep;
					else if (this.getInput().isMouseScrolled(false)) this.verticalValue -= this.getInput().isKeyboardDown(Keys.SHIFT_LEFT, true) ? this.getVerticalScrollArea() / 10 : this.verticalStep;
				} else if (this.isHorizontal) {
					if (this.getInput().isMouseScrolled(true)) this.horizontalValue += this.getInput().isKeyboardDown(Keys.SHIFT_LEFT, true) ? this.getHorizontalScrollArea() / 10 : this.horizontalStep;
					else if (this.getInput().isMouseScrolled(false)) this.horizontalValue -= this.getInput().isKeyboardDown(Keys.SHIFT_LEFT, true) ? this.getHorizontalScrollArea() / 10 : this.horizontalStep;
				}
			}
			this.verticalValue = (int)MathUtils.clamp(this.verticalValue, 0, this.getVerticalScrollArea());
			this.horizontalValue = (int)MathUtils.clamp(this.horizontalValue, 0, this.getHorizontalScrollArea());
		}
	}
	
	public void renderVertical(ShapeDrawer shape, float x, float y, float width, float height) {
		prevColor = shape.getPackedColor();
		shape.setColor(this.isMouseOver() ? UColor.elementBoundsClicked : UColor.elementDefaultColor);
		if (this.getVerticalScrollArea() > 0) {
			float scrollHeight = Math.max(2, height * ((float)this.getVerticalScrollArea() / this.vertScrollArea));
			shape.filledRectangle(x, Interpolation.linear.apply(y + height - scrollHeight, y, (float)this.verticalValue / (float)this.getVerticalScrollArea()), width, scrollHeight);
		} else shape.filledRectangle(x, y, width, height);
		shape.setColor(prevColor);
	}
	
	public void renderHorizontal(ShapeDrawer shape, float x, float y, float width, float height) {
		prevColor = shape.getPackedColor();
		shape.setColor(this.isMouseOver() ? UColor.elementBoundsClicked : UColor.elementDefaultColor);
		if (this.getHorizontalScrollArea() > 0) {
			float scrollWidth = Math.max(2, width * ((float)this.width / (float)this.horizScrollArea));
			shape.filledRectangle(Interpolation.linear.apply(x, x + width - scrollWidth, (float)this.horizontalValue / (float)this.getHorizontalScrollArea()), y, scrollWidth, height);
		} else shape.filledRectangle(x, y, width, height);
		shape.setColor(prevColor);
	}
	
	public int getVerticalValue() {
		return this.verticalValue;
	}
	
	public int getHorizontalValue() {
		return this.horizontalValue;
	}
	
	public float getNormalizedVerticalValue() {
		return this.getVerticalScrollArea() == 0 ? 1 : (float)this.verticalValue / (float)this.getVerticalScrollArea();
	}
	
	public float getNormalizedHorizontalValue() {
		return this.getHorizontalScrollArea() == 0 ? 1 : (float)this.horizontalValue / (float)this.getHorizontalScrollArea();
	}
	
	public STDScrollArea setModes(boolean isVertical, boolean isHorizontal) {
		this.isVertical = isVertical;
		this.isHorizontal = isHorizontal;
		return this;
	}
	
	public STDScrollArea setScrollParameters(float vertScrollArea, float verticalStep, float horizScrollArea, float horizontalStep) {
		this.vertScrollArea = vertScrollArea > 0 ? (int)vertScrollArea : 0;
		this.verticalStep = verticalStep > 0 ? (int)verticalStep : 0;
		this.horizScrollArea = horizScrollArea > 0 ? (int)horizScrollArea : 0;
		this.horizontalStep = horizontalStep > 0 ? (int)horizontalStep : 0;
		this.verticalValue = (int)MathUtils.clamp(this.verticalValue, 0, this.getVerticalScrollArea());
		this.horizontalValue = (int)MathUtils.clamp(this.horizontalValue, 0, this.getHorizontalScrollArea());
		return this;
	}
	
	public STDScrollArea setScrollBounds(float x, float y, float width, float height) {
		this.x = (int)x;
		this.y = (int)y;
		this.width = width > 0 ? (int)width : 0;
		this.height = height > 0 ? (int)height : 0;
		return this;
	}
	
	/** available scroll value for vertical dimension **/
	private float getVerticalScrollArea() {
		return Math.max(0, this.vertScrollArea - this.height);
	}
	
	/** available scroll value for horizontal dimension **/
	private float getHorizontalScrollArea() {
		return Math.max(0, this.horizScrollArea - this.width);
	}
}
