package by.fxg.speceditor.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Align;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.pilesos.utils.GDXUtil;
import by.fxg.speceditor.Game;
import by.fxg.speceditor.ui.SpecInterface.UColor;
import space.earlygrey.shapedrawer.ShapeDrawer;

public abstract class URenderBlock {
	protected String name;
	protected int x, width, height;
	
	protected boolean isDropped;
	
	public URenderBlock(int x, int sx, int sy) { this("Unnamed", x, sx, sy); }
	public URenderBlock(String name, int x, int sx, int sy) {
		this.name = name;
		this.x = x;
		this.width = sx;
		this.height = sy;
	}

	public int render(int y, Batch batch, ShapeDrawer shape, Foster foster) {
		if (Game.get.getInput().isMouseDown(0, false) && GDXUtil.isMouseInArea(this.x, y - 13, this.width, this.height)) {
			this.isDropped = !this.isDropped;
		}
		
		if (this.isDropped) {
			final int prevY = y;
			y = this.renderInside(y, batch, shape, foster);
			shape.setColor(UColor.gray);
			shape.rectangle(this.x - 5, prevY - 5, this.width + 10, y - prevY - 10);
			
			shape.setColor(UColor.white);
			shape.filledTriangle(this.x - 1, prevY - 1, this.x + 7, prevY - 1, this.x + 3, prevY - 10);
			foster.setString(this.name).draw(this.x + 10, prevY - 1, Align.left);
			shape.line(this.x + foster.getWidth() + 15, prevY - 5, this.x + this.width, prevY - 5, 3F);
			
			y -= 5;
		} else {
			shape.setColor(UColor.white);
			shape.filledTriangle(this.x, y, this.x + 6, y - 5, this.x, y - 10);
			foster.setString(this.name).draw(this.x + 10, y - 1, Align.left);
			shape.line(this.x + foster.getWidth() + 15, y - 5, this.x + this.width, y - 5, 3F);
		}
		return y - this.height;
	}
	
	abstract protected int renderInside(int y, Batch batch, ShapeDrawer shape, Foster foster);
	
	public URenderBlock setDropped(boolean isDropped) {
		this.isDropped = isDropped;
		return this;
	}
	
	public URenderBlock setName(String name) {
		this.name = name;
		return this;
	}
	
	public URenderBlock setTransforms(int x, int width, int height) { 
		this.x = x;
		this.width = width;
		this.height = height;
		return this;
	}
	
	public boolean isDropped() {
		return this.isDropped;
	}
}
