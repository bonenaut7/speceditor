package by.fxg.speceditor.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.graphics.PilesosScissorStack;
import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.pilesos.utils.GDXUtil;
import by.fxg.speceditor.std.ui.ISTDInterfaceActionListener;
import by.fxg.speceditor.std.ui.STDScrollArea;
import by.fxg.speceditor.std.ui.SpecInterface.UColor;
import by.fxg.speceditor.std.ui.UIElement;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class UIOptionSelectSingleList extends UIElement {
	protected STDScrollArea scrollArea;
	protected int selectedOption = -1, elementHeight, textAlign = Align.left, textAlignOffset = 5;
	protected Array<String> options = new Array<>();
	
	public UIOptionSelectSingleList() {
		this.scrollArea = new STDScrollArea().setModes(true, false);
	}
	
	public void update() {
		this.scrollArea.update();
		if (this.isMouseOver(this.x, this.y, this.width - 3, this.height) && this.getInput().isMouseDown(0, false)) {
			int clickIndex = -(GDXUtil.getMouseY() - this.y - this.height + 2 - this.scrollArea.getVerticalValue()) / this.elementHeight;
			clickIndex = clickIndex > -1 && clickIndex < this.options.size ? clickIndex : -1;
			this.setSelectedOption(clickIndex);
			if (this.actionListener != null) {
				this.actionListener.onOptionSingleListAction(this, this.actionListenerID, clickIndex);
			}
		}
	}
	
	public void render(Batch batch, ShapeDrawer shape, Foster foster) {
		prevColor = shape.getPackedColor();
		shape.setColor(UColor.background);
		shape.filledRectangle(this.x, this.y, this.width, this.height);
		shape.setColor(UColor.elementDefaultColor);
		shape.rectangle(this.x, this.y, this.width - 3, this.height);	
		batch.flush();
		if (PilesosScissorStack.instance.peekScissors(this.x + 1, this.y + 1, this.width - 5, this.height - 2)) {
			for (int i = 0; i != this.options.size; i++) {
				int y = this.y + this.height - (i * this.elementHeight) - this.elementHeight - 1 + this.scrollArea.getVerticalValue();
				if (this.selectedOption == i) {
					shape.setColor(UColor.elementDefaultColor);
					shape.filledRectangle(this.x, y, this.width - 3, this.elementHeight);
				}
				if (this.isMouseOver(this.x, y, this.width - 3, this.elementHeight)) {
					shape.setColor(UColor.elementHover);
					shape.filledRectangle(this.x, y, this.width - 3, this.elementHeight);
				}
				foster.setString(this.options.get(i)).draw(
					Align.isLeft(this.textAlign) ? this.x + this.textAlignOffset : Align.isCenterHorizontal(this.textAlign) ? this.x + this.width / 2 : this.x + this.width - this.textAlignOffset - 3,
					y + this.elementHeight / 2 - foster.getHalfHeight(), this.textAlign);
			}
			batch.flush();
			PilesosScissorStack.instance.popScissors();
		}
		shape.setColor(prevColor);
		this.scrollArea.renderVertical(shape, this.x + this.width - 3, this.y, 3, this.height);
	}
	
	public int getSelectedOption() {
		return this.selectedOption;
	}
	
	public Array<String> getOptions() {
		return this.options;
	}
	
	public UIOptionSelectSingleList setSelectedOption(int selectedOption) {
		this.selectedOption = MathUtils.clamp(selectedOption, -1, this.options.size);
		return this;
	}
	
	public UIOptionSelectSingleList clearOptions() {
		this.options.size = 0;
		this.selectedOption = -1;
		return this;
	}
	
	public UIOptionSelectSingleList addOptions(String... options) {
		this.options.addAll(options);
		this.scrollArea.setScrollParameters(this.options.size * this.elementHeight + 2, this.elementHeight, 0, 0);
		return this;
	}
	
	public UIOptionSelectSingleList removeOption(int option) {
		if (option > -1 && option < this.options.size) {
			this.options.removeIndex(option);
		}
		return this;
	}
	
	public UIOptionSelectSingleList setTextAlign(int textAlign) { return this.setTextAlign(textAlign, this.textAlignOffset); }
	public UIOptionSelectSingleList setTextAlign(int textAlign, int textAlignOffset) {
		this.textAlign = MathUtils.clamp(textAlign, 1, 16);
		this.textAlignOffset = textAlignOffset;
		return this;
	}
	
	public UIOptionSelectSingleList setTransforms(float x, float y, float width, float height, float elementHeight) {
		this.x = (int)x;
		this.y = (int)y;
		this.width = width > 0 ? (int)width : 0;
		this.height = height > 0 ? (int)height : 0;
		this.elementHeight = elementHeight > 0 ? (int)elementHeight : 0;
		this.scrollArea.setScrollBounds(this.x, this.y, this.width - 3, this.height);
		this.scrollArea.setScrollParameters(this.options.size * this.elementHeight + 2, this.elementHeight, 0, 0);
		return this;
	}
	
	public UIOptionSelectSingleList setActionListener(ISTDInterfaceActionListener listener, String actionListenerID) { super.setActionListener(listener, actionListenerID); return this; }
}
