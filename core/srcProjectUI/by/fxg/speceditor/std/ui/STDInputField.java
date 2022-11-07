package by.fxg.speceditor.std.ui;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.PilesosInputImpl;
import by.fxg.pilesos.graphics.PilesosScissorStack;
import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.pilesos.utils.GDXUtil;
import by.fxg.speceditor.SpecEditor;
import by.fxg.speceditor.std.ui.SpecInterface.AppCursor;
import by.fxg.speceditor.std.ui.SpecInterface.IFocusable;
import space.earlygrey.shapedrawer.ShapeDrawer;

/** Some version of text field <br>
 *  This text field have next properties:
 *   <li>Max text length</li>
 *   <li>Allowed Characters</li>
 *   <li>`Fullfocus`, when disabled not focusing through SpecInterface focus, but ignores this value when selecting text by dragging mouse</li>
 *   
 *   <strong>TODO: Add {@link #allowedCharacters} checking for Ctrl+V action in {@link #handleInput(PilesosInputImpl)}.</strong>
 **/
public class STDInputField extends UIElement implements IFocusable {
	protected static long LAST_TAB_CLICK_TIME = 0L;
	protected static final StringBuilder builder = new StringBuilder(4096);
	protected Foster foster = SpecEditor.fosterNoDraw;
	protected int maxTextLength = 256;
	protected String allowedCharacters = null; //null to allow everything
	protected boolean allowToFullfocus = false;
	protected STDInputField prevInputField = null, nextInputField = null;
	protected ISTDInputFieldListener listener;
	protected String listenerID = null;
	
	/** Replacement for fucusing variable if fullfocus disabled **/
	protected boolean _isFocused = false;
	/** text cursor pointer **/
	protected int pointer;
	/** Pointers for text selection **/
	protected int selectPointerFrom = -1, selectPointerTo = -1;
	/** Offset of text to backward direction for rendering. Some type of scroll? **/
	protected int xTextOffset = 0;
	/** Current text **/
	protected String currentString = "";
	
	//long-select
	protected boolean dragSelectStart = false, dragSelect = false;
	protected int dragX = 0;
	
	public STDInputField() {}
	public STDInputField(Batch batch, BitmapFont font) { this.foster = new Foster(font).setBatch(batch); }
	public STDInputField(Parameters parameters) {
		this.foster = parameters.foster;
		this.maxTextLength = parameters.maxTextLength;
		this.allowedCharacters = parameters.allowedCharacters;
		this.allowToFullfocus = parameters.allowToFullfocus;
		this.listener = parameters.listener;
		this.listenerID = parameters.listenerID;
		this.currentString = parameters.currentString == null ? "" : parameters.currentString;
		this.selectPointerFrom = parameters.selectPointerFrom;
		this.selectPointerTo = parameters.selectPointerTo;
		this.setTransforms(parameters.x, parameters.y, parameters.width, parameters.height);
		if (this.foster != null) {
			this.moveTextPointer(parameters.textPointer);
		}
	}
	
	public void update() {
		if (SpecInterface.isFocused(this)) {
			if (this.isMouseOver()) SpecInterface.setCursor(AppCursor.IBEAM);
			if (this.getInput().isMouseDown(0, false) && this.isMouseOver() && !this.isFocused()) this.setFocused(true);
			if (this.isFocused()) {
				this.handleInput(this.getInput());
				if (this.listener != null) {
					this.listener.whileInputFieldFocused(this, this.listenerID);
				}
			} else if (this.listener != null) {
				this.listener.whileInputFieldNotFocused(this, this.listenerID);
			}
		} else if (this.isFocused()) {
			this.setFocused(false);
		} else if (!this.isFocused() && this.listener != null) {
			this.listener.whileInputFieldNotFocused(this, this.listenerID);
		}
	}
	
	public void render(Batch batch, ShapeDrawer shape) {
		batch.flush();
		if (PilesosScissorStack.instance.peekScissors(this.x, this.y, this.width, this.height)) {
			float prevColor = shape.getPackedColor();
			this.foster.setString(this.currentString).draw(this.x - this.xTextOffset, this.y + this.height / 2 - this.foster.getHalfHeight(), Align.left);
			if (this.havePointerSelection()) {
				shape.setColor(0.25F, 0.25F, 1.0F, 0.5F);
				int from = (int)this.foster.setString(this.currentString.substring(0, this.selectPointerFrom)).getWidth() - this.xTextOffset;
				int to = (int)this.foster.setString(this.currentString.substring(0, this.selectPointerTo)).getWidth() - this.xTextOffset;
				shape.filledRectangle(this.x + from, this.y, to - from, this.height);
			}
			if (this.isFocused()) {
				shape.setColor(1, 1, 1, 1);
				int pointerX = (int)this.foster.setString(this.currentString.substring(0, this.pointer)).getWidth() + 1;
				shape.line(this.x - this.xTextOffset + pointerX, this.y, this.x - this.xTextOffset + pointerX, this.y + this.height);
			}
			shape.setColor(prevColor);
			batch.flush();
			PilesosScissorStack.instance.popScissors();
		}
	}
	
	/** Allows full-focusing on the object. To leave focus needs at least 1 click outside of the box or pressing any key that forces focus to leave **/
	public STDInputField setAllowFullfocus(boolean fullfocus) {
		this.allowToFullfocus = fullfocus;
		return this;
	}
	
	/** Sets max text length **/
	public STDInputField setMaxLength(int maxLength) {
		this.maxTextLength = maxLength;
		return this;
	}
	
	/** Sets allowed characters **/
	public STDInputField setAllowedCharacters(String allowedCharacters) {
		this.allowedCharacters = allowedCharacters;
		return this;
	}
	
	/** Sets parameters only for number input **/
	public STDInputField setNumeralInput(boolean floatingNumbers) {
		this.allowedCharacters = floatingNumbers ? "0123456789-." : "0123456789-";
		return this;
	}
	
	/** Sets current text **/
	public STDInputField setText(String text) {
		if (text.length() > this.maxTextLength) this.currentString = text.substring(0, this.maxTextLength);
		else this.currentString = text;
		return this;
	}
	
	/** Moves text pointer **/
	public STDInputField setTextPointer(int pointer) {
		this.moveTextPointer(pointer);
		return this;
	}
	
	/** Sets current text and text pointer **/
	public STDInputField setTextWithPointer(String text) {
		return this.setText(text).setTextPointer(this.currentString.length());
	}
	
	/** Sets current text and text pointer **/
	public STDInputField setTextWithPointer(String text, int pointer) {
		return this.setText(text).setTextPointer(pointer);
	}
	
	/** Sets previous input field for switching between them by keyboard shortcuts **/
	public STDInputField setPreviousField(STDInputField inputField) {
		this.prevInputField = inputField;
		return this;
	}
	
	/** Sets next input field for switching between them by keyboard shortcuts **/
	public STDInputField setNextField(STDInputField inputField) {
		this.nextInputField = inputField;
		return this;
	}
	
	public STDInputField setListener(ISTDInputFieldListener listener, String id) {
		this.listener = listener;
		this.listenerID = id;
		return this;
	}
	
	/** Sets foster for internal calculations and render **/
	public STDInputField setFoster(Foster foster) {
		this.foster = foster;
		return this;
	}
	
	/** Drops offset in case if size not set but you need to set pointer to max **/
	public STDInputField dropOffset() {
		this.xTextOffset = 0;
		return this;
	}
	
	/** Sets transformation of the text field **/
	public STDInputField setTransforms(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		return this;
	}
	
	/** @return current text **/
	public String getText() {
		return this.currentString;
	}
	
	/** @return current text as a number. Returns failValue if not possible **/
	public float getTextAsNumber(float failValue) {
		try {
			return Float.valueOf(this.currentString);
		} catch (NullPointerException | NumberFormatException e) {
			return failValue;
		}
	}

	@Override
	public boolean isMouseOver(int x, int y, int width, int height) {
		return GDXUtil.isMouseInArea(x, y, width, height);
	}
	
	@Override
	public boolean isFocused() {
		return this.allowToFullfocus ? SpecInterface.INSTANCE.currentFocus == this : this._isFocused;
	}
	
	@Override
	public void setFocused(boolean value) {
		if (this.allowToFullfocus) {
			if (value) {
				if (SpecInterface.INSTANCE.currentFocus != null && SpecInterface.INSTANCE.currentFocus != this) SpecInterface.INSTANCE.currentFocus.onFocusRemoved();
				SpecInterface.INSTANCE.currentFocus = this;
				this.onFocusAdded();
			} else if (SpecInterface.INSTANCE.currentFocus == this) { //thinking about everlasting setFocusing(false) from everywhere, so it won't break logic of other objects
				SpecInterface.INSTANCE.currentFocus.onFocusRemoved();
				SpecInterface.INSTANCE.currentFocus = null;
			}
		} else {
			if (this._isFocused = value) this.onFocusAdded();
			else this.onFocusRemoved();
		}
	}
	
	public void onFocusAdded() {
		if (this.listener != null) this.listener.onInputFieldFocusAdded(this, this.listenerID);
	}
	
	public void onFocusRemoved() {
		if (this.listener != null) this.listener.onInputFieldFocusRemoved(this, this.listenerID);
	}
	
	/** Returns true if select pointers are active **/
	protected boolean havePointerSelection() {
		return this.selectPointerFrom > -1;
	}
	
	/** Handles input **/
	protected void handleInput(PilesosInputImpl input) {
		//exit
		if (input.isKeyboardDown(Keys.ESCAPE, false) || input.isKeyboardDown(Keys.ENTER, false) || input.isMouseDown(0, false) && !this.isMouseOver()) {
			this.selectPointerFrom = this.selectPointerTo = -1;
			this.setFocused(false);
			return;
		}
		
		//switch between fields
		if (input.isKeyboardDown(Keys.TAB, false) && SpecEditor.get.getTick() > LAST_TAB_CLICK_TIME) {
			if (input.isKeyboardDown(Keys.CONTROL_LEFT, true)) {
				if (this.prevInputField != null) {
					this.setFocused(false);
					this.prevInputField.setFocused(true);
				}
			} else if (this.nextInputField != null) {
				this.setFocused(false);
				this.nextInputField.setFocused(true);
			}
			LAST_TAB_CLICK_TIME = SpecEditor.get.getTick();
		}
		
		//set cursor
		if (input.isMouseDown(0, false) && this.isMouseOver()) {
			if (this.currentString.length() > 0) {
				this.selectPointerFrom = this.selectPointerTo = -1;
				this.moveTextPointer(this.getPointerPosition(this.foster, GDXUtil.getMouseX() - this.x));
				if (this.currentString.length() > 0) {
					this.dragSelectStart = true;
					this.dragX = GDXUtil.getMouseX();
				}
			}
		}
		
		//drag cursor start
		if (input.isMouseDown(0, true) && this.dragSelectStart && (GDXUtil.getMouseX() - this.dragX > 4 || GDXUtil.getMouseX() - this.dragX < -4)) {
			this.selectPointerFrom = this.selectPointerTo = this.pointer;
			this.dragSelect = true;
			this.dragSelectStart = false;
			this.dragX = (int)this.foster.setString(this.currentString.substring(0, this.pointer)).getWidth();
		}
		
		//drag cursor
		if (this.dragSelect) {
			int mouseX = GDXUtil.getMouseX();
			this.selectPointerTo = this.getPointerPosition(this.foster, mouseX - this.x);
			if (mouseX < this.x && this.xTextOffset > 0) {
				this.xTextOffset = MathUtils.clamp(this.xTextOffset - (int)this.foster.setString("@").getWidth(), 0, (int)this.foster.setString(this.currentString).getWidth() - this.width);
			} else if (mouseX > this.x + this.width && this.xTextOffset < (int)this.foster.setString(this.currentString).getWidth() - this.width) {
				this.xTextOffset = MathUtils.clamp(this.xTextOffset + (int)this.foster.setString("@").getWidth(), 0, (int)this.foster.setString(this.currentString).getWidth() - this.width);
			}
			
			if (!input.isMouseDown(0, true)) {
				this.dragSelect = false;
				this.moveTextPointer(this.selectPointerTo);
				if (this.selectPointerFrom > this.selectPointerTo) {
					int smallPointer = this.selectPointerTo;
					this.selectPointerTo = this.selectPointerFrom;
					this.selectPointerFrom = smallPointer;
				}
			}
		}
		
		//remove character
		if (input.isKeyboardDown(Keys.BACKSPACE, false) || input.isKeyboardDown(Keys.BACKSPACE, true) && input.getClickedKeyTime(Keys.BACKSPACE) > 30 && SpecEditor.get.getTick() % 2 == 0) {
			if (this.havePointerSelection()) {
				builder.append(this.currentString.substring(0, this.selectPointerFrom)).append(this.currentString.substring(this.selectPointerTo, this.currentString.length()));
				this.currentString = builder.toString();
				builder.setLength(0);
				this.moveTextPointer(this.selectPointerFrom);
				this.selectPointerFrom = this.selectPointerTo = -1;
			} else if (this.pointer > 0) {
				builder.append(this.currentString.substring(0, this.pointer - 1)).append(this.currentString.substring(this.pointer, this.currentString.length()));
				this.currentString = builder.toString();
				builder.setLength(0);
				this.moveTextPointer(this.pointer - 1);
			}
			if (this.listener != null) {
				this.listener.onInputFieldTextChanged(this, this.listenerID, null);
			}
		}
		
		//move pointer
		boolean movePointerLeft = input.isKeyboardDown(Keys.LEFT, false) || input.isKeyboardDown(Keys.LEFT, true) && input.getClickedKeyTime(Keys.LEFT) > 30 && SpecEditor.get.getTick() % 2 == 0;
		boolean movePointerRight = input.isKeyboardDown(Keys.RIGHT, false) || input.isKeyboardDown(Keys.RIGHT, true) && input.getClickedKeyTime(Keys.RIGHT) > 30 && SpecEditor.get.getTick() % 2 == 0;
		if (movePointerLeft || movePointerRight) {
			if (this.havePointerSelection()) {
				this.moveTextPointer(movePointerLeft ? this.selectPointerFrom : this.selectPointerTo);
				this.selectPointerFrom = this.selectPointerTo = -1;
			} else if (movePointerLeft && this.pointer > 0) {
				this.moveTextPointer(this.pointer - 1);
			} else if (movePointerRight && this.pointer < this.currentString.length()) {
				this.moveTextPointer(this.pointer + 1);
			}
		}
		
		//control keys
		if (input.isKeyboardDown(Keys.CONTROL_LEFT, true)) {
			//All-selection
			if (input.isKeyboardDown(Keys.A, false)) {
				this.selectPointerFrom = 0;
				this.selectPointerTo = this.currentString.length();
			}
			
			//Cut & Copy
			if ((input.isKeyboardDown(Keys.X, false) || input.isKeyboardDown(Keys.C, false)) && this.havePointerSelection()) {
				StringSelection selection = new StringSelection(this.currentString.substring(this.selectPointerFrom, this.selectPointerTo));
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
				if (input.isKeyboardDown(Keys.X, false)) {
					builder.append(this.currentString.substring(0, this.selectPointerFrom)).append(this.currentString.substring(this.selectPointerTo, this.currentString.length()));
					this.currentString = builder.toString();
					builder.setLength(0);
					this.moveTextPointer(this.selectPointerFrom);
					this.selectPointerFrom = this.selectPointerTo = -1;
					if (this.listener != null) {
						this.listener.onInputFieldTextChanged(this, this.listenerID, null);
					}
				}
			}
			
			//Paste
			if (input.isKeyboardDown(Keys.V, false) && (this.currentString.length() < this.maxTextLength || this.havePointerSelection())) {
				try {
					Object obj = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this).getTransferData(DataFlavor.stringFlavor);
					if (obj instanceof String) {
						if (this.havePointerSelection()) {
							builder.append(this.currentString.substring(0, this.selectPointerFrom)).append(this.currentString.substring(this.selectPointerTo, this.currentString.length()));
							this.currentString = builder.toString();
							builder.setLength(0);
							this.pointer = this.selectPointerFrom;
							this.selectPointerFrom = this.selectPointerTo = -1;
						}
						String objString = ((String)obj);
						if (this.currentString.length() + objString.length() > this.maxTextLength) {
							objString = ((String)obj).substring(0, this.maxTextLength - this.currentString.length());
						}
						builder.append(this.currentString.substring(0, this.pointer));
						builder.append(objString);
						builder.append(this.currentString.substring(this.pointer, this.currentString.length()));
						this.currentString = builder.toString();
						this.moveTextPointer(this.pointer + objString.length());
						builder.setLength(0);
						if (this.listener != null) {
							this.listener.onInputFieldTextChanged(this, this.listenerID, objString);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		//text input
		if (input.getCharTypedLast() != null) {
			if (this.currentString.length() < this.maxTextLength || this.havePointerSelection()) {
				if (this.allowedCharacters == null || this.allowedCharacters.contains(input.getCharTypedLast())) {
					if (this.havePointerSelection()) {
						builder.append(this.currentString.substring(0, this.selectPointerFrom)).append(this.currentString.substring(this.selectPointerTo, this.currentString.length()));
						this.currentString = builder.toString();
						builder.setLength(0);
						this.pointer = this.selectPointerFrom;
						this.selectPointerFrom = this.selectPointerTo = -1;	
					}
					builder.append(this.currentString.substring(0, this.pointer));
					builder.append(input.getCharTypedLast());
					builder.append(this.currentString.substring(this.pointer, this.currentString.length()));
					this.currentString = builder.toString();
					builder.setLength(0);
					this.moveTextPointer(this.pointer + 1);
					if (this.listener != null) {
						this.listener.onInputFieldTextChanged(this, this.listenerID, input.getCharTypedLast());
					}
				}
			}
		}
	}

	//Be careful using this method, looks dangerous to me
	protected int getPointerPosition(Foster foster, int fieldX) {
		int prevWidth = 0, width = (int)foster.setString(this.currentString).getWidth() - this.xTextOffset, halfPart;
		//if (fieldX < 1) return 0; else //checking for 0 position || removed because can cause errors if xTextOffset is not zero
		if (width < fieldX) return this.currentString.length(); //checking max length
		for (int i = 1; i != this.currentString.length(); i++) {
			width = MathUtils.ceil(foster.setString(this.currentString.substring(0, i)).getWidth()) - this.xTextOffset;
			/** Split3 method, splits difference by 3 and looks only backwards. Less correct than active variant, but faster
			halfPart = (width - prevWidth) / 3;
			if (width - halfPart > fieldX) {
				return i -1;
			}
			prevWidth = width;
			*/
			halfPart = (width - prevWidth) / 2;
			if (width - halfPart <= fieldX && fieldX <= width + halfPart) {
				return i;
			}
			if (width-halfPart > fieldX) return i-1;
			prevWidth = width;
		}
		if (width < fieldX) return this.currentString.length();
		return 0;
	}
	
	/** Moves pointer to character position of {@link #currentString}, resets {@link #xTextOffset}. **/
	protected void moveTextPointer(int pointer) {
		this.pointer = MathUtils.clamp(pointer, 0, this.currentString.length());

		int pointerX = (int)this.foster.setString(this.currentString.substring(0, this.pointer)).getWidth();
		int totalLength = (int)this.foster.setString(this.currentString).getWidth();
		if (pointerX - this.xTextOffset > this.width) this.xTextOffset = pointerX - this.width;
		else if (pointerX < this.xTextOffset) this.xTextOffset = pointerX;
		else if (totalLength > this.width && this.xTextOffset > totalLength - this.width) this.xTextOffset = totalLength - this.width;
	}
	
	public static class Parameters {
		public Foster foster = SpecEditor.fosterNoDraw;
		public ISTDInputFieldListener listener = null;
		public String allowedCharacters = null, listenerID = null, currentString = "";
		public boolean allowToFullfocus = true;
		public int maxTextLength = 32, textPointer = 0, selectPointerFrom = -1, selectPointerTo = -1, x, y, width, height;
	}
	
	/** Builder for {@link #STDInputField()} **/
	public static class Builder {
		/** Linker array, allows to link all {@link #STDInputField()} together to allow CTRL+TAB and TAB **/
		protected Array<STDInputField> fieldsToLink = new Array<>();
		/** Basic parameters of STDInputField **/
		protected Parameters parameters = new Parameters();
		
		/** Basic parameters of STDInputField **/
		public Parameters getParameters() {
			return this.parameters;
		}
		
		/** Allows full-focusing on the object. To leave focus needs at least 1 click outside of the box or pressing any key that forces focus to leave **/
		public Builder setAllowFullfocus(boolean fullfocus) {
			this.parameters.allowToFullfocus = fullfocus;
			return this;
		}
		
		/** Sets max text length **/
		public Builder setMaxLength(int maxLength) {
			this.parameters.maxTextLength = maxLength;
			return this;
		}
		
		/** Sets allowed characters **/
		public Builder setAllowedCharacters(String allowedCharacters) {
			this.parameters.allowedCharacters = allowedCharacters;
			return this;
		}
		
		/** Sets parameters only for number input **/
		public Builder setNumeralInput(boolean floatingNumbers) {
			this.parameters.allowedCharacters = floatingNumbers ? "0123456789-." : "0123456789-";
			return this;
		}
		
		/** Sets current text **/
		public Builder setText(String text) {
			if (text.length() > this.parameters.maxTextLength) this.parameters.currentString = text.substring(0, this.parameters.maxTextLength);
			else this.parameters.currentString = text;
			return this;
		}
		
		/** Moves text pointer, needs {@link #foster} to be set **/
		public Builder setTextPointer(int pointer) {
			this.parameters.textPointer = MathUtils.clamp(pointer, 0, this.parameters.currentString.length());
			return this;
		}
		
		/** Sets current text and text pointer, needs {@link #foster} to be set **/
		public Builder setTextWithPointer(String text) {
			return this.setText(text).setTextPointer(this.parameters.currentString.length());
		}
		
		/** Sets current text and text pointer, needs {@link #foster} to be set **/
		public Builder setTextWithPointer(String text, int pointer) {
			return this.setText(text).setTextPointer(pointer);
		}
		
		/** Sets selection pointers, needs {@link #foster} to be set **/
		public Builder setTextSelectPointers(int from, int to) {
			this.parameters.selectPointerFrom = from > to ? from : to;
			this.parameters.selectPointerTo = from > to ? to : from;
			return this;
		}
		
		public Builder setListener(ISTDInputFieldListener listener, String id) {
			this.parameters.listener = listener;
			this.parameters.listenerID = id;
			return this;
		}
		
		/** Sets foster for internal calculations and render **/
		public Builder setFoster(Foster foster) {
			this.parameters.foster = foster;
			return this;
		}
		
		/** Sets transformation of the text field **/
		public Builder setTransforms(int x, int y, int width, int height) {
			this.parameters.x = x;
			this.parameters.y = y;
			this.parameters.width = width;
			this.parameters.height = height;
			return this;
		}
		
		/** Builds new {@link #STDInputField()} with specified parameters.
		 *  @return {@link #STDInputField()} **/
		public STDInputField build() {
			return this.build(null, null);
		}
		
		/** Builds new {@link #STDInputField()} with specified parameters.
		 *  @param prevInputField - Previous input field to switch with CTRL+TAB
		 *  @param nextInputField - Next input field to switch with TAB
		 *  @return {@link #STDInputField()} **/
		public STDInputField build(STDInputField prevInputField, STDInputField nextInputField) {
			return new STDInputField(this.parameters).setPreviousField(prevInputField).setNextField(nextInputField);
		}
		
		/** Adds specified fields to the linking array. **/
		public Builder addToLink(STDInputField... fields) {
			this.fieldsToLink.addAll(fields);
			return this;
		}
		
		/** Links all fields added through {@link #addToLink(STDInputField...)} together <br>
		 *  This method sets {@link STDInputField#prevInputField} & {@link STDInputField#nextInputField}
		 *   to allow switch between {@link #STDInputField()} using CTRL+TAB and TAB keys **/
		public Builder linkFields() {
			for (int i = 0; i != this.fieldsToLink.size; i++) {
				this.fieldsToLink.get(i).setPreviousField(i == 0 ? null : this.fieldsToLink.get(i - 1)).setNextField(i + 1 == this.fieldsToLink.size ? null : this.fieldsToLink.get(i + 1));
			}
			this.fieldsToLink.size = 0;
			return this;
		}
	}
}
