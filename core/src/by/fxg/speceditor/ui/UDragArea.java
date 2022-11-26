package by.fxg.speceditor.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;

import by.fxg.speceditor.GInputProcessor;
import by.fxg.speceditor.GInputProcessor.IMouseController;
import by.fxg.speceditor.std.ui.ISTDInterfaceActionListener;
import by.fxg.speceditor.std.ui.SpecInterface;
import by.fxg.speceditor.std.ui.SpecInterface.AppCursor;
import by.fxg.speceditor.std.ui.SpecInterface.IFocusable;
import by.fxg.speceditor.std.ui.SpecInterface.UColor;
import by.fxg.speceditor.std.ui.UIElement;
import by.fxg.speceditor.utils.Utils;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class UDragArea extends UIElement implements IFocusable, IMouseController {
	protected int minValue, maxValue;
	protected boolean moveByHeight = false;
	protected int dragStart, drag;
	
	public UDragArea(ISTDInterfaceActionListener actionListener, String actionListenerID) {
		this.setActionListener(actionListener, actionListenerID);
	}
	
	public UDragArea setParameters(int minValue, int maxValue, boolean moveByHeight) {
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.moveByHeight = moveByHeight;
		return this;
	}
	
	public void update() {
		if (this.isFocused()) {
			SpecInterface.setCursor(AppCursor.GRABBING);
			if (!this.getInput().isMouseDown(0, true)) {
				this.drag = MathUtils.clamp(this.drag, this.minValue, this.maxValue);
				this.actionListener.onDragAreaDrag(this, this.actionListenerID, this.dragStart, this.drag, true);
				this.setFocused(false);
				this.getInput().setCursorCatched(false);
				Gdx.input.setCursorPosition(this.x + this.width / 2, Utils.getHeight() - (this.y + this.height / 2));
			}
		} else if (this.isMouseOver()) {
			SpecInterface.setCursor(this.moveByHeight ? AppCursor.RESIZE_VERTICAL : AppCursor.RESIZE_HORIZONTAL);
			if (this.getInput().isMouseDown(0, false)) {
				this.dragStart = this.drag = this.moveByHeight ? this.y : this.x;
				this.setFocused(true);
				GInputProcessor.mouseController = this;
				this.getInput().setCursorCatched(true);
			}
		}
	}
	
	public void render(ShapeDrawer shape) {
		prevColor = shape.getPackedColor();
		shape.setColor(this.isMouseOver() || this.isFocused() ? UColor.elementHover : UColor.elementDefaultColor);
		shape.filledRectangle(this.x, this.y, this.width, this.height);
		shape.setColor(prevColor);
	}

	public UDragArea setTransforms(float x, float y, float width, float height) {
		this.x = (int)x;
		this.y = (int)y;
		this.width = (int)width;
		this.height = (int)height;
		return this;
	}

	public void onMouseInput(float x, float y) {
		if (this.isFocused()) {
			this.drag = MathUtils.clamp(this.drag + (int)(this.moveByHeight ? y : -x), this.minValue, this.maxValue);
			this.actionListener.onDragAreaDrag(this, this.actionListenerID, this.dragStart, this.drag, false);
		}
	}
}
