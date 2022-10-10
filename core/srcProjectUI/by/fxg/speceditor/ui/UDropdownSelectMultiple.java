package by.fxg.speceditor.ui;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.pilesos.utils.GDXUtil;
import by.fxg.speceditor.std.ui.SpecInterface;
import by.fxg.speceditor.std.ui.SpecInterface.AppCursor;
import by.fxg.speceditor.std.ui.SpecInterface.IFocusable;
import by.fxg.speceditor.std.ui.SpecInterface.UColor;
import by.fxg.speceditor.std.ui.UIElement;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class UDropdownSelectMultiple extends UIElement implements IFocusable {
	private int dropHeight;
	private String[] variants;
	private boolean[] variantValues;
	
	//Local caching
	private String displayString = "None";
	
	public UDropdownSelectMultiple(int x, int y, int width, int height, int dropHeight, String... variants) {
		this.variantValues = new boolean[variants.length];
		this.dropHeight = dropHeight;
		this.variants = variants;
		this.setTransforms(x, y, width, height);
	}
	
	public UDropdownSelectMultiple set(boolean... variants) {
		for (int i = 0; i != Math.min(this.variantValues.length, variants.length); i++) {
			this.variantValues[i] = variants[i];
		}
		return this;
	}
	
	public void update(Foster foster) {
		if (this.isFocused()) {
			if (this.getInput().isMouseDown(0, false)) {
				if (this.isMouseOver(this.x, this.y - this.dropHeight * this.variants.length - 2, this.width, this.dropHeight * this.variants.length + 2)) {
					int idx = (this.y - GDXUtil.getMouseY() - 2) / this.dropHeight;
					if (idx < this.variants.length && idx > -1) {
						this.variantValues[idx] = !this.variantValues[idx];
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
		if (this.isFocused()) {
			if (this.isMouseOver()) SpecInterface.setCursor(this.getInput().isMouseDown(0, true) ? AppCursor.POINTING : AppCursor.POINT);
			shape.setColor(UColor.select);
			shape.rectangle(this.x, this.y, this.width, this.height, 2f);
			shape.setColor(UColor.overlay);
			shape.filledRectangle(this.x, this.y, this.width, this.height);
			foster.setString(this.displayString).draw(this.x + this.width / 2, this.y + this.height / 2 - foster.getHalfHeight());
			for (int i = 0; i != this.variants.length; i++) {
				if (this.variantValues[i]) shape.setColor(UColor.greenblack);
				else shape.setColor(UColor.gray);
				shape.filledRectangle(this.x, this.y - this.dropHeight * i - this.dropHeight - 2, this.width, this.dropHeight);
				if (this.isMouseOver(this.x + 1, this.y - this.dropHeight * i - this.dropHeight - 1, this.width - 2, this.dropHeight - 1)) { //x+1, w-2 | y-1, h-1 to keep it without holes
					SpecInterface.setCursor(this.getInput().isMouseDown(0, true) ? AppCursor.POINTING : AppCursor.POINT);
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
			foster.setString(this.displayString).draw(this.x + this.width / 2, this.y + this.height / 2 - foster.getHalfHeight());
		}
	}
	
	private void updateDisplayString(Foster foster) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i != this.variants.length; i++) {
			if (this.variantValues[i]) sb.append(this.variants[i]).append(", ");
		}
		if (sb.length() == 0) sb.append("None");
		else sb.setLength(sb.length() - 2);
		if (foster.setString(sb.toString()).getWidth() + 4 >= this.width) {
			sb.setLength((this.width - (int)(foster.setString(" ").getWidth() * 4f)) / (int)foster.getWidth());
			sb.append("...");
		}
		this.displayString = sb.toString();
	}
	
	public UDropdownSelectMultiple setVariants(String... variants) {
		this.variants = variants;
		return this;
	}
	
	public UDropdownSelectMultiple setTransforms(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		return this;
	}
	
	public boolean isDropped() {
		return this.isFocused();
	}
	
	public boolean[] getVariantsSelected() {
		return this.variantValues;
	}
	
	public String[] getVariants() {
		return this.variants;
	}
}
