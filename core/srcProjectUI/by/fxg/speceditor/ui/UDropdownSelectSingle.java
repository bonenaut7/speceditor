package by.fxg.speceditor.ui;

import by.fxg.pilesos.graphics.PilesosScissorStack;
import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.pilesos.utils.GDXUtil;
import by.fxg.speceditor.ui.SpecInterface.AppCursor;
import by.fxg.speceditor.ui.SpecInterface.IFocusable;
import by.fxg.speceditor.ui.SpecInterface.UColor;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class UDropdownSelectSingle extends UIElement implements IFocusable {
	protected int dropHeight;
	protected String[] variants;
	protected int selectedVariant;
	
	public UDropdownSelectSingle(int x, int y, int width, int height, int dropHeight, String... variants) { this(0, dropHeight, variants); this.setTransforms(x, y, width, height); }
	public UDropdownSelectSingle(int selectedVariant, int x, int y, int width, int height, int dropHeight, String... variants) { this(selectedVariant, dropHeight, variants); this.setTransforms(x, y, width, height); }
	public UDropdownSelectSingle(int dropHeight, String... variants) { this(0, dropHeight, variants); }
	public UDropdownSelectSingle(int selectedVariant, int dropHeight, String... variants) {
		this.selectedVariant = Math.max(0, Math.min(selectedVariant, variants.length));
		this.dropHeight = dropHeight;
		this.variants = variants;
	}
	
	public void update() {
		if (this.isFocused()) {
			if (this.getInput().isMouseDown(0, false)) {
				if (GDXUtil.isMouseInArea(this.x, this.y - this.dropHeight * this.variants.length - 2, this.width, this.dropHeight * this.variants.length + 2)) {
					int idx = (this.y - GDXUtil.getMouseY() - 2) / this.dropHeight;
					if (idx < this.variants.length && idx > -1) {
						this.setSelectedVariant(idx);
						this.setFocused(false);
					}
				} else this.setFocused(false);
			}
		} else {
			if (this.isMouseOver()) {
				if (this.getInput().isMouseDown(0, false)) this.setFocused(true);
				if (this.getInput().isMouseScrolled(true)) {
					this.setSelectedVariant(this.selectedVariant + 1 >= this.variants.length ? 0 : this.selectedVariant + 1);
				} else if (this.getInput().isMouseScrolled(false)) {
					this.setSelectedVariant(this.selectedVariant - 1 < 0 ? this.variants.length - 1 : this.selectedVariant - 1);
				}
			}
		}
	}
	
	public void render(ShapeDrawer shape, Foster foster) {
		if (this.isDropped()) {
			SpecInterface.setCursor(this.getInput().isMouseDown(0, true) ? AppCursor.POINTING : AppCursor.POINT);
			shape.setColor(UColor.select);
			shape.rectangle(this.x, this.y, this.width, this.height, 2f);
			shape.setColor(UColor.overlay);
			shape.filledRectangle(this.x, this.y, this.width, this.height);
			foster.setString(this.variants[this.selectedVariant]).draw(this.x + this.width / 2, this.y + this.height / 2 + foster.getHalfHeight());
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
				foster.setString(this.variants[i]).draw(this.x + this.width / 2, this.y - this.dropHeight * i - this.dropHeight - 2 + this.dropHeight / 2 + foster.getHalfHeight());
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
				foster.setString(this.variants[this.selectedVariant]).draw(this.x + this.width / 2, this.y + this.height / 2 + foster.getHalfHeight());
				shape.getBatch().flush();
				PilesosScissorStack.instance.popScissors();
			}
		}
	}
	
	public UDropdownSelectSingle setVariants(String... variants) {
		this.variants = variants;
		return this;
	}
	
	public UDropdownSelectSingle setSelectedVariant(int variant) {
		this.selectedVariant = variant;
		return this;
	}
	
	public UDropdownSelectSingle setTransforms(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		return this;
	}
	
	public boolean isDropped() {
		return this.isFocused();
	}
	
	public String[] getVariants() {
		return this.variants;
	}
	
	public int getVariant() {
		return this.selectedVariant;
	}
}
