package by.fxg.speceditor.std.g3d;

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
		if (flipX) this.flipX = !this.flipX;
		if (flipY) this.flipY = !this.flipY;
		if (this.textureDescription != null && this.textureDescription.texture != null) {
			TextureRegion region = new TextureRegion(this.textureDescription.texture);
			region.flip(this.flipX, this.flipY);
			this.offsetU = region.getU();
			this.offsetV = region.getV();
			this.scaleU = region.getU2() - this.offsetU;
			this.scaleV = region.getV2() - this.offsetV;
		}
		return this;
	}
}
