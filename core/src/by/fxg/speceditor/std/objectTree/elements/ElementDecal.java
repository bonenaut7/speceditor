package by.fxg.speceditor.std.objectTree.elements;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;

import by.fxg.speceditor.DefaultResources;
import by.fxg.speceditor.project.assets.IProjectAssetHandler;
import by.fxg.speceditor.project.assets.ProjectAsset;
import by.fxg.speceditor.std.g3d.EditDecal;
import by.fxg.speceditor.std.gizmos.GizmoTransformType;
import by.fxg.speceditor.std.gizmos.ITreeElementGizmos;
import by.fxg.speceditor.std.objectTree.TreeElement;

public class ElementDecal extends TreeElement implements ITreeElementGizmos, IProjectAssetHandler<Texture>  {
	public ProjectAsset<Texture> decalAsset = null;
	public EditDecal decal = new EditDecal();
	
	public ElementDecal() { this("New decal"); }
	public ElementDecal(String name) {
		this.displayName = name;
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

	public void onAssetHandlerAdded(ProjectAsset<Texture> asset) {
		if (this.decalAsset != null) this.decalAsset.removeHandlerWithoutNotify(this);
		this.decalAsset = asset;
		this.onAssetLoad(asset);
	}
	
	public void onAssetLoad(ProjectAsset<Texture> asset) {
		this.decal.setTexture(asset.getAsset());
	}
	
	public void onAssetUnload(ProjectAsset<Texture> asset) {
		this.decal.setDefaultDecal();
	}
	
	public TreeElement cloneElement() {
		ElementDecal elementDecal = new ElementDecal(this.getName());
		if (this.decalAsset != null) this.decalAsset.addHandler(elementDecal);
		elementDecal.decal.setBillboard(this.decal.isBillboard());
		elementDecal.decal.setVisible(this.decal.isVisible());
		elementDecal.decal.position.set(this.decal.position);
		elementDecal.decal.rotation.set(this.decal.rotation);
		elementDecal.decal.scale.set(this.decal.scale);
		return elementDecal;
	}
	
	public void onDelete() {
		if (this.decalAsset != null) {
			this.decalAsset.removeHandlerWithoutNotify(this);
		}
	}
}
