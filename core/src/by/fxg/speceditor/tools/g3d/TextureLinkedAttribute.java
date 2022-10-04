package by.fxg.speceditor.tools.g3d;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;

public class TextureLinkedAttribute extends TextureAttribute {
	public FileHandle texturePath;
	public boolean flipX, flipY;
	
	public TextureLinkedAttribute(long type, TextureRegion region, FileHandle texturePath) {
		super(type, region);
		this.texturePath = texturePath;
	}
	
	public TextureLinkedAttribute(long type, Texture texture, FileHandle texturePath) {
		super(type, texture);
		this.texturePath = texturePath;
	}
	
	public TextureLinkedAttribute(TextureAttribute attribute) { this(attribute, null); }
	public TextureLinkedAttribute(TextureAttribute attribute, FileHandle texturePath) {
		super(attribute.type, attribute.textureDescription);
		this.texturePath = texturePath;
	}
	
	public TextureLinkedAttribute setFlip(boolean flipX, boolean flipY) {
		this.flipX = flipX;
		this.flipY = flipY;
		return this;
	}
}
