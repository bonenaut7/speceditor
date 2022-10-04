package by.fxg.speceditor;

import com.badlogic.gdx.Gdx;

import by.fxg.pilesos.PilesosInputImpl;
import by.fxg.speceditor.screen.project.map.ScreenProject;
import by.fxg.speceditor.screen.project.map.SubscreenViewport;

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
	
	public int curX, curY;
	private float tempMX = -1048576F, tempMY = -1048576F;
	public void moveCamera(int screenX, int screenY) {
		if (this.isCursorCatched() && Game.get.renderer != null && Game.get.renderer.getScreen() != null) {
			float dx = this.tempMX - screenX;
			float dy = this.tempMY - screenY;
			if (Game.get.renderer.currentScreen != null && Game.get.renderer.currentScreen instanceof ScreenProject) {
				((SubscreenViewport)((ScreenProject)Game.get.renderer.currentScreen).subViewport).applyCameraMovement(dx, dy);
			}
			this.tempMX = screenX;
			this.tempMY = screenY;
		} else {
			this.tempMX = this.tempMY = -1048576F;
		}
		curX = screenX;
		curY = screenY;
	}
}
