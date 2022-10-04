package by.fxg.pilesos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;

public class PilesosInputImpl implements InputProcessor {
	private static final String 
		SYMBOL_RU = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя",
		SYMBOL_RUI = "ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖ×ØÙÚÛÜÝÞßàáâãäåæçèéêëìíîïðñòóôõö÷øùúûüýþÿ",
		SYMBOL_EN = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz",
		SYMBOL_MK = "1234567890.,;:?!()[]{}<>|\\/ \"'@$€-%+=#_&~*§©";
	public static String ALLOWED_CHARACTERS = SYMBOL_RU + SYMBOL_RUI + SYMBOL_EN + SYMBOL_MK;
	
	public boolean isCursorCatched = true;
	public long[] mouseScrollTime = new long[2];
	public long[] mouseClickTime = new long[5];
	public long[] keyboardClickTime = new long[255/*180 max*/];
	
	public boolean isMouseScrolled(boolean up) { return this.mouseScrollTime[up ? 1 : 0] == Pilesos.getApp().getTick() - 1; }
	public boolean isMouseDown(int key, boolean isHold) { return isHold ? this.mouseClickTime[key] > 0L : this.mouseClickTime[key] == Pilesos.getApp().getTick() - 1; }
	public boolean isKeyboardDown(int key, boolean isHold) { return isHold ? this.keyboardClickTime[key] > 0L : this.keyboardClickTime[key] == Pilesos.getApp().getTick() - 1; }
	
	public long getClickedMouseTime(int key) { return Pilesos.getApp().getTick() - this.mouseClickTime[key]; }
	public long getClickedKeyTime(int key) { return Pilesos.getApp().getTick() - this.keyboardClickTime[key]; }
	
	public boolean isCursorCatched() { return Gdx.input.isCursorCatched() || this.isCursorCatched; }
	public void setCursorCatched(boolean bool) {
		Gdx.input.setCursorCatched(this.isCursorCatched = bool);
	}
	
//===========================================================================================================================
	
	public boolean keyDown(int keycode) {
		if (keycode >= 0) {
			this.keyboardClickTime[keycode] = Pilesos.getApp().getTick();
			return true;
		}
		return false;
	}

	public boolean keyUp(int keycode) {
		if (keycode >= 0) {
			this.keyboardClickTime[keycode] = 0;
			return true;
		}
		return false;
	}
	
	public String getCharTypedLast() {
		return this.lastChar.length() > 0 && this.lastCharTyped == Pilesos.getApp().getTick() - 1 ? this.lastChar : null;
	}

	private String lastChar = "";
	private long lastCharTyped;
	public boolean keyTyped(char character) {
		if (ALLOWED_CHARACTERS.contains(String.valueOf(character))) {
			this.lastChar = String.valueOf(character);
			this.lastCharTyped = Pilesos.getApp().getTick();
			return true;
		}
		return false;
	}

	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (button >= 0) {
			this.mouseClickTime[button] = Pilesos.getApp().getTick();
			return true;
		}
		return false;
	}

	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (button >= 0) {
			this.mouseClickTime[button] = 0;
			return true;
		}
		return false;
	}

	public boolean scrolled(float amountX, float amountY) {
		this.mouseScrollTime[amountY > 0.0 ? 1 : 0] = Pilesos.getApp().getTick();
		return false;
	}
	
	public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
	public boolean mouseMoved(int screenX, int screenY) { return false; }
}
