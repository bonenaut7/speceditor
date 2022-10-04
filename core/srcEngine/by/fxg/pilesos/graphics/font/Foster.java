package by.fxg.pilesos.graphics.font;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.Align;

public class Foster {
	public static BitmapFont defaultFont = null;
	private static GlyphLayout layout = new GlyphLayout();
	private Batch batch = null;
	private BitmapFont font;
	private String string;
	
	public Foster() { this(defaultFont, null); }
	public Foster(String str) { this(defaultFont, str); }
	public Foster(BitmapFont font) { this(font, null); }
	public Foster(BitmapFont font, String str) {
		this.font = font;
		this.string = str;
		if (this.string != null) this.updateLayout();
	}
	
	public Foster setBatch(Batch batch) {
		this.batch = batch;
		return this;
	}
	
	public Foster setString(String str) {
		this.string = str;
		return this;
	}

	public Foster draw(float x, float y) { return this.draw(this.batch, x, y, Align.center); }
	public Foster draw(float x, float y, int align) { return this.draw(this.batch, x, y, align); }
	public Foster draw(Batch batch, float x, float y) { return this.draw(batch, x, y, Align.center); }
	public Foster draw(Batch batch, float x, float y, int align) {
		this.font.draw(batch, this.string, x, y, 0, align, false);
		return this;
	}

	public String getString() { return this.string; }
	public float getHalfHeight() { return this.getHeight() / 2; }
	public float getHalfWidth() { return this.getWidth() / 2; }
	
	public float getHeight() {
		this.updateLayout();
		return layout.height;
	}
	
	public float getWidth() {
		this.updateLayout();
		return layout.width;
	}
	
	private void updateLayout() { layout.setText(this.font, this.string); }
}
