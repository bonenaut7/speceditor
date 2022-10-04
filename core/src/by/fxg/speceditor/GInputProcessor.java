package by.fxg.speceditor;

import com.badlogic.gdx.Gdx;

import by.fxg.pilesos.PilesosInputImpl;

public class GInputProcessor extends PilesosInputImpl {
	public void setCursorCatched(boolean value) {
		super.setCursorCatched(value);
		if (value) Gdx.input.setCursorPosition((int)this.tempMX, (int)this.tempMY);
	}
	
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		this.moveCamera(screenX, screenY);
		return false;
	}
	
	public boolean mouseMoved(int screenX, int screenY) {
		this.moveCamera(screenX, screenY);
		return false;
	}
	
	public static IMouseController mouseController;
	private float tempMX = -1048576F, tempMY = -1048576F;
	public void moveCamera(int screenX, int screenY) {
		if (this.isCursorCatched() && mouseController != null) {
			float dx = this.tempMX - screenX;
			float dy = this.tempMY - screenY;
			mouseController.onMouseInput(dx, dy);
			this.tempMX = screenX;
			this.tempMY = screenY;
		} else {
			this.tempMX = this.tempMY = -1048576F;
		}
	}
	
	public interface IMouseController {
		void onMouseInput(float x, float y);
	}
}
