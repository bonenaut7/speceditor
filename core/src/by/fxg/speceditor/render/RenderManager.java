package by.fxg.speceditor.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import by.fxg.pilesos.graphics.PilesosScissorStack;
import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.Game;
import by.fxg.speceditor.screen.BaseScreen;
import by.fxg.speceditor.screen.Screen;
import by.fxg.speceditor.screen.ScreenSelectProject;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class RenderManager {
	private static TextureRegion shapeDrawerRegion;
	public static Foster foster;
	public static PolygonSpriteBatch batch;
	public static ShapeDrawer shape;
	
	public BaseScreen currentScreen, currentGui;
	
	public RenderManager(Game game) {
		this.currentScreen = new ScreenSelectProject();
		this.currentGui = null;
		
		if (shapeDrawerRegion == null) {
			Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
			pixmap.setColor(Color.WHITE);
			pixmap.drawPixel(0, 0);
			shapeDrawerRegion = new TextureRegion(new Texture(pixmap), 0, 0, 1, 1);
			pixmap.dispose();
		}
		batch = new PolygonSpriteBatch();
		shape = new ShapeDrawer(batch, shapeDrawerRegion);
		foster = new Foster().setBatch(batch);
		new PilesosScissorStack(10);	
	}
	
	public void update(Game demo, int width, int height) {
		if (this.currentGui != null) this.currentGui.update(batch, shape, foster, width, height);
		else {
			if (this.currentScreen != null) this.currentScreen.update(batch, shape, foster, width, height);
		}
	}
	
	public void render(Game demo, int width, int height) {
		if (this.currentScreen != null) this.currentScreen.render(batch, shape, foster, width, height);
		if (this.currentGui != null) this.currentGui.render(batch, shape, foster, width, height);
	}
	
	public Screen getScreen() {
		return this.currentScreen;
	}
	
	public void resize(int width, int height) {
		if (this.currentScreen != null) this.currentScreen.resize(width, height);
		if (this.currentGui != null) this.currentGui.resize(width, height);
	}
}
