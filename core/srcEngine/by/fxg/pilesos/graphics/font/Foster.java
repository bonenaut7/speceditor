package by.fxg.pilesos.graphics.font;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.Align;

public class Foster {
	public static BitmapFont defaultFont = null;
	private static GlyphLayout layout = new GlyphLayout();
	
	private Batch batch = null;
	private BitmapFont font = null;
	private String string = "";
	private float fontHeight, fontHalfHeight, stringWidth, stringHalfWidth;
	
	public Foster() { this(null, defaultFont); }
	public Foster(BitmapFont font) { this(null, font); }
	public Foster(Batch batch) { this(batch, defaultFont); }
	public Foster(Batch batch, BitmapFont font) {
		this.setBatch(batch);
		this.setFont(font);
	}
	
	public BitmapFont getFont() { return this.font; }
	public Foster setFont(BitmapFont font) {
		this.font = font;
		this.fontHalfHeight = (this.fontHeight = font.getCapHeight()) / 2.0F;
		return this;
	}
	
	public Batch getBatch() { return this.batch; }
	public Foster setBatch(Batch batch) {
		this.batch = batch;
		return this;
	}
	
	public String getString() { return this.string; }
	public Foster setString(String str) {
		this.string = str;
		layout.setText(this.font, str);
		this.stringWidth = layout.width;
		return this;
	}

	/** Draws string with Y offset(font height) **/
	public Foster draw(float x, float y) {
		return this.drawRaw(x, y + this.fontHeight, Align.center);
	}
	
	/** Draws string with Y offset(font height) **/
	public Foster draw(float x, float y, int align) {
		return this.drawRaw(x, y + this.fontHeight, align);
	}
	
	/** Draws string without Y offset(font height) **/
	public Foster drawRaw(float x, float y) { return this.drawRaw(x, y, Align.center); }
	/** Draws string without Y offset(font height) **/
	public Foster drawRaw(float x, float y, int align) {
		this.font.draw(this.batch, this.string, x, y, 0, align, false);
		return this;
	}

	public float getHalfHeight() { return this.fontHalfHeight; }
	public float getHalfWidth() { return this.stringHalfWidth; }
	public float getHeight() { return this.fontHeight; }
	public float getWidth() { return this.stringWidth; }
}
