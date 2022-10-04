package by.fxg.speceditor.screen;

import com.badlogic.gdx.graphics.g2d.Batch;

import by.fxg.pilesos.graphics.font.Foster;
import space.earlygrey.shapedrawer.ShapeDrawer;

public abstract class Screen {
	abstract public void update(Batch batch, ShapeDrawer shape, Foster foster, int width, int height);
	abstract public void render(Batch batch, ShapeDrawer shape, Foster foster, int width, int height);

	public void debug(String str) {
		System.err.println("DEBUG: " + str);
	}
}