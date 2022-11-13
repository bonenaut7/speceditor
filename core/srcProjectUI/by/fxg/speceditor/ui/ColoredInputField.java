package by.fxg.speceditor.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Align;

import by.fxg.pilesos.graphics.PilesosScissorStack;
import by.fxg.speceditor.std.ui.STDInputField;
import by.fxg.speceditor.std.ui.SpecInterface.UColor;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class ColoredInputField extends STDInputField {
	protected Color backgroundColor = null;
	
	public ColoredInputField() {}
	public ColoredInputField(Batch batch, BitmapFont font) { super(batch, font); }
	public ColoredInputField(Parameters parameters) { super(parameters); }
	
	public void render(Batch batch, ShapeDrawer shape) {
		prevColor = shape.getPackedColor();
		shape.setColor(UColor.gray);
		shape.rectangle(this.x - 1, this.y - 1, this.width + 2, this.height + 2);
		batch.flush();
		if (PilesosScissorStack.instance.peekScissors(this.x, this.y, this.width, this.height)) {
			if (this.backgroundColor != null) {
				shape.setColor(this.backgroundColor);
				shape.filledRectangle(this.x, this.y, this.width, this.height);
			}
			this.foster.setString(this.currentString).draw(this.x - this.xTextOffset, this.y + this.height / 2 - this.foster.getHalfHeight(), Align.left);
			if (this.havePointerSelection()) {
				shape.setColor(0.25F, 0.25F, 1.0F, 0.5F);
				int from = (int)this.foster.setString(this.currentString.substring(0, Math.min(this.selectPointerFrom, this.currentString.length()))).getWidth() - this.xTextOffset;
				int to = (int)this.foster.setString(this.currentString.substring(0, Math.min(this.selectPointerTo, this.currentString.length()))).getWidth() - this.xTextOffset;
				shape.filledRectangle(this.x + from, this.y, to - from, this.height);
			}
			if (this.isFocused()) {
				shape.setColor(1, 1, 1, 1);
				int pointerX = (int)this.foster.setString(this.currentString.substring(0, this.pointer)).getWidth() + 1;
				shape.line(this.x - this.xTextOffset + pointerX, this.y, this.x - this.xTextOffset + pointerX, this.y + this.height);
			}
			batch.flush();
			PilesosScissorStack.instance.popScissors();
		}
		shape.setColor(prevColor);
	}
	
	/** Sets background **/
	public ColoredInputField setBackgroundColor(Color color) {
		this.backgroundColor = color;
		return this;
	}
	
	public STDInputField setTransforms(int x, int y, int width, int height) {
		this.x = x + 1;
		this.y = y + 1;
		this.width = width - 2;
		this.height = height - 2;
		return this;
	}
	
	public STDInputField setTransforms(float x, float y, float width, float height) {
		this.x = (int)x + 1;
		this.y = (int)y + 1;
		this.width = width > 0 ? (int)width - 2 : 0;
		this.height = height > 0 ? (int)height - 2 : 0;
		return this;
	}
	
	public static class Builder extends STDInputField.Builder {
		protected Color backgroundColor = null;
		
		public Builder setBackgroundColor(Color color) {
			this.backgroundColor = color;
			return this;
		}
		
		public STDInputField build(STDInputField prevInputField, STDInputField nextInputField) {
			return new ColoredInputField(this.parameters).setBackgroundColor(this.backgroundColor).setPreviousField(prevInputField).setNextField(nextInputField);
		}
	}
}
