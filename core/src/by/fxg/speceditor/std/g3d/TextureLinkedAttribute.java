package by.fxg.speceditor.std.g3d;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;

import by.fxg.speceditor.ResourceManager;
import by.fxg.speceditor.project.assets.IProjectAssetHandler;
import by.fxg.speceditor.project.assets.ProjectAsset;

/** Tips: <br>
 * 	 - To connect ProjectAsset, use {@link ProjectAsset#addHandler(IProjectAssetHandler)} with object of this class.
 * **/
public class TextureLinkedAttribute extends TextureAttribute implements IProjectAssetHandler<Texture> {
	public ProjectAsset<Texture> asset;
	public boolean flipX, flipY;
	
	public TextureLinkedAttribute(long type) {
		super(type, ResourceManager.standardTexture);
	}
	
	public TextureLinkedAttribute(TextureAttribute attribute) {
		super(attribute.type, attribute.textureDescription);
	}
	
	public TextureLinkedAttribute(TextureLinkedAttribute attribute) {
		super(attribute.type, attribute.textureDescription);
		this.flipX = attribute.flipX;
		this.flipY = attribute.flipY;
		if (attribute.asset != null) {
			attribute.asset.addHandler(this);
		}
	}
	
	/** Sets flip values **/
	public TextureLinkedAttribute setFlip(Boolean flipX, Boolean flipY) {
		return this.flip(!flipX.equals(this.flipX), !flipY.equals(this.flipY));
	}
	
	/** Flips texture **/
	public TextureLinkedAttribute flip(boolean flipX, boolean flipY) {
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
	
	public void onAssetHandlerAdded(ProjectAsset<Texture> asset) {
		if (this.asset != null) this.asset.removeHandlerWithoutNotify(this);
		this.asset = asset;
		this.setTexture(asset.isLoaded() ? asset.getAsset() : ResourceManager.standardTexture);
	}
	
	public void onAssetLoad(ProjectAsset<Texture> asset) {
		this.setTexture(asset.getAsset());
	}
	
	public void onAssetUnload(ProjectAsset<Texture> asset) {
		this.setTexture(ResourceManager.standardTexture);
	}
	
	/** Sets texture with flips **/
	private void setTexture(Texture texture) {
		TextureRegion region = new TextureRegion(texture);
		region.flip(this.flipX, this.flipY);
		this.textureDescription.texture = texture;
		this.offsetU = region.getU();
		this.offsetV = region.getV();
		this.scaleU = region.getU2() - this.offsetU;
		this.scaleV = region.getV2() - this.offsetV;
	}
	
	public Attribute copy() {
		return new TextureLinkedAttribute(this);
	}
}
