package by.fxg.speceditor.std.objectTree.elements;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.Vector3;

import by.fxg.speceditor.Game;
import by.fxg.speceditor.ResourceManager;
import by.fxg.speceditor.std.g3d.IModelProvider;
import by.fxg.speceditor.std.gizmos.GizmoTransformType;
import by.fxg.speceditor.std.gizmos.ITreeElementGizmos;
import by.fxg.speceditor.std.objectTree.TreeElement;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

public class ElementModel extends TreeElement implements ITreeElementGizmos, IModelProvider {
	public String localModelHandle = "";
	public FileHandle modelHandle = null;
	public ModelInstance modelInstance;
	private Vector3 position = new Vector3();
	private Vector3 rotation = new Vector3();
	private Vector3 scale = new Vector3(1, 1, 1);
	
	public ElementModel() { this("New model"); }
	public ElementModel(String name) {
		this.displayName = name;
		this.modelInstance = new ModelInstance(ResourceManager.standartModel);
	}

	public IModelProvider applyTransforms() {
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
	
	public boolean isTransformSupported(GizmoTransformType transformType) { return true; }
	public RenderableProvider getDefaultModel() { return this.modelInstance; }
	public SceneAsset getGLTFModel() { return null; }
}
