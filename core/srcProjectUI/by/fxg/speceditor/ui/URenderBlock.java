package by.fxg.speceditor.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Align;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.SpecEditor;
import by.fxg.speceditor.std.ui.SpecInterface;
import by.fxg.speceditor.std.ui.SpecInterface.UColor;
import by.fxg.speceditor.std.ui.UIElement;
import space.earlygrey.shapedrawer.ShapeDrawer;

//debug code: float c=shape.getPackedColor();shape.setColor(1,0,0,1);shape.rectangle(x+3,yOffset,1,1);shape.setColor(c);
public abstract class URenderBlock {
	protected String name;
	protected int x, width;
	protected boolean isDropped;
	protected int xBorder, widthBorder;
	
	public URenderBlock(String name, float x, float width) { this(name); this.setTransforms(x, width); }
	public URenderBlock(String name) {
		this.name = name;
	}

	public int render(Batch batch, ShapeDrawer shape, Foster foster, int yOffset) {
		if (SpecEditor.get.getInput().isMouseDown(0, false) && SpecInterface.isFocused(this) && UIElement.isMouseInArea(this.x, yOffset - 10, this.width, 10)) {
			this.isDropped = !this.isDropped;
		}

		UIElement.prevColor = shape.getPackedColor();
		if (this.isDropped) {
			final int prevY = yOffset;
			yOffset = this.renderInside(batch, shape, foster, yOffset - 6) + 6;
			shape.setColor(UColor.elementDefaultColor);
			shape.rectangle(this.xBorder, prevY - 6, this.widthBorder, yOffset - prevY);
			shape.setColor(UColor.elementIntensiveColor);
			shape.filledTriangle(this.xBorder + 3, prevY - 1, this.xBorder + 11, prevY - 1, this.xBorder + 7, prevY - 10);
			foster.setString(this.name).draw(this.xBorder + 15, prevY - 5 - foster.getHeight() / 2, Align.left);
			shape.line(this.xBorder + foster.getWidth() + 20, prevY - 6, this.xBorder + this.widthBorder - 5, prevY - 6, 3F);
			yOffset -= 6;
		} else {
			shape.setColor(UColor.elementIntensiveColor);
			shape.filledTriangle(this.xBorder + 5, yOffset, this.xBorder + 11, yOffset - 5, this.xBorder + 5, yOffset - 10); //5, 6, 10 appropriate sizes
			foster.setString(this.name).draw(this.xBorder + 15, yOffset - 5 - foster.getHeight() / 2, Align.left);
			shape.line(this.xBorder + foster.getWidth() + 20, yOffset - 6, this.xBorder + this.widthBorder - 5, yOffset - 6, 3F);
			yOffset -= 9;
		}
		shape.setColor(UIElement.prevColor);
		return yOffset;
	}
	
	/** you need to return changed yOffset XXX docs **/
	abstract protected int renderInside(Batch batch, ShapeDrawer shape, Foster foster, int yOffset);
	
	public URenderBlock setDropped(boolean isDropped) {
		this.isDropped = isDropped;
		return this;
	}
	
	public URenderBlock setName(String name) {
		this.name = name;
		return this;
	}
	
	public URenderBlock setTransforms(float x, float width) {
		//borders overrides 1px on horizon line, x+1,width-2
		this.x = (int)x + 1;
		this.width = (int)width - 2;
		this.xBorder = (int)x;
		this.widthBorder = (int)width;
		return this;
	}
	
	public boolean isDropped() {
		return this.isDropped;
	}
}
