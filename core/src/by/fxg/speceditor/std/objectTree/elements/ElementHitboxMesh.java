package by.fxg.speceditor.std.objectTree.elements;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.utils.Array;

import by.fxg.speceditor.DefaultResources;
import by.fxg.speceditor.project.assets.IProjectAssetHandler;
import by.fxg.speceditor.project.assets.ProjectAsset;
import by.fxg.speceditor.render.DebugDraw3D;
import by.fxg.speceditor.render.DebugDraw3D.IDebugDraw;
import by.fxg.speceditor.std.gizmos.GizmoTransformType;
import by.fxg.speceditor.std.gizmos.ITreeElementGizmos;
import by.fxg.speceditor.std.objectTree.SpecObjectTree;
import by.fxg.speceditor.std.objectTree.TreeElement;
import by.fxg.speceditor.std.ui.SpecInterface.UColor;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ElementHitboxMesh extends TreeElementHitbox implements ITreeElementGizmos, IDebugDraw, IProjectAssetHandler {
	public ProjectAsset modelAsset;
	/** Nodes for generating hitbox. <br>
	 * null - not generate, int[] with 0 length - generate from all nodes, in other case use id's from the array **/
	public boolean[] nodes = new boolean[0];
	public Model model;
	private btCollisionShape shape = null;
	
	private Vector3 position = new Vector3();
	private Vector3 rotation = new Vector3();
	private Vector3 scale = new Vector3(1, 1, 1);
	
	public ElementHitboxMesh() { this("New hitbox mesh"); }
	public ElementHitboxMesh(String name) {
		this.displayName = name;
		this.setNewModel(DefaultResources.INSTANCE.standardModel);
	}
	
	public void generateMesh() { this.generateMesh(this.nodes); }
	public void generateMesh(boolean[] nodes) {
		if (this.shape != null) this.shape.release();
		if (this.model != null && nodes != null) {
			if (nodes.length == 0) {
				this.shape = Bullet.obtainStaticNodeShape(this.model.nodes);
			} else {
				Array<Node> modelNodes = new Array<>();
				for (int i = 0; i != nodes.length && i < this.model.nodes.size; i++) {
					if (nodes[i]) modelNodes.add(this.model.nodes.get(i));
				}
				this.shape = Bullet.obtainStaticNodeShape(modelNodes);
			}
		}
	}
	
	public void draw(SpecObjectTree objectTree, DebugDraw3D draw) {
		if (this.shape == null) return;
		boolean isSelected = objectTree.elementSelector.isElementSelected(this);
		if (this.parent instanceof ElementHitboxStack) {
			tmpMatrix.setToTranslation(parent.getOffsetTransform(GizmoTransformType.TRANSLATE));
			tmpMatrix.translate(this.position);
			tmpMatrix.rotate(1F, 0F, 0F, parent.getOffsetTransform(GizmoTransformType.ROTATE).x);
			tmpMatrix.rotate(0F, 1F, 0F, parent.getOffsetTransform(GizmoTransformType.ROTATE).y);
			tmpMatrix.rotate(0F, 0F, 1F, parent.getOffsetTransform(GizmoTransformType.ROTATE).z);
			tmpMatrix.rotate(1F, 0F, 0F, this.rotation.x);
			tmpMatrix.rotate(0F, 1F, 0F, this.rotation.y);
			tmpMatrix.rotate(0F, 0F, 1F, this.rotation.z);
			tmpMatrix.scale(parent.getOffsetTransform(GizmoTransformType.SCALE).x, parent.getOffsetTransform(GizmoTransformType.SCALE).y, parent.getOffsetTransform(GizmoTransformType.SCALE).z);
			tmpMatrix.scale(this.scale.x, this.scale.y, this.scale.z);
			if (!isSelected) isSelected = objectTree.elementSelector.isElementSelected(this.parent);
		} else {
			tmpMatrix.setToTranslation(this.position);
			tmpMatrix.rotate(1F, 0F, 0F, this.rotation.x);
			tmpMatrix.rotate(0F, 1F, 0F, this.rotation.y);
			tmpMatrix.rotate(0F, 0F, 1F, this.rotation.z);
			tmpMatrix.scale(this.scale.x, this.scale.y, this.scale.z);
		}
		draw.world.debugDrawObject(tmpMatrix, this.shape, isSelected ? UColor.hitboxSelected : UColor.hitbox);
	}

	public Vector3 getTransform(GizmoTransformType transformType) {
		switch(transformType) {
			case TRANSLATE: return this.position;
			case ROTATE: return this.rotation;
			case SCALE: return this.scale;
			default: return Vector3.Zero;
		}
	}
	
	public void onAssetHandlerAdded(ProjectAsset asset) {
		if (this.modelAsset != null) this.modelAsset.removeHandlerWithoutNotify(this);
		this.modelAsset = asset;
		this.onAssetLoad(asset);
	}
	
	public void onAssetLoad(ProjectAsset asset) {
		Object object = asset.getAsset();
		if (object instanceof SceneAsset) { //gltf model
			this.setNewModel(((SceneAsset)object).scene.model);
		} else if (object instanceof Model) { //default model
			this.setNewModel((Model)object);
		}
	}
	
	public void onAssetUnload(ProjectAsset asset) {
		this.setNewModel(null);
	}
	
	public void onAssetHandlerRemoved(ProjectAsset asset) {
		this.modelAsset = null;
		this.setNewModel(null);
	}
	
	public TreeElement cloneElement() {
		ElementHitboxMesh elementHitboxMesh = new ElementHitboxMesh(this.getName());
		elementHitboxMesh.specFlags = this.specFlags;
		elementHitboxMesh.bulletFlags = this.bulletFlags;
		elementHitboxMesh.bulletFilterMask = this.bulletFilterMask;
		elementHitboxMesh.bulletFilterGroup = this.bulletFilterGroup;
		System.arraycopy(this.linkFlagsToParent, 0, elementHitboxMesh.linkFlagsToParent, 0, this.linkFlagsToParent.length);
		if (this.modelAsset != null) this.modelAsset.addHandler(elementHitboxMesh);
		elementHitboxMesh.nodes = new boolean[this.nodes.length];
		System.arraycopy(this.nodes, 0, elementHitboxMesh.nodes, 0, this.nodes.length);
		elementHitboxMesh.position.set(this.position);
		elementHitboxMesh.rotation.set(this.rotation);
		elementHitboxMesh.scale.set(this.scale);
		return elementHitboxMesh;
	}
	
	public void onDelete() {
		if (this.modelAsset != null) {
			this.modelAsset.removeHandlerWithoutNotify(this);
		}
	}
	
	private void setNewModel(Model model) {
		this.model = model == null ? DefaultResources.INSTANCE.standardModel : model;
		this.nodes = new boolean[this.model.nodes.size];
		for (int i = 0; i != this.nodes.length; i++) this.nodes[i] = true;
		this.generateMesh();
	}
}
