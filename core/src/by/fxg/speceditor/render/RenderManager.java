package by.fxg.speceditor.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import by.fxg.pilesos.graphics.PilesosScissorStack;
import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.SpecEditor;
import by.fxg.speceditor.screen.BaseScreen;
import by.fxg.speceditor.screen.Screen;
import by.fxg.speceditor.screen.ScreenLoading;

public class RenderManager {
	private static TextureRegion shapeDrawerRegion;
	public static Foster foster;
	public static PolygonSpriteBatch batch;
	public static ExtShapeDrawer shape;
	
	public BaseScreen currentScreen, currentGui;
	
	public RenderManager(SpecEditor game) {
		if (shapeDrawerRegion == null) {
			Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
			pixmap.setColor(Color.WHITE);
			pixmap.drawPixel(0, 0);
			shapeDrawerRegion = new TextureRegion(new Texture(pixmap), 0, 0, 1, 1);
			pixmap.dispose();
		}
		batch = new PolygonSpriteBatch();
		shape = new ExtShapeDrawer(batch, shapeDrawerRegion);
		foster = new Foster().setBatch(batch);
		new PilesosScissorStack(10);
		
		this.currentScreen = new ScreenLoading();
		this.currentGui = null;
	}
	
	public void update(SpecEditor demo, int width, int height) {
		if (this.currentGui != null) this.currentGui.update(batch, shape, foster, width, height);
		else if (this.currentScreen != null) this.currentScreen.update(batch, shape, foster, width, height);
	}
	
	public void render(SpecEditor demo, int width, int height) {
		if (this.currentGui != null) this.currentGui.render(batch, shape, foster, width, height);
		// Not allowing to render screen if gui is rendering because of UI issues, XXX need to make something like FrameBuffer to save prev. frame of screen or something like that
		else if (this.currentScreen != null) this.currentScreen.render(batch, shape, foster, width, height);
	}
	
	public Screen getScreen() {
		return this.currentScreen;
	}
	
	public void resize(int width, int height) {
		batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
		shape.update();
		if (this.currentScreen != null) this.currentScreen.resize(width, height);
		if (this.currentGui != null) this.currentGui.resize(width, height);
	}
}
