package by.fxg.speceditor.std.ui;

import com.badlogic.gdx.utils.Align;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.pilesos.utils.GDXUtil;
import by.fxg.speceditor.SpecEditor;
import by.fxg.speceditor.std.ui.SpecInterface.UColor;
import by.fxg.speceditor.utils.Utils;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class UIElementTooltipped extends UIElement {
	private static int prevMouseX, prevMouseY;
	protected String[] tooltipText = null;
	protected int tooltipX, tooltipY, tooltipWidth, tooltipHeight, mouseTime;
	
	public void updateTooltip() {
		if (this.tooltipText != null && this.isMouseOver()) {
			if (this.mouseTime > 20) {
				SpecInterface.INSTANCE.highlightedTooltipElement = this;
			} else {
				if (prevMouseX == GDXUtil.getMouseX() && prevMouseY == GDXUtil.getMouseY()) {
					if (++this.mouseTime > 20) {
						this.tooltipX = Math.min(GDXUtil.getMouseX(), Utils.getWidth() - this.tooltipWidth);
						this.tooltipY = Math.min(GDXUtil.getMouseY(), Utils.getHeight() - this.tooltipHeight);
					}
				} else {
					prevMouseX = GDXUtil.getMouseX();
					prevMouseY = GDXUtil.getMouseY();
					this.mouseTime = 0;
				}
			}
		} else this.mouseTime = 0;
	}
	
	public void renderTooltip(ShapeDrawer shape, Foster foster) {
		prevColor = shape.getPackedColor();
		shape.setColor(UColor.background);
		shape.filledRectangle(this.tooltipX, this.tooltipY, this.tooltipWidth, this.tooltipHeight);
		shape.setColor(UColor.gray);
		shape.rectangle(this.tooltipX, this.tooltipY, this.tooltipWidth, this.tooltipHeight);
		for (int i = 0; i != this.tooltipText.length; i++) {
			foster.setString(this.tooltipText[i]).draw(this.tooltipX + 3, this.tooltipY + this.tooltipHeight - 2 - (foster.getHeight() + 2) * (i + 1), Align.left);
		}
		shape.setColor(prevColor);
		SpecInterface.INSTANCE.highlightedTooltipElement = null;
	}
	
	public UIElementTooltipped setTooltip(String... tooltipText) { return this.setTooltip(SpecEditor.fosterNoDraw, tooltipText); }
	public UIElementTooltipped setTooltip(Foster foster, String... tooltipText) {
		this.tooltipText = tooltipText;
		this.tooltipWidth = 0;
		for (String line : tooltipText) {
			if (foster.setString(line).getWidth() > this.tooltipWidth) {
				this.tooltipWidth = (int)foster.getWidth();
			}
		}
		this.tooltipWidth += 6; //padding
		this.tooltipHeight = (int)(foster.getHeight() + 2) * tooltipText.length + 7; //size + padding
		return this;
	}
}
