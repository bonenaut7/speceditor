package by.fxg.speceditor.ui;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Align;

import by.fxg.pilesos.PilesosInputImpl;
import by.fxg.pilesos.graphics.PilesosScissorStack;
import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.pilesos.utils.GDXUtil;
import by.fxg.speceditor.Game;
import by.fxg.speceditor.ui.SpecInterface.AppCursor;
import by.fxg.speceditor.ui.SpecInterface.IFocusable;
import space.earlygrey.shapedrawer.ShapeDrawer;

/** Latest version of text field <br>
 *  This text field have next properties:
 *   <li>Max text length</li>
 *   <li>Allowed Characters</li>
 *   <li>`Fullfocus`, when disabled not focusing through SpecInterface focus, but ignores this value when selecting text by dragging mouse</li>
 **/
public class STDInputField extends UIElement implements IFocusable {
	private StringBuilder builder = new StringBuilder();
	private Foster foster = null;
	private int paramMaxTextLength = 256;
	private String paramAllowedCharacters = null; //null to allow everything
	private boolean allowToFullfocus = true;
	
	/** Replacement for fucusing variable if fullfocus disabled **/
	private boolean _isFocused = false;
	/** text cursor pointer **/
	private int pointer;
	/** Pointers for text selection **/
	private int selectPointerFrom = -1, selectPointerTo = -1;
	/** Offset of text to backward direction for rendering. Some type of scroll? **/
	private int xTextOffset = 0;
	/** Current text **/
	private String currentString = "";
	
	//long-select
	private boolean dragSelectStart = false, dragSelect = false;
	private int dragX = 0;
	
	public STDInputField(Batch batch, BitmapFont font) { this(new Foster(font).setBatch(batch)); }
	public STDInputField(Foster foster) {
		this.foster = foster;
	}
	
	public void update() {
		if (SpecInterface.isFocused(this)) {
			if (this.isMouseOver()) SpecInterface.setCursor(AppCursor.IBEAM);
			if (this.getInput().isMouseDown(0, false) && this.isMouseOver() && !this.isFocused()) this.setFocused(true);
			if (this.isFocused()) this.handleKeys(this.getInput());
		} else if (this.isFocused()) {
			this.setFocused(false);
		}
	}
	
	public void render(Batch batch, ShapeDrawer shape) {
		batch.flush();
		if (PilesosScissorStack.instance.peekScissors(this.x, this.y, this.width, this.height)) {
			float prevColor = shape.getPackedColor();
			this.foster.setString(this.currentString).draw(this.x - this.xTextOffset, this.y + this.height / 2 + this.foster.getHeight() / 2, Align.left);
			if (this.havePointerSelection()) {
				shape.setColor(0.25F, 0.25F, 1.0F, 0.5F);
				int from = (int)this.foster.setString(this.currentString.substring(0, this.selectPointerFrom)).getWidth() - this.xTextOffset;
				int to = (int)this.foster.setString(this.currentString.substring(0, this.selectPointerTo)).getWidth() - this.xTextOffset;
				shape.filledRectangle(this.x + from, this.y, to - from, this.height);
			}
			shape.setColor(1, 1, 1, 1);
			int pointerX = (int)this.foster.setString(this.currentString.substring(0, this.pointer)).getWidth() + 1;
			shape.line(this.x - this.xTextOffset + pointerX, this.y, this.x - this.xTextOffset + pointerX, this.y + this.height);
			shape.setColor(prevColor);
			batch.flush();
			PilesosScissorStack.instance.popScissors();
		}

		if (Game.DEBUG) {
			int ls = 6;
			float prevColor0 = shape.getPackedColor();
			shape.setColor(1, this.isFocused() ? 1 : 0, 0, 1);
			shape.rectangle(this.x, this.y, this.width, this.height);
			shape.setColor(prevColor0);
		}
	}
	
	/** Allows full-focusing on the object. To leave focus needs at least 1 click outside of the box or pressing any key that forces focus to leave **/
	public STDInputField setAllowFullfocus(boolean fullfocus) {
		this.allowToFullfocus = fullfocus;
		return this;
	}
	
	/** Sets max text length **/
	public STDInputField setMaxTextLength(int maxLength) {
		this.paramMaxTextLength = maxLength;
		return this;
	}
	
	/** Sets allowed characters **/
	public STDInputField setAllowedCharacters(String allowedCharacters) {
		this.paramAllowedCharacters = allowedCharacters;
		return this;
	}
	
	/** Sets parameters only for number input **/
	public STDInputField setNumeralInput(boolean floatingNumbers) {
		this.builder.append("0123456789-");
		if (floatingNumbers) this.builder.append(".");
		this.builder.trimToSize();
		this.paramAllowedCharacters = this.builder.toString();
		this.builder.setLength(0);
		return this;
	}
	
	/** Sets current text, pointer to zero **/
	public STDInputField setText(String text) {
		this.currentString = text;
		this.movePointer(0);
		return this;
	}
	
	/** Sets current text and pointer **/
	public STDInputField setTextWithPointer(String text) {
		this.currentString = text;
		this.movePointer(text.length());
		return this;
	}
	
	/** Sets current text and pointer **/
	public STDInputField setTextWithPointer(String text, int pointer) {
		this.currentString = text;
		this.movePointer(pointer);
		return this;
	}
	
	/** Sets foster **/
	public STDInputField setFoster(Foster foster) {
		this.foster = foster;
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
	
	/** @return current text as a number. Returns safeValue if not possible **/
	public float getTextAsNumber(float safeValue) {
		try {
			return Float.valueOf(this.currentString);
		} catch (Exception e) {
			return safeValue;
		}
	}

	public boolean isMouseOver(int x, int y, int width, int height) {
		return GDXUtil.isMouseInArea(x, y, width, height);
	}
	
	public boolean isFocused() {
		return this.allowToFullfocus ? SpecInterface.get.currentFocus == this : this._isFocused;
	}
	
	public void setFocused(boolean value) {
		if (this.allowToFullfocus) {
			if (value) {
				if (SpecInterface.get.currentFocus != null && SpecInterface.get.currentFocus != this) SpecInterface.get.currentFocus.onFocusRemoved();
				SpecInterface.get.currentFocus = this;
				this.onFocusAdded();
			} else if (SpecInterface.get.currentFocus == this) { //thinking about everlasting setFocusing(false) from everywhere, so it won't break logic of other objects
				SpecInterface.get.currentFocus.onFocusRemoved();
				SpecInterface.get.currentFocus = null;
			}
		} else {
			if (this._isFocused = value) this.onFocusAdded();
			else this.onFocusRemoved();
		}
	}
	
	protected boolean havePointerSelection() { return this.selectPointerFrom > -1; }
	
	protected void handleKeys(PilesosInputImpl input) {
		//exit
		if (input.isKeyboardDown(Keys.ESCAPE, false) || input.isKeyboardDown(Keys.ENTER, false) || input.isMouseDown(0, false) && !this.isMouseOver()) {
			this.selectPointerFrom = this.selectPointerTo = -1;
			this.setFocused(false);
			return;
		}
		
		//set cursor
		if (input.isMouseDown(0, false) && this.isMouseOver()) {
			if (this.currentString.length() > 0) {
				this.selectPointerFrom = this.selectPointerTo = -1;
				this.movePointer(this.getPointerPosition(this.foster, GDXUtil.getMouseX() - this.x));
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
				this.movePointer(this.selectPointerTo);
				if (this.selectPointerFrom > this.selectPointerTo) {
					int smallPointer = this.selectPointerTo;
					this.selectPointerTo = this.selectPointerFrom;
					this.selectPointerFrom = smallPointer;
				}
			}
		}
		
		//remove character
		if (input.isKeyboardDown(Keys.BACKSPACE, false) || input.isKeyboardDown(Keys.BACKSPACE, true) && input.getClickedKeyTime(Keys.BACKSPACE) > 30 && Game.get.getTick() % 2 == 0) {
			if (this.havePointerSelection()) {
				this.builder.append(this.currentString.substring(0, this.selectPointerFrom)).append(this.currentString.substring(this.selectPointerTo, this.currentString.length()));
				this.currentString = this.builder.toString();
				this.builder.setLength(0);
				this.movePointer(this.selectPointerFrom);
				this.selectPointerFrom = this.selectPointerTo = -1;
			} else if (this.pointer > 0) {
				this.builder.append(this.currentString.substring(0, this.pointer - 1)).append(this.currentString.substring(this.pointer, this.currentString.length()));
				this.currentString = this.builder.toString();
				this.builder.setLength(0);
				this.movePointer(this.pointer - 1);
			}
		}
		
		//move pointer
		boolean movePointerLeft = input.isKeyboardDown(Keys.LEFT, false) || input.isKeyboardDown(Keys.LEFT, true) && input.getClickedKeyTime(Keys.LEFT) > 30 && Game.get.getTick() % 2 == 0;
		boolean movePointerRight = input.isKeyboardDown(Keys.RIGHT, false) || input.isKeyboardDown(Keys.RIGHT, true) && input.getClickedKeyTime(Keys.RIGHT) > 30 && Game.get.getTick() % 2 == 0;
		if (movePointerLeft || movePointerRight) {
			if (this.havePointerSelection()) {
				this.movePointer(movePointerLeft ? this.selectPointerFrom : this.selectPointerTo);
				this.selectPointerFrom = this.selectPointerTo = -1;
			} else if (movePointerLeft && this.pointer > 0) {
				this.movePointer(this.pointer - 1);
			} else if (movePointerRight && this.pointer < this.currentString.length()) {
				this.movePointer(this.pointer + 1);
			}
		}
		
		//control keys
		if (input.isKeyboardDown(Keys.CONTROL_LEFT, true)) {
			//All-selection
			if (input.isKeyboardDown(Keys.A, false)) {
				this.selectPointerFrom = 0;
				this.selectPointerTo = this.currentString.length();
			}
			
			//Copy
			if (input.isKeyboardDown(Keys.C, false) && this.havePointerSelection()) {
				StringSelection selection = new StringSelection(this.currentString.substring(this.selectPointerFrom, this.selectPointerTo));
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
			}
			
			//Paste
			if (input.isKeyboardDown(Keys.V, false) && (this.currentString.length() < this.paramMaxTextLength || this.havePointerSelection())) {
				try {
					Object obj = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this).getTransferData(DataFlavor.stringFlavor);
					if (obj instanceof String) {
						if (this.havePointerSelection()) {
							this.builder.append(this.currentString.substring(0, this.selectPointerFrom)).append(this.currentString.substring(this.selectPointerTo, this.currentString.length()));
							this.currentString = this.builder.toString();
							this.builder.setLength(0);
							this.pointer = this.selectPointerFrom;
							this.selectPointerFrom = this.selectPointerTo = -1;
						}
						String objString = ((String)obj).substring(0, this.paramMaxTextLength - this.currentString.length());
						this.builder.append(this.currentString.substring(0, this.pointer));
						this.builder.append(objString);
						this.builder.append(this.currentString.substring(this.pointer, this.currentString.length()));
						this.currentString = this.builder.toString();
						this.movePointer(this.pointer + objString.length());
						this.builder.setLength(0);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		//text input
		if (input.getCharTypedLast() != null) {
			this.onTyped(input.getCharTypedLast());
		}
	}
	
	protected void onTyped(String text) {
		if (this.currentString.length() < this.paramMaxTextLength || this.havePointerSelection()) {
			if (this.paramAllowedCharacters == null || this.paramAllowedCharacters.contains(text)) {
				if (this.havePointerSelection()) {
					this.builder.append(this.currentString.substring(0, this.selectPointerFrom)).append(this.currentString.substring(this.selectPointerTo, this.currentString.length()));
					this.currentString = this.builder.toString();
					this.builder.setLength(0);
					this.pointer = this.selectPointerFrom;
					this.selectPointerFrom = this.selectPointerTo = -1;	
				}
				this.builder.append(this.currentString.substring(0, this.pointer));
				this.builder.append(text);
				this.builder.append(this.currentString.substring(this.pointer, this.currentString.length()));
				this.currentString = this.builder.toString();
				this.builder.setLength(0);
				this.movePointer(this.pointer + 1);
			}
		}
	}
	
	/** Be careful using this method, TODO needs optimizations **/
	private int getPointerPosition(Foster foster, int fieldX) {
		int prevWidth = 0, width = 0, halfPart;
		for (int i = 1; i != this.currentString.length(); i++) {
			width = (int)foster.setString(this.currentString.substring(0, i)).getWidth() - this.xTextOffset;
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
			prevWidth = width;
		}
		if (width < fieldX) return this.currentString.length();
		return 0;
	}
	
	private void movePointer(int pointer) {
		this.pointer = MathUtils.clamp(pointer, 0, this.currentString.length());

		int pointerX = (int)this.foster.setString(this.currentString.substring(0, this.pointer)).getWidth();
		int totalLength = (int)this.foster.setString(this.currentString).getWidth();
		if (pointerX - this.xTextOffset > this.width) this.xTextOffset = pointerX - this.width;
		else if (pointerX < this.xTextOffset) this.xTextOffset = pointerX;
		else if (totalLength > this.width && this.xTextOffset > totalLength - this.width) this.xTextOffset = totalLength - this.width;
	}
}
