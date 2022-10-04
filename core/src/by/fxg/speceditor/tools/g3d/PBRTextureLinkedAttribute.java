package by.fxg.speceditor.tools.g3d;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;

import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;

public class PBRTextureLinkedAttribute extends PBRTextureAttribute {
	public FileHandle texturePath;
	public boolean flipX, flipY;
	
	public PBRTextureLinkedAttribute(long type, TextureRegion region, FileHandle texturePath) {
		super(type, region.getTexture());
		this.texturePath = texturePath;
	}
	
	public PBRTextureLinkedAttribute(long type, Texture texture, FileHandle texturePath) {
		super(type, texture);
		this.texturePath = texturePath;
	}
	
	public PBRTextureLinkedAttribute(TextureAttribute attribute) { this(attribute, null); }
	public PBRTextureLinkedAttribute(TextureAttribute attribute, FileHandle texturePath) {
		super(attribute.type, attribute.textureDescription);
		this.texturePath = texturePath;
	}
	
	public PBRTextureLinkedAttribute setFlip(boolean flipX, boolean flipY) {
		this.flipX = flipX;
		this.flipY = flipY;
		return this;
	}
}
