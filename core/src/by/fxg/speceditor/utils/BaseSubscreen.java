package by.fxg.speceditor.utils;

import com.badlogic.gdx.graphics.g2d.Batch;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.screen.Screen;
import space.earlygrey.shapedrawer.ShapeDrawer;

public abstract class BaseSubscreen extends Screen {
	public void update(Batch batch, ShapeDrawer shape, Foster foster, int width, int height) {}
	public void render(Batch batch, ShapeDrawer shape, Foster foster, int width, int height) {}
	abstract public void update(Batch batch, ShapeDrawer shape, Foster foster, int x, int y, int width, int height);
	abstract public void render(Batch batch, ShapeDrawer shape, Foster foster, int x, int y, int width, int height);
	abstract public void resize(int subX, int subY, int subWidth, int subHeight);
}
