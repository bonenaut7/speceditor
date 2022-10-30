package by.fxg.speceditor.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Align;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.pilesos.utils.GDXUtil;
import by.fxg.speceditor.SpecEditor;
import by.fxg.speceditor.std.ui.SpecInterface;
import by.fxg.speceditor.std.ui.SpecInterface.UColor;
import space.earlygrey.shapedrawer.ShapeDrawer;

//debug code: float c=shape.getPackedColor();shape.setColor(1,0,0,1);shape.rectangle(x+3,yOffset,1,1);shape.setColor(c);
public abstract class URenderBlock {
	protected String name;
	protected int x, width;
	protected boolean isDropped;
	
	public URenderBlock(String name) { this(name, 0, 0); }
	public URenderBlock(String name, int x, int width) {
		this.name = name;
		this.x = x;
		this.width = width;
	}

	public int render(Batch batch, ShapeDrawer shape, Foster foster, int yOffset) {
		if (SpecInterface.isFocused(this) && SpecEditor.get.getInput().isMouseDown(0, false) && GDXUtil.isMouseInArea(this.x, yOffset - 13, this.width, 13)) {
			this.isDropped = !this.isDropped;
		}
		
		if (this.isDropped) {
			final int prevY = yOffset;
			yOffset = this.renderInside(batch, shape, foster, yOffset - 14) + 10;
			shape.setColor(UColor.gray);
			shape.rectangle(this.x - 5, prevY - 5, this.width + 10, yOffset - prevY - 10);
			
			shape.setColor(UColor.white);
			shape.filledTriangle(this.x - 1, prevY - 1, this.x + 7, prevY - 1, this.x + 3, prevY - 10);
			foster.setString(this.name).draw(this.x + 10, prevY - foster.getHeight() - 1, Align.left);
			shape.line(this.x + foster.getWidth() + 15, prevY - 5, this.x + this.width, prevY - 5, 3F);
		} else {
			shape.setColor(UColor.white);
			shape.filledTriangle(this.x, yOffset, this.x + 6, yOffset - 5, this.x, yOffset - 10);
			foster.setString(this.name).draw(this.x + 10, yOffset - foster.getHeight() - 1, Align.left);
			shape.line(this.x + foster.getWidth() + 15, yOffset - 5, this.x + this.width, yOffset - 5, 3F);
		}
		return yOffset -= 17;
	}
	
	/** you need to return changed yOffset XXX**/
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
		this.x = (int)x;
		this.width = (int)width;
		return this;
	}
	
	public boolean isDropped() {
		return this.isDropped;
	}
}
