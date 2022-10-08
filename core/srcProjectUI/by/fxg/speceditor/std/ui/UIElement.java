package by.fxg.speceditor.std.ui;

import by.fxg.pilesos.utils.GDXUtil;
import by.fxg.speceditor.GInputProcessor;
import by.fxg.speceditor.Game;

public abstract class UIElement {
	protected int x, y, width, height;
	
	public boolean isMouseOver(int x, int y, int width, int height) {
		return GDXUtil.isMouseInArea(x, y, width, height) && SpecInterface.isFocused(this);
	}
	
	public boolean isMouseOver() {
		return this.isMouseOver(this.x, this.y, this.width, this.height);
	}
	
	public GInputProcessor getInput() {
		return Game.get.getInput();
	}
}
