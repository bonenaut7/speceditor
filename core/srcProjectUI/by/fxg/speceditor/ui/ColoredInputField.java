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
	private Color backgroundColor = null;
	
	public ColoredInputField() {}
	public ColoredInputField(Batch batch, BitmapFont font) { super(batch, font); }
	public ColoredInputField(Parameters parameters) { super(parameters); }

	public ColoredInputField setBackgroundColor(Color color) {
		this.backgroundColor = color;
		return this;
	}
	
	public void render(Batch batch, ShapeDrawer shape) {
		float prevColor = shape.getPackedColor();
		shape.setColor(UColor.gray);
		shape.rectangle(this.x, this.y - 1, this.width + 1, this.height + 1);
		batch.flush();
		if (PilesosScissorStack.instance.peekScissors(this.x, this.y, this.width, this.height)) {
			if (this.backgroundColor != null) {
				shape.setColor(this.backgroundColor);
				shape.filledRectangle(this.x, this.y, this.width, this.height);
			}
			this.foster.setString(this.currentString).draw(this.x - this.xTextOffset, this.y + this.height / 2 + this.foster.getHeight() / 2, Align.left);
			if (this.havePointerSelection()) {
				shape.setColor(0.25F, 0.25F, 1.0F, 0.5F);
				int from = (int)this.foster.setString(this.currentString.substring(0, this.selectPointerFrom)).getWidth() - this.xTextOffset;
				int to = (int)this.foster.setString(this.currentString.substring(0, this.selectPointerTo)).getWidth() - this.xTextOffset;
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
	
	public STDInputField setTransforms(int x, int y, int width, int height) {
		this.x = x + 1;
		this.y = y + 1;
		this.width = width - 2;
		this.height = height - 2;
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
