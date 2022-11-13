package by.fxg.speceditor.ui;

import by.fxg.pilesos.graphics.PilesosScissorStack;
import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.pilesos.utils.GDXUtil;
import by.fxg.speceditor.std.ui.SpecInterface.IFocusable;
import by.fxg.speceditor.std.ui.SpecInterface.UColor;
import by.fxg.speceditor.std.ui.UIElement;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class UDropdownSelectMultiple extends UIElement implements IFocusable {
	protected int dropHeight;
	protected String[] variants;
	protected boolean[] variantValues;
	
	//Local caching
	protected String displayString = "None";
	
	public UDropdownSelectMultiple(float x, float y, float width, float height, float dropHeight, String... variants) { this(dropHeight, variants); this.setTransforms(x, y, width, height); }
	public UDropdownSelectMultiple(float dropHeight, String... variants) {
		this.variantValues = new boolean[variants.length];
		this.dropHeight = dropHeight > 0 ? (int)dropHeight : 0;
		this.variants = variants;
	}
	
	public void update(Foster foster) {
		if (this.isFocused()) {
			if (this.getInput().isMouseDown(0, false)) {
				int elementsSize = this.dropHeight * this.variants.length + 2;
				if (this.isMouseOver(this.x, this.y - elementsSize, this.width, elementsSize)) {
					int idx = (this.y - GDXUtil.getMouseY() - 2) / this.dropHeight;
					if (idx < this.variants.length && idx > -1) {
						this.invertSelected(idx);
						this.updateDisplayString(foster);
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
		prevColor = shape.getPackedColor();
		if (this.isFocused()) {
			shape.setColor(UColor.elementDefaultColor);
			shape.filledRectangle(this.x, this.y, this.width, this.height);
			shape.setColor(UColor.elementIntensiveColor); //needs redesign of colors
			shape.rectangle(this.x, this.y + 1, this.width - 1, this.height - 1, 2f);
			
			int elementsSize = this.dropHeight * this.variants.length;
			shape.getBatch().flush();
			if (PilesosScissorStack.instance.peekScissors(this.x, this.y - elementsSize - 2, this.width, this.height + elementsSize + 2)) {
				foster.setString(this.displayString).draw(this.x + this.width / 2, this.y + this.height / 2 - foster.getHalfHeight());
				shape.setColor(UColor.elementBoundsClicked);
				shape.rectangle(this.x, this.y - elementsSize - 2, this.width, elementsSize + 2);
				int localHeight = 0;
				for (int i = 0; i != this.variants.length; i++) {
					localHeight = this.dropHeight * i + this.dropHeight + 1;
					shape.setColor(this.variantValues[i] ? UColor.greenblack : UColor.elementDefaultColor);
					shape.filledRectangle(this.x + 1, this.y - localHeight, this.width - 2, this.dropHeight);
					if (this.isMouseOver(this.x, this.y - localHeight, this.width, this.dropHeight)) {
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
				foster.setString(this.displayString).draw(this.x + this.width / 2, this.y + this.height / 2 - foster.getHalfHeight());
				shape.getBatch().flush();
				PilesosScissorStack.instance.popScissors();
			}
		}
		shape.setColor(prevColor);
	}
	
	public UDropdownSelectMultiple set(boolean... variants) {
		for (int i = 0; i != Math.min(this.variantValues.length, variants.length); i++) {
			this.variantValues[i] = variants[i];
		}
		return this;
	}
	
	public void invertSelected(int variant) {
		this.variantValues[variant] = !this.variantValues[variant];
	}
	
	public boolean[] getVariantsSelected() { return this.variantValues; }
	public void setVariantSelected(int variant, boolean value) {
		this.variantValues[variant] = value;
	}
	
	public String[] getVariants() { return this.variants; }
	public UDropdownSelectMultiple setVariants(String[] variants, boolean[] values) {
		this.variants = variants;
		this.variantValues = values;
		return this;
	}
	
	public int getDropHeight() {
		return this.variants.length * this.dropHeight + 2;
	}

	public UDropdownSelectMultiple setTransforms(float x, float y, float width, float height) { return this.setTransforms(x, y, width, height, this.dropHeight); }
	public UDropdownSelectMultiple setTransforms(float x, float y, float width, float height, float dropHeight) {
		this.x = (int)x;
		this.y = (int)y;
		this.width = width > 0 ? (int)width : 0;
		this.height = height > 0 ? (int)height : 0;
		this.dropHeight = dropHeight > 0 ? (int)dropHeight : 0;
		return this;
	}
	
	public void updateDisplayString(Foster foster) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i != this.variants.length; i++) {
			if (this.variantValues[i]) sb.append(this.variants[i]).append(", ");
		}
		if (sb.length() == 0) sb.append("None");
		else sb.setLength(sb.length() - 2);
		if (foster.setString(sb.toString()).getWidth() + 5 > this.width) {
			sb.setLength(Math.max(0, (this.width - (int)(foster.setString(" ").getWidth() * 4f)) / (int)foster.getWidth()));
			sb.append("...");
		}
		this.displayString = sb.toString();
	}
}
