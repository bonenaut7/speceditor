package by.fxg.pilesos.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import by.fxg.pilesos.graphics.font.Foster;
import space.earlygrey.shapedrawer.ShapeDrawer;

public abstract class Screen {
	private static TextureRegion shapeDrawerRegion;
	public Foster foster;
	public PolygonSpriteBatch batch;
	public ShapeDrawer shape;
	
	public Screen() { this(true); }
	public Screen(boolean create) {
		this.foster = new Foster();
		if (create) {
			if (shapeDrawerRegion == null) {
				Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
				pixmap.setColor(Color.WHITE);
				pixmap.drawPixel(0, 0);
				shapeDrawerRegion = new TextureRegion(new Texture(pixmap), 0, 0, 1, 1);
				pixmap.dispose();
			}
			this.batch = new PolygonSpriteBatch();
			this.shape = new ShapeDrawer(this.batch, shapeDrawerRegion);
			this.foster.setBatch(this.batch);
		}
	}
	
	abstract public void update(int width, int height);
	abstract public void render(int width, int height);

	protected Screen dispose() {
		if (this.batch != null) this.batch.dispose();
		return this;
	}
	
	public void debug(String str) {
		System.err.println("DEBUG: " + str);
	}
}
