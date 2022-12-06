package by.fxg.speceditor.std.objectTree.elements;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;

import by.fxg.speceditor.DefaultResources;
import by.fxg.speceditor.project.assets.ISpakAssetUser;
import by.fxg.speceditor.project.assets.SpakAsset;
import by.fxg.speceditor.std.g3d.EditDecal;
import by.fxg.speceditor.std.gizmos.GizmoTransformType;
import by.fxg.speceditor.std.gizmos.ITreeElementGizmos;
import by.fxg.speceditor.std.objectTree.TreeElement;

public class ElementDecal extends TreeElement implements ITreeElementGizmos, ISpakAssetUser<Texture>  {
	public SpakAsset<Texture> asset = null;
	public EditDecal decal = new EditDecal();
	
	public ElementDecal() { this("New decal"); }
	public ElementDecal(String name) {
		this.displayName = name;
	}
	
	private ElementDecal(ElementDecal copy) {
		this.displayName = copy.displayName;
		this.visible = copy.visible;
		if (copy.asset != null) copy.asset.addUser(this);
		this.decal.setBillboard(copy.decal.isBillboard());
		this.decal.position.set(copy.decal.position);
		this.decal.rotation.set(copy.decal.rotation);
		this.decal.scale.set(copy.decal.scale);
	}
	
	public Vector3 getTransform(GizmoTransformType transformType) {
		switch(transformType) {
			case TRANSLATE: return this.decal.position;
			case ROTATE: return this.decal.rotation;
			default: return Vector3.Zero;
		}
	}
	
	public Sprite getObjectTreeSprite() {
		return DefaultResources.INSTANCE.sprites.get("icons/decal");
	}
	
	public boolean isTransformSupported(GizmoTransformType transformType) {
		return transformType != GizmoTransformType.SCALE;
	}

	public void onSpakUserAdded(SpakAsset<Texture> asset) {
		if (this.asset != null) this.asset.removeUserWithoutNotify(this);
		this.asset = asset;
		this.onAssetLoad(asset);
	}
	
	public void onAssetLoad(SpakAsset<Texture> asset) {
		this.decal.setTexture(asset.getAsset());
	}
	
	public void onAssetUnload(SpakAsset<Texture> asset) {
		this.decal.setDefaultDecal();
	}
	
	public TreeElement cloneElement() {
		return new ElementDecal(this);
	}
	
	public void onDelete() {
		if (this.asset != null) {
			this.asset.removeUserWithoutNotify(this);
		}
	}
}
