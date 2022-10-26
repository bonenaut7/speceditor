package by.fxg.speceditor.std.objectTree.elements;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import by.fxg.speceditor.Game;
import by.fxg.speceditor.ResourceManager;
import by.fxg.speceditor.project.assets.IProjectAssetHandler;
import by.fxg.speceditor.project.assets.ProjectAsset;
import by.fxg.speceditor.project.assets.ProjectAssetManager;
import by.fxg.speceditor.std.g3d.ITreeElementModelProvider;
import by.fxg.speceditor.std.gizmos.GizmoTransformType;
import by.fxg.speceditor.std.gizmos.ITreeElementGizmos;
import by.fxg.speceditor.std.objectTree.TreeElement;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ElementModel extends TreeElement implements ITreeElementGizmos, ITreeElementModelProvider, IProjectAssetHandler {
	public String localModelHandle = "";
	
	private ProjectAsset<?> modelAsset = null;
	public ModelInstance modelInstance;
	/** used for setting materials while model being reloaded or loaded another model(???) **/
	private Array<Material> _modelInstanceMaterialsCache = new Array<>();
	
	private Vector3 position = new Vector3();
	private Vector3 rotation = new Vector3();
	private Vector3 scale = new Vector3(1, 1, 1);
	
	public ElementModel() { this("New model"); }
	public ElementModel(String name) {
		this.displayName = name;
		this.modelInstance = new ModelInstance(ResourceManager.standardModel);
	}
	
	public void setModelAsset(FileHandle handle) {
		if (handle != null) {
			ProjectAsset projectAsset = null;
			if (handle.extension().equalsIgnoreCase("gltf") || handle.extension().equalsIgnoreCase("glb")) {
				projectAsset = ProjectAssetManager.INSTANCE.getLoadAsset(SceneAsset.class, handle);
			} else projectAsset = ProjectAssetManager.INSTANCE.getLoadAsset(Model.class, handle);
			projectAsset.addHandler(this);
		}
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
			default: return gizmoVector.set(0, 0, 0);
		}
	}
	
	public Sprite getObjectTreeSprite() {
		return Game.storage.sprites.get("icons/model");
	}
	
	public void onDelete() {
		if (this.modelAsset != null) {
			this.modelAsset.removeHandlerWithoutNotify(this);
		}
	}
	
	public boolean isTransformSupported(GizmoTransformType transformType) { return true; }
	public RenderableProvider getRenderableProvider() { return this.modelInstance; }
	
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
		this.setModel(ResourceManager.standardModel);
	}
	
	public void onAssetHandlerRemoved(ProjectAsset asset) {
		this.modelAsset = null;
		this.setModel(ResourceManager.standardModel);
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
