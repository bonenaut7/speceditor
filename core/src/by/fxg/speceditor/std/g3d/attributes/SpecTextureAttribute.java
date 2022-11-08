package by.fxg.speceditor.std.g3d.attributes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.utils.Disposable;

import by.fxg.speceditor.DefaultResources;
import by.fxg.speceditor.project.assets.IProjectAssetHandler;
import by.fxg.speceditor.project.assets.ProjectAsset;

/** Tips: <br>
 * 	 - To connect ProjectAsset, use {@link ProjectAsset#addHandler(IProjectAssetHandler)} with object of this class.
 * **/
public class SpecTextureAttribute extends TextureAttribute implements IProjectAssetHandler<Texture>, Disposable {
	public ProjectAsset<Texture> asset;
	public boolean flipX, flipY;
	
	public SpecTextureAttribute(long type) {
		super(type, DefaultResources.INSTANCE.standardTexture);
	}
	
	public SpecTextureAttribute(TextureAttribute attribute) {
		super(attribute.type, attribute.textureDescription);
	}
	
	public SpecTextureAttribute(SpecTextureAttribute attribute) {
		super(attribute.type, attribute.textureDescription);
		this.flipX = attribute.flipX;
		this.flipY = attribute.flipY;
		if (attribute.asset != null) {
			attribute.asset.addHandler(this);
		}
	}
	
	/** Sets flip values **/
	public SpecTextureAttribute setFlip(Boolean flipX, Boolean flipY) {
		return this.flip(!flipX.equals(this.flipX), !flipY.equals(this.flipY));
	}
	
	/** Flips texture **/
	public SpecTextureAttribute flip(boolean flipX, boolean flipY) {
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
		this.setTexture(asset.isLoaded() ? asset.getAsset() : DefaultResources.INSTANCE.standardTexture);
	}
	
	public void onAssetLoad(ProjectAsset<Texture> asset) {
		this.setTexture(asset.getAsset());
	}
	
	public void onAssetUnload(ProjectAsset<Texture> asset) {
		this.setTexture(DefaultResources.INSTANCE.standardTexture);
	}
	
	public Attribute copy() {
		return new SpecTextureAttribute(this);
	}

	public void dispose() {
		if (this.asset != null) {
			this.asset.removeHandlerWithoutNotify(this);
		}
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
}
