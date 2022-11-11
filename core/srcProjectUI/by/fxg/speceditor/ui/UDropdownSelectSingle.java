package by.fxg.speceditor.ui;

import by.fxg.pilesos.graphics.PilesosScissorStack;
import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.pilesos.utils.GDXUtil;
import by.fxg.speceditor.std.ui.SpecInterface;
import by.fxg.speceditor.std.ui.SpecInterface.AppCursor;
import by.fxg.speceditor.std.ui.SpecInterface.IFocusable;
import by.fxg.speceditor.std.ui.SpecInterface.UColor;
import by.fxg.speceditor.std.ui.UIElement;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class UDropdownSelectSingle extends UIElement implements IFocusable {
	protected int dropHeight, selectedVariant;
	protected String[] variants;
	
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
				int elementsSize = this.dropHeight * this.variants.length + 2;
				if (this.isMouseOver(this.x, this.y - elementsSize, this.width, elementsSize)) {
					int idx = (this.y - GDXUtil.getMouseY() - 2) / this.dropHeight;
					if (idx < this.variants.length && idx > -1) {
						this.setVariantSelected(idx);
						this.setFocused(false);
					}
				} else this.setFocused(false);
			}
		} else {
			if (this.isMouseOver()) {
				SpecInterface.setCursor(!this.getInput().isMouseDown(0, true) ? AppCursor.POINT : AppCursor.POINTING);
				if (this.getInput().isMouseDown(0, false)) this.setFocused(true);
				if (this.getInput().isMouseScrolled(true)) {
					this.setVariantSelected(this.selectedVariant + 1 >= this.variants.length ? 0 : this.selectedVariant + 1);
				} else if (this.getInput().isMouseScrolled(false)) {
					this.setVariantSelected(this.selectedVariant - 1 < 0 ? this.variants.length - 1 : this.selectedVariant - 1);
				}
			}
		}
	}
	
	public void render(ShapeDrawer shape, Foster foster) {
		prevColor = shape.getPackedColor();
		if (this.isFocused()) {
			shape.setColor(UColor.elementDefaultColor);
			shape.filledRectangle(this.x, this.y, this.width, this.height);
			shape.setColor(UColor.elementIntensiveColor); //needs redesign of colors
			shape.rectangle(this.x, this.y + 1, this.width - 1, this.height - 1, 2f);
			
			int elementsSize = this.dropHeight * this.variants.length;
			shape.getBatch().flush();
			if (PilesosScissorStack.instance.peekScissors(this.x, this.y - elementsSize - 2, this.width, this.height + elementsSize + 2)) {
				foster.setString(this.variants[this.selectedVariant]).draw(this.x + this.width / 2, this.y + this.height / 2 - foster.getHalfHeight());
				shape.setColor(UColor.elementDefaultColor);
				shape.filledRectangle(this.x + 1, this.y - elementsSize - 1, this.width - 2, elementsSize);
				shape.setColor(UColor.elementBoundsClicked);
				shape.rectangle(this.x, this.y - elementsSize - 2, this.width, elementsSize + 2);
				
				int localHeight = 0;
				for (int i = 0; i != this.variants.length; i++) {
					localHeight = this.dropHeight * i + this.height + 1;
					if (this.isMouseOver(this.x, this.y - localHeight, this.width, this.dropHeight)) {
						SpecInterface.setCursor(this.getInput().isMouseDown(0, true) ? AppCursor.POINTING : AppCursor.POINT);
						shape.setColor(UColor.elementHover);
						shape.filledRectangle(this.x + 1, this.y - localHeight, this.width - 2, this.dropHeight);
					}
					foster.setString(this.variants[i]).draw(this.x + this.width / 2, this.y - localHeight + this.dropHeight / 2 - foster.getHalfHeight());
				}
				shape.getBatch().flush();
				PilesosScissorStack.instance.popScissors();
			}
		} else {
			shape.setColor(UColor.elementDefaultColor);
			shape.filledRectangle(this.x, this.y, this.width, this.height);
			if (this.isMouseOver()) {
				shape.setColor(UColor.elementHover);
				shape.filledRectangle(this.x, this.y, this.width, this.height);
			}
			shape.getBatch().flush();
			if (PilesosScissorStack.instance.peekScissors(this.x, this.y, this.width, this.height)) {
				foster.setString(this.variants[this.selectedVariant]).draw(this.x + this.width / 2, this.y + this.height / 2 - foster.getHalfHeight());
				shape.getBatch().flush();
				PilesosScissorStack.instance.popScissors();
			}
		}
		shape.setColor(prevColor);
	}
	
	public int getVariantSelected() { return this.selectedVariant; }
	public UDropdownSelectSingle setVariantSelected(int variant) {
		this.selectedVariant = variant;
		return this;
	}
	
	public String[] getVariants() { return this.variants; }
	public UDropdownSelectSingle setVariants(String... variants) {
		this.variants = variants;
		return this;
	}
	
	public UDropdownSelectSingle setTransforms(float x, float y, float width, float height) {
		this.x = (int)x;
		this.y = (int)y;
		this.width = width > 0 ? (int)width : 0;
		this.height = height > 0 ? (int)height : 0;
		return this;
	}
}
