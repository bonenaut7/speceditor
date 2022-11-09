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
	
	private ElementDecal(ElementDecal copy) {
		this.displayName = copy.displayName;
		this.visible = copy.visible;
		if (copy.decalAsset != null) copy.decalAsset.addHandler(this);
		this.decal.setBillboard(copy.decal.isBillboard());
		this.decal.setVisible(copy.decal.isVisible());
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
		return new ElementDecal(this);
	}
	
	public void onDelete() {
		if (this.decalAsset != null) {
			this.decalAsset.removeHandlerWithoutNotify(this);
		}
	}
}
