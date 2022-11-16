package by.fxg.speceditor.std.objectTree.elements;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import by.fxg.speceditor.DefaultResources;
import by.fxg.speceditor.project.assets.IProjectAssetHandler;
import by.fxg.speceditor.project.assets.ProjectAsset;
import by.fxg.speceditor.std.gizmos.GizmoTransformType;
import by.fxg.speceditor.std.gizmos.ITreeElementGizmos;
import by.fxg.speceditor.std.objectTree.ITreeElementModelProvider;
import by.fxg.speceditor.std.objectTree.TreeElement;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ElementModel extends TreeElement implements ITreeElementGizmos, ITreeElementModelProvider, IProjectAssetHandler {
	public ProjectAsset<?> modelAsset = null;
	public ModelInstance modelInstance;
	/** used for setting materials while model being reloaded or loaded another model(???) **/
	private Array<Material> _modelInstanceMaterialsCache = new Array<>();
	
	private Vector3 position = new Vector3();
	private Vector3 rotation = new Vector3();
	private Vector3 scale = new Vector3(1, 1, 1);
	
	public ElementModel() { this("New model"); }
	public ElementModel(String name) {
		this.displayName = name;
		this.modelInstance = new ModelInstance(DefaultResources.INSTANCE.standardModel);
	}
	
	private ElementModel(ElementModel copy) {
		this.displayName = copy.displayName;
		this.visible = copy.visible;
		if (copy.modelAsset != null) {
			if (copy.modelInstance != null) {
				for (Material material : copy.modelInstance.materials) {
					this._modelInstanceMaterialsCache.add(material.copy());
				}
			}
			copy.modelAsset.addHandler(this);
		}
		this.position.set(copy.position);
		this.rotation.set(copy.rotation);
		this.scale.set(copy.scale);
	}
	
	public ITreeElementModelProvider applyTransforms() {
		if (this.modelInstance != null) {
			this.modelInstance.transform.setToTranslation(this.position);
			this.modelInstance.transform.scale(this.scale.x, this.scale.y, this.scale.z);
			this.modelInstance.transform.rotate(1f, 0f, 0f, this.rotation.x);
			this.modelInstance.transform.rotate(0f, 1f, 0f, this.rotation.y);
			this.modelInstance.transform.rotate(0f, 0f, 1f, this.rotation.z);
		}
		return this;
	}
	
	public Vector3 getTransform(GizmoTransformType transformType) {
		switch(transformType) {
			case TRANSLATE: return this.position;
			case ROTATE: return this.rotation;
			case SCALE: return this.scale;
			default: return Vector3.Zero;
		}
	}
	
	public RenderableProvider getRenderableProvider() {
		return this.modelInstance;
	}
	
	public void onAssetHandlerAdded(ProjectAsset asset) {
		if (this.modelAsset != null) this.modelAsset.removeHandlerWithoutNotify(this);
		this.modelAsset = asset;
		this.onAssetLoad(asset);
	}
	
	public void onAssetLoad(ProjectAsset asset) {
		Object object = asset.getAsset();
		if (object instanceof SceneAsset) { //gltf model
			this.setModel(((SceneAsset)object).scene.model);
		} else if (object instanceof Model) { //default model
			this.setModel((Model)object);
		}
	}
	
	public void onAssetUnload(ProjectAsset asset) {
		this._modelInstanceMaterialsCache.size = 0;
		if (this.modelInstance != null) {
			for (Material material : this.modelInstance.materials) {
				this._modelInstanceMaterialsCache.add(material.copy());
			}
		}
		this.setModel(DefaultResources.INSTANCE.standardModel);
	}
	
	public void onAssetHandlerRemoved(ProjectAsset asset) {
		this.modelAsset = null;
		this.setModel(DefaultResources.INSTANCE.standardModel);
	}
	
	public Sprite getObjectTreeSprite() {
		return DefaultResources.INSTANCE.sprites.get("icons/model");
	}
	
	public TreeElement cloneElement() {
		return new ElementModel(this);
	}
	
	public void onDelete() {
		if (this.modelAsset != null) {
			this.modelAsset.removeHandlerWithoutNotify(this);
		}
	}

	private void setModel(Model model) {
		this.modelInstance = new ModelInstance(model);
		if (!this._modelInstanceMaterialsCache.isEmpty()) {
			for (Material material : this._modelInstanceMaterialsCache) {
				Material modelMaterial = this.modelInstance.getMaterial(material.id);
				if (modelMaterial != null) {
					modelMaterial.set(material);
				}
			}
		}
	}
}
