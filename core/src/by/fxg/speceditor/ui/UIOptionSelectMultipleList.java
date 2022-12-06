package by.fxg.speceditor.ui;

import com.badlogic.gdx.Input.Keys;
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

public class UIOptionSelectMultipleList extends UIElement {
	public static final int TYPE_CLICK_INVERT = 0, TYPE_EXPLORER_SELECT = 1;
	protected STDScrollArea scrollArea;
	protected int elementHeight, textAlign = Align.left, textAlignOffset = 5, selectionType = TYPE_CLICK_INVERT, _st1FirstType = 0;
	protected Array<String> options = new Array<>();
	protected Array<Boolean> optionValues = new Array<>();
	
	public UIOptionSelectMultipleList() {
		this.scrollArea = new STDScrollArea().setModes(true, false);
	}
	
	public void update() {
		this.scrollArea.update();
		if (this.isMouseOver(this.x, this.y, this.width - 3, this.height) && this.getInput().isMouseDown(0, false)) {
			int clickIndex = -(GDXUtil.getMouseY() - this.y - this.height + 2 - this.scrollArea.getVerticalValue()) / this.elementHeight;
			if (clickIndex > -1 && clickIndex < this.options.size) {
				switch (this.selectionType) {
					case TYPE_CLICK_INVERT: this.optionValues.set(clickIndex, !this.optionValues.get(clickIndex)); break;
					case TYPE_EXPLORER_SELECT: {
						if (this.getInput().isKeyboardDown(Keys.SHIFT_LEFT, true) && this._st1FirstType > -1) {
							for (int i = 0; i != this.optionValues.size; i++) this.optionValues.set(i, false);
							for (int i = this._st1FirstType; this._st1FirstType > clickIndex ? i >= clickIndex : i <= clickIndex; i = this._st1FirstType > clickIndex ? i - 1 : i + 1) {
								this.optionValues.set(i, true);
							}
						} else if (this.getInput().isKeyboardDown(Keys.CONTROL_LEFT, true)) {
							this.optionValues.set(clickIndex, !this.optionValues.get(clickIndex));
						} else {
							for (int i = 0; i != this.optionValues.size; i++) this.optionValues.set(i, false);
							this.optionValues.set(clickIndex, true);
							this._st1FirstType = clickIndex;
						}
					} break;
				}
				if (this.actionListener != null) {
					this.actionListener.onOptionMultipleListAction(this, this.actionListenerID, clickIndex, this.optionValues.get(clickIndex));
				}
			} else if (this.selectionType == TYPE_EXPLORER_SELECT && !this.getInput().isKeyboardDown(Keys.SHIFT_LEFT, true) && !this.getInput().isKeyboardDown(Keys.CONTROL_LEFT, true)) {
				for (int i = 0; i != this.optionValues.size; i++) this.optionValues.set(i, false);
				this._st1FirstType = -1;
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
				if (this.optionValues.get(i)) {
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
	
	public Array<Boolean> getOptionValues() {
		return this.optionValues;
	}
	
	public boolean getOptionValue(int option) {
		if (option > -1 && option < this.optionValues.size) {
			return this.optionValues.get(option);
		}
		return false;
	}
	
	public Array<String> getOptions() {
		return this.options;
	}
	
	public UIOptionSelectMultipleList setOptionValue(int option, boolean value) {
		if (option > 0 && option < this.optionValues.size) {
			this.optionValues.set(option , value);
		}
		return this;
	}
	
	public UIOptionSelectMultipleList clearOptions() {
		this.options.size = 0;
		this.optionValues.size = 0;
		return this;
	}
	
	public UIOptionSelectMultipleList addOptions(String... options) {
		this.options.addAll(options);
		for (int i = 0; i != options.length; i++) this.optionValues.add(false);
		this.scrollArea.setScrollParameters(this.options.size * this.elementHeight + 2, this.elementHeight, 0, 0);
		return this;
	}
	
	public UIOptionSelectMultipleList removeOption(int option) {
		if (option > -1 && option < this.options.size) {
			this.options.removeIndex(option);
			this.optionValues.removeIndex(option);
		}
		return this;
	}
	
	public UIOptionSelectMultipleList setSelectionType(int mode) {
		this.selectionType = MathUtils.clamp(mode, TYPE_CLICK_INVERT, TYPE_EXPLORER_SELECT);
		return this;
	}
	
	public UIOptionSelectMultipleList setTextAlign(int textAlign) { return this.setTextAlign(textAlign, this.textAlignOffset); }
	public UIOptionSelectMultipleList setTextAlign(int textAlign, int textAlignOffset) {
		this.textAlign = MathUtils.clamp(textAlign, 1, 16);
		this.textAlignOffset = textAlignOffset;
		return this;
	}
	
	public UIOptionSelectMultipleList setTransforms(float x, float y, float width, float height, float elementHeight) {
		this.x = (int)x;
		this.y = (int)y;
		this.width = width > 0 ? (int)width : 0;
		this.height = height > 0 ? (int)height : 0;
		this.elementHeight = elementHeight > 0 ? (int)elementHeight : 0;
		this.scrollArea.setScrollBounds(this.x, this.y, this.width - 3, this.height);
		this.scrollArea.setScrollParameters(this.options.size * this.elementHeight + 2, this.elementHeight, 0, 0);
		return this;
	}
	
	/** @return first positive of all values **/
	public int getFirstPositiveOptionValue() {
		for (int i = 0; i != this.optionValues.size; i++) {
			if (this.optionValues.get(i)) {
				return i;
			}
		}
		return -1;
	}
	
	/** @returns amount of values that is equal to <code>true</code> **/
	public int getOptionPositiveValues() {
		int positive = 0;
		for (int i = 0; i != this.optionValues.size; i++) {
			if (this.optionValues.get(i)) {
				positive++;
			}
		}
		return positive;
	}
	
	/** @returns array of indexes of positive option values **/
	public int[] getOptionPositiveValuesIndices() {
		int[] indices = new int[this.getOptionPositiveValues()];
		for (int index = 0, i = 0; i != this.optionValues.size; i++) {
			if (this.optionValues.get(i)) {
				indices[index] = i;
				index++;
			}
		}
		return indices;
	}
	
	public UIOptionSelectMultipleList setActionListener(ISTDInterfaceActionListener listener, String actionListenerID) { super.setActionListener(listener, actionListenerID); return this; }
}
