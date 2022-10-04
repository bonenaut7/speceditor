package by.fxg.pilesos.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

public class TextureFrameBuffer {	
	private FrameBuffer frameBuffer;
	private TextureRegion textureRegion;
	
	private Format format;
	private int width, height;
	private boolean hasDepth;
	
	public TextureFrameBuffer() { this(Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true); }
	public TextureFrameBuffer(int width, int height) { this(Format.RGBA8888, width, height, true); }
	public TextureFrameBuffer(int width, int height, boolean hasDepth) { this(Format.RGBA8888, width, height, hasDepth); }
	public TextureFrameBuffer(Format format, int width, int height) { this(format, width, height, true); }
	public TextureFrameBuffer(Format format, int width, int height, boolean hasDepth) {
		this.frameBuffer = new FrameBuffer(format, width, height, hasDepth);
		this.textureRegion = new TextureRegion(this.frameBuffer.getColorBufferTexture());
		this.format = format;
		this.width = width;
		this.height = height;
		this.hasDepth = hasDepth;
	}
	
	private TextureFrameBuffer updateBuffer() {
		this.frameBuffer.dispose();
		this.frameBuffer = new FrameBuffer(format, width, height, hasDepth);
		this.textureRegion = new TextureRegion(this.frameBuffer.getColorBufferTexture());
		return this;
	}
	
	public TextureFrameBuffer setFormat(Format format) {
		this.format = format;
		return this.updateBuffer();
	}
	
	public TextureFrameBuffer setSize(int width, int height) {
		this.width = width;
		this.height = height;
		return this.updateBuffer();
	}
	
	public TextureFrameBuffer setHasDepth(boolean hasDepth) {
		this.hasDepth = hasDepth;
		return this.updateBuffer();
	}
	
	public TextureFrameBuffer flip(boolean flipX, boolean flipY) {
		this.textureRegion.flip(flipX, flipY);
		return this;
	}
	
	public void capture() { this.capture(0f, 0f, 0f, 1f); }
	public void capture(Color color) { this.capture(color.r, color.g, color.b, color.a); }
	public void capture(float r, float g, float b, float a) {
		this.frameBuffer.begin();
		Gdx.gl.glClearColor(r, g, b, a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
	}
	
	public void endCapture() {
		this.frameBuffer.end();
	}
	
	public TextureRegion getTexture() {
		return this.textureRegion;
	}
}
