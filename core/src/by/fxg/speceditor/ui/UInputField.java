package by.fxg.speceditor.ui;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Align;

import by.fxg.pilesos.PilesosInputImpl;
import by.fxg.pilesos.graphics.PilesosScissorStack;
import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.pilesos.utils.GDXUtil;
import by.fxg.speceditor.Game;
import by.fxg.speceditor.ui.SpecInterface.AppCursor;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class UInputField implements SpecInterface.IFocusable {
	public static final int GLYPH_SIZE = 6;//8;
	public int x, y, width, height;
	private int maxLength = 0, pointer = 0, renderOffset = 0, flashTimer = 0;
	private String text = "", allowedCharacters = PilesosInputImpl.ALLOWED_CHARACTERS;
	public boolean allSelected = false;
	
	public UInputField(int x, int y, int sx, int sy) {
		this.x = x;
		this.y = y;
		this.width = sx;
		this.height = sy;
	}
	
	public String getText() { return this.text; }
	
	public UInputField setTransforms(int x, int y, int width, int height) { this.x = x; this.y = y; this.width = width; this.height = height; return this; }
	public UInputField setMaxLength(int maxLength) { this.maxLength = maxLength; return this; }
	public UInputField setAllowedCharacters(String allowedCharacters) { this.allowedCharacters = allowedCharacters; return this; }
	public UInputField setPointer(int pointer) { this.pointer = pointer < this.text.length() ? pointer : this.text.length(); return this; }
	public UInputField setText(String text, boolean setPointer) { this.text = text; if (this.pointer > this.text.length() || setPointer) this.pointer = this.text.length(); return this; }
	public UInputField setText(String text) { return this.setText(text, true); }
	
	public void update() {
		if (GDXUtil.isMouseInArea(this.x, this.y, this.width, this.height)) SpecInterface.setCursor(AppCursor.IBEAM);
		PilesosInputImpl input = Game.get.getInput();
		if (this.isFocused()) {
			if (input.isKeyboardDown(Keys.ESCAPE, false) || input.isKeyboardDown(Keys.ENTER, false) || input.isMouseDown(0, false) && !GDXUtil.isMouseInArea(this.x, this.y, this.width, this.height)) { this.setFocused(this.allSelected = false); return; }
			if (input.isKeyboardDown(Keys.BACKSPACE, false) || input.isKeyboardDown(Keys.BACKSPACE, true) && input.getClickedKeyTime(Keys.BACKSPACE) > 30 && Game.get.getTick() % 3 == 0) {
				if (this.allSelected) {
					this.allSelected = false;
					this.text = "";
					this.pointer = this.renderOffset = 0;
				} else  if (this.text.length() > 0 && this.pointer > 0) {
					this.text = this.pointer < this.text.length() ? this.text.substring(0, this.pointer - 1) + this.text.substring(this.pointer) : this.text.substring(0, this.text.length() - 1);
					this.pointer--;
					if (this.renderOffset > 0 && this.pointer == this.text.length()) this.renderOffset = (this.pointer * GLYPH_SIZE - this.width) / GLYPH_SIZE + 2;
					else if (this.renderOffset > 0 && this.pointer < this.text.length() && this.renderOffset * GLYPH_SIZE - GLYPH_SIZE - this.width < 0) this.renderOffset--;
				}
			} else if (input.isKeyboardDown(Keys.LEFT, false) || input.isKeyboardDown(Keys.LEFT, true) && input.getClickedKeyTime(Keys.LEFT) > 30 && Game.get.getTick() % 3 == 0) {
				if (this.allSelected) { 
					this.pointer = 0;
					this.allSelected = false;
					this.renderOffset = 0;
				} else if (this.pointer > 0) {
					this.pointer--;
					if (this.pointer * GLYPH_SIZE < this.renderOffset * GLYPH_SIZE) this.renderOffset--;
				}
				this.flashTimer = 0;
			} else if (input.isKeyboardDown(Keys.RIGHT, false) || input.isKeyboardDown(Keys.RIGHT, true) && input.getClickedKeyTime(Keys.RIGHT) > 30 && Game.get.getTick() % 3 == 0) {
				if (this.allSelected) { 
					this.pointer = this.text.length(); 
					this.allSelected = false;
					if (this.text.length() * GLYPH_SIZE + GLYPH_SIZE - this.width > 0) this.renderOffset = (this.pointer * GLYPH_SIZE + GLYPH_SIZE - this.width) / GLYPH_SIZE + 1;
				} else if (this.pointer < this.text.length()) {
					this.pointer++;
					if (this.pointer * GLYPH_SIZE + GLYPH_SIZE > this.width + this.renderOffset * GLYPH_SIZE) this.renderOffset++;
				}
				this.flashTimer = 0;
			} else if (input.isKeyboardDown(Keys.CONTROL_LEFT, true)) {
				if (input.isKeyboardDown(Keys.A, false)) {
					if (this.allSelected) this.pointer = this.text.length();
					this.allSelected = !this.allSelected;
				} else if (this.allSelected && input.isKeyboardDown(Keys.C, false)) {
					StringSelection selection = new StringSelection(this.text);
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					clipboard.setContents(selection, selection);
				} else if (input.isKeyboardDown(Keys.V, false)) {
					try {
						Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
						Object obj = clipboard.getContents(this).getTransferData(DataFlavor.stringFlavor);
						if (obj != null) {
							if (this.allSelected) this.text = (String)obj;
							else this.text += (String)obj;
							if (this.text.length() > this.maxLength) this.text = this.text.substring(0, this.maxLength);
							this.pointer = this.text.length();
							this.allSelected = false;
						}
					} catch (Exception e) {}
				}
				
			} else if (input.getCharTypedLast() != null) {
				if ((this.text.length() < this.maxLength || this.allSelected) && (this.allowedCharacters.equals("-") || this.allowedCharacters.contains(input.getCharTypedLast()))) {
					if (this.allSelected) {
						this.text = input.getCharTypedLast();
						this.pointer = this.text.length();
						this.allSelected = false;
						this.renderOffset = 0;
					} else {
						if (this.pointer < this.text.length()) this.text = this.text.substring(0, this.pointer) + input.getCharTypedLast() + this.text.substring(this.pointer);
						else this.text += input.getCharTypedLast();
						this.pointer++;
						if (this.text.length() * GLYPH_SIZE + GLYPH_SIZE - this.width > 0) this.renderOffset++;
						this.flashTimer = 0;
					}
				}
			}
			if (input.isMouseDown(0, false) && GDXUtil.isMouseInArea(this.x, this.y, this.width, this.height)) {
				boolean rightSided = true;
				if (rightSided) {
					int espr = this.renderOffset + (GDXUtil.getMouseX() - this.x - 2) / GLYPH_SIZE;
					this.pointer = espr > this.text.length() ? this.text.length() : espr;
				}
			} 
		} else {
			if (this.allSelected) this.allSelected = false;
			if (input.isMouseDown(0, false) && GDXUtil.isMouseInArea(this.x, this.y, this.width, this.height)) {
				this.setFocused(true);
			}
			//else this.setFocused(this.allSelected = false);
		}
		if (this.flashTimer++ > 99) this.flashTimer = 0;
	}
	
	public void render(Batch batch, ShapeDrawer shape, Foster foster) {
		shape.setColor(0.25f, 0.25f, 0.25f, 1f);
		shape.rectangle(this.x + 1, this.y, this.width - 1, this.height - 1); // maybe move text -1 by Y axis
		batch.flush();

		if (PilesosScissorStack.instance.setBounds(0, this.x + 2, this.y + 2, this.width - 4, this.height - 4).pushScissors(0)) {
			if (this.allSelected) {
				shape.setColor(0.1f, 0.25f, 0.75f, 1f);
				shape.filledRectangle(this.x + 2, this.y + 2, this.width, 16);
			}
			foster.setString(this.text).draw(this.x + 6 - this.renderOffset * GLYPH_SIZE, this.y + 5 + GLYPH_SIZE, Align.left);
			
			if (this.isFocused() && this.flashTimer < 50) { // pointer
				int xPos = this.x + 6 + this.pointer * GLYPH_SIZE - this.renderOffset * GLYPH_SIZE;
				shape.setColor(1, 1, 1, 1);
				shape.line(xPos, this.y + 3, xPos, this.y + this.height - 3);
			}
			batch.flush();
			PilesosScissorStack.instance.popScissors();
		}
	}
	
	public void onFocusRemoved() {
		this.allSelected = false;
	}
}
