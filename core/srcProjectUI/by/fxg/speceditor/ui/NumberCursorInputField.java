package by.fxg.speceditor.ui;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Align;

import by.fxg.pilesos.PilesosInputImpl;
import by.fxg.pilesos.graphics.PilesosScissorStack;
import by.fxg.pilesos.utils.GDXUtil;
import by.fxg.speceditor.GInputProcessor;
import by.fxg.speceditor.GInputProcessor.IMouseController;
import by.fxg.speceditor.SpecEditor;
import by.fxg.speceditor.std.ui.STDInputField;
import by.fxg.speceditor.std.ui.SpecInterface.UColor;
import by.fxg.speceditor.utils.Utils;
import space.earlygrey.shapedrawer.ShapeDrawer;

/** Number-based input field **/
public class NumberCursorInputField extends STDInputField implements IMouseController {
	protected Color backgroundColor = null;
	protected float lastValidNumber = 0.0F;
	protected float dragStartNumber, dragX, dragY;
	
	public NumberCursorInputField() {
		this.allowedCharacters = "0123456789-.";
	}
	
	public NumberCursorInputField(Batch batch, BitmapFont font) {
		super(batch, font);
		this.allowedCharacters = "0123456789-.";
	}
	
	public NumberCursorInputField(Parameters parameters) {
		super(parameters); 
		this.allowedCharacters = "0123456789-.";
	}
	
	public void render(Batch batch, ShapeDrawer shape) {
		prevColor = shape.getPackedColor();
		shape.setColor(UColor.gray);
		shape.rectangle(this.x - 1, this.y - 1, this.width + 2, this.height + 2);
		batch.flush();
		if (PilesosScissorStack.instance.peekScissors(this.x, this.y, this.width, this.height)) {
			if (this.backgroundColor != null) {
				shape.setColor(this.backgroundColor);
				shape.filledRectangle(this.x, this.y, this.width, this.height);
			}
			this.foster.setString(this.currentString).draw(this.x - this.xTextOffset, this.y + this.height / 2 - this.foster.getHalfHeight(), Align.left);
			if (this.havePointerSelection()) {
				shape.setColor(0.25F, 0.25F, 1.0F, 0.5F);
				int from = (int)this.foster.setString(this.currentString.substring(0, Math.min(this.selectPointerFrom, this.currentString.length()))).getWidth() - this.xTextOffset;
				int to = (int)this.foster.setString(this.currentString.substring(0, Math.min(this.selectPointerTo, this.currentString.length()))).getWidth() - this.xTextOffset;
				shape.filledRectangle(this.x + from, this.y, to - from, this.height);
			}
			if (this.isFocused()) {
				shape.setColor(1, 1, 1, 1);
				int pointerX = (int)this.foster.setString(this.currentString.substring(0, this.pointer)).getWidth() + 1;
				shape.line(this.x - this.xTextOffset + pointerX, this.y, this.x - this.xTextOffset + pointerX, this.y + this.height);
			}
			batch.flush();
			PilesosScissorStack.instance.popScissors();
		}
		shape.setColor(prevColor);
	}
	
	/** Sets background **/
	public NumberCursorInputField setBackgroundColor(Color color) {
		this.backgroundColor = color;
		return this;
	}
	
	/** Sets current number **/
	public NumberCursorInputField setNumber(float number) {
		this.lastValidNumber = number;
		String text = String.valueOf(number);
		if (text.length() > this.maxTextLength) this.currentString = text.substring(0, this.maxTextLength);
		else this.currentString = text;
		return this;
	}
	
	public STDInputField setTransforms(float x, float y, float width, float height) {
		this.x = (int)x + 1;
		this.y = (int)y + 1;
		this.width = width > 0 ? (int)width - 2 : 0;
		this.height = height > 0 ? (int)height - 2 : 0;
		return this;
	}
	
	/** Override to intercept use of other characters than numbers **/
	public STDInputField setAllowedCharacters(String allowedCharacters) { return this; }
	/** Override to intercept use of other characters than numbers **/
	public STDInputField setText(String text) { this.currentString = text; return this.setNumber(this.getTextAsNumber(this.lastValidNumber)); }

	public void onMouseInput(float x, float y) {
		if (this.isFocused()) {
			this.dragX += this.getInput().isKeyboardDown(Keys.SHIFT_LEFT, true) ? x : this.getInput().isKeyboardDown(Keys.CONTROL_LEFT, true) ? x / 100.0F : x / 10.0F;
			this.dragY += this.getInput().isKeyboardDown(Keys.SHIFT_LEFT, true) ? y : this.getInput().isKeyboardDown(Keys.CONTROL_LEFT, true) ? y / 100.0F : y / 10.0F;
			this.currentString = Utils.dFormat((double)(this.dragStartNumber - this.dragX + this.dragY), 2).replace(",", ".");
			if (this.listener != null) this.listener.onInputFieldTextChanged(this, this.listenerID, this.currentString);
		}
	}
	
	/** Handles input **/
	protected void handleInput(PilesosInputImpl input) {
		this.lastValidNumber = this.getTextAsNumber(this.lastValidNumber);
		
		//exit
		if (input.isKeyboardDown(Keys.ESCAPE, false) || input.isKeyboardDown(Keys.ENTER, false) || input.isMouseDown(0, false) && !this.isMouseOver()) {
			this.selectPointerFrom = this.selectPointerTo = -1;
			this.setFocused(false);
			return;
		}
		
		//switch between fields
		if (input.isKeyboardDown(Keys.TAB, false) && !this.isFocused() && SpecEditor.get.getTick() > LAST_TAB_CLICK_TIME) {
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
				this.dragSelectStart = true;
				this.dragX = GDXUtil.getMouseX();
				this.dragY = GDXUtil.getMouseY();
			}
		}
		
		//drag cursor start
		if (input.isMouseDown(0, true) && this.dragSelectStart && !GDXUtil.isMouseInArea(this.dragX - 4, this.dragY - 4, 8, 8)) {
			this.selectPointerFrom = this.selectPointerTo = this.pointer;
			this.dragSelect = true;
			this.dragSelectStart = false;
			this.dragX = this.dragY = 0.0f;
			this.dragStartNumber = this.lastValidNumber;
			this.moveTextPointer(0);
			this.selectPointerFrom = this.selectPointerTo = 0;
			GInputProcessor.mouseController = this;
			this.getInput().setCursorCatched(true);
		}
		
		//drag cursor
		if (this.dragSelect) {
			if (!input.isMouseDown(0, true)) {
				this.dragSelect = false;
				this.lastValidNumber = this.dragStartNumber - this.dragX + this.dragY;
				this.setFocused(false);
				this.getInput().setCursorCatched(false);
				Gdx.input.setCursorPosition(this.x + this.width / 2, Utils.getHeight() - (this.y + this.height / 2));
				this.moveTextPointer(this.currentString.length());
				this.selectPointerFrom = this.selectPointerTo = this.currentString.length();
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
	
	public static class Builder extends STDInputField.Builder {
		protected Color backgroundColor = null;
		
		public Builder setBackgroundColor(Color color) {
			this.backgroundColor = color;
			return this;
		}
		
		public STDInputField build(STDInputField prevInputField, STDInputField nextInputField) {
			return new NumberCursorInputField(this.parameters).setBackgroundColor(this.backgroundColor).setPreviousField(prevInputField).setNextField(nextInputField);
		}
	}
}
