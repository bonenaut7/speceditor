package by.fxg.speceditor.ui;

import by.fxg.pilesos.graphics.PilesosScissorStack;
import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.pilesos.utils.GDXUtil;
import by.fxg.speceditor.std.ui.SpecInterface;
import by.fxg.speceditor.std.ui.UIElement;
import by.fxg.speceditor.std.ui.SpecInterface.AppCursor;
import by.fxg.speceditor.std.ui.SpecInterface.IFocusable;
import by.fxg.speceditor.std.ui.SpecInterface.UColor;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class UDropdownClick extends UIElement implements IFocusable {
	private int dropHeight;
	private String name;
	private String[] variants;
	private int selectedVariant;
	
	public UDropdownClick(String name, int x, int y, int width, int height, int dsy, String... variants) { this(name, dsy, variants); this.setTransforms(x, y, width, height); }
	public UDropdownClick(String name, int dsy, String... variants) {
		this.selectedVariant = -1;
		this.name = name;
		this.dropHeight = dsy;
		this.variants = variants;
	}
	
	public void update() {
		this.selectedVariant = -1;
		if (this.isFocused()) {
			if (this.getInput().isMouseDown(0, false)) {
				if (GDXUtil.isMouseInArea(this.x, this.y - this.dropHeight * this.variants.length - 2, this.width, this.dropHeight * this.variants.length + 2)) {
					int idx = (this.y - GDXUtil.getMouseY() - 2) / this.dropHeight;
					if (idx < this.variants.length && idx > -1) {
						this.selectedVariant = idx;
						this.setFocused(false);
					}
				} else this.setFocused(false);
			}
		} else {
			if (this.getInput().isMouseDown(0, false) && this.isMouseOver()) {
				this.setFocused(true);
			}
		}
	}
	
	public void render(ShapeDrawer shape, Foster foster) {
		if (this.isFocused()) {
			if (this.isMouseOver()) SpecInterface.setCursor(this.getInput().isMouseDown(0, true) ? AppCursor.POINTING : AppCursor.POINT);
			shape.setColor(UColor.select);
			shape.rectangle(this.x, this.y, this.width, this.height, 2f);
			shape.setColor(UColor.overlay);
			shape.filledRectangle(this.x, this.y, this.width, this.height);
			foster.setString(this.name).draw(this.x + this.width / 2, this.y + this.height / 2 - foster.getHalfHeight());
			for (int i = 0; i != this.variants.length; i++) {
				shape.setColor(UColor.gray);
				shape.filledRectangle(this.x, this.y - this.dropHeight * i - this.dropHeight - 2, this.width, this.dropHeight);
				if (this.isMouseOver(this.x + 1, this.y - this.dropHeight * i - this.dropHeight - 1, this.width - 2, this.dropHeight - 1)) { //x+1, w-2 | y-1, h-1 to keep it without holes
					SpecInterface.setCursor(AppCursor.POINT);
					shape.setColor(UColor.suboverlay);
					shape.filledRectangle(this.x, this.y - this.dropHeight * i - this.dropHeight - 2, this.width, this.dropHeight);
				}
				shape.setColor(UColor.overlay);
				shape.rectangle(this.x, this.y - this.dropHeight * i - this.dropHeight - 2, this.width, this.dropHeight);
				foster.setString(this.variants[i]).draw(this.x + this.width / 2, this.y - this.dropHeight * i - this.dropHeight - 2 + this.dropHeight / 2 - foster.getHalfHeight());
			}
		} else {
			shape.setColor(UColor.gray);
			shape.filledRectangle(this.x, this.y, this.width, this.height);
			if (this.isMouseOver()) {
				SpecInterface.setCursor(this.getInput().isMouseDown(0, true) ? AppCursor.POINTING : AppCursor.POINT);
				shape.setColor(UColor.overlay);
				shape.filledRectangle(this.x, this.y, this.width, this.height);
			}
			shape.getBatch().flush();
			if (PilesosScissorStack.instance.peekScissors(this.x, this.y, this.width, this.height)) {
				foster.setString(this.name).draw(this.x + this.width / 2, this.y + this.height / 2 - foster.getHalfHeight());
				shape.getBatch().flush();
				PilesosScissorStack.instance.popScissors();
			}
		}
	}
	
	public UDropdownClick setVariants(String... variants) {
		this.variants = variants;
		return this;
	}
	
	public UDropdownClick setTransforms(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		return this;
	}
	
	public boolean isDropped() {
		return this.isFocused();
	}
	
	public boolean isPressed() {
		return this.selectedVariant != -1;
	}
	
	public int getVariant() {
		return this.selectedVariant;
	}
}
