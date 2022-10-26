package by.fxg.speceditor.std.g3d;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import by.fxg.speceditor.ResourceManager;
import by.fxg.speceditor.project.assets.IProjectAssetHandler;
import by.fxg.speceditor.project.assets.ProjectAsset;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;

/** Tips: <br>
 * 	 - To connect ProjectAsset, use {@link ProjectAsset#addHandler(IProjectAssetHandler)} with object of this class.
 * **/
public class PBRTextureLinkedAttribute extends PBRTextureAttribute implements IProjectAssetHandler<Texture> {
	public ProjectAsset<Texture> asset;
	public boolean flipX, flipY;
	
	//TODO: Constructor from TextureAttribute & self(for #copy() method)
	public PBRTextureLinkedAttribute(long type) {
		super(type, ResourceManager.standardTexture);
	}
	
	/** Sets flip values **/
	public PBRTextureLinkedAttribute setFlip(Boolean flipX, Boolean flipY) {
		return this.flip(!flipX.equals(this.flipX), !flipY.equals(this.flipY));
	}
	
	/** Flips texture **/
	public PBRTextureLinkedAttribute flip(boolean flipX, boolean flipY) {
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
}
