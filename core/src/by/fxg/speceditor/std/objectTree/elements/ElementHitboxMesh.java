package by.fxg.speceditor.std.objectTree.elements;

import java.util.Arrays;

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
	
	private ElementHitboxMesh(ElementHitboxMesh copy) {
		this.displayName = copy.displayName;
		this.visible = copy.visible;
		this.specFlags = copy.specFlags;
		this.bulletFlags = copy.bulletFlags;
		this.bulletFilterMask = copy.bulletFilterMask;
		this.bulletFilterGroup = copy.bulletFilterGroup;
		this.linkFlagsToParent = Arrays.copyOf(copy.linkFlagsToParent, copy.linkFlagsToParent.length);
		if (copy.modelAsset != null) {
			copy.modelAsset.addHandler(this);
			this.nodes = Arrays.copyOf(copy.nodes, copy.nodes.length);
			this.generateMesh();
		}
		this.position.set(copy.position);
		this.rotation.set(copy.rotation);
		this.scale.set(copy.scale);
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
		tmpMatrix.setToTranslation(this.getOffsetTransform(tmpVector.setZero(), GizmoTransformType.TRANSLATE).add(this.position));
		this.getOffsetTransform(tmpVector.setZero(), GizmoTransformType.ROTATE).add(this.rotation);
		tmpMatrix.rotate(1, 0, 0, tmpVector.x).rotate(0, 1, 0, tmpVector.y).rotate(0, 1, 1, tmpVector.z);
		this.getOffsetTransform(tmpVector.set(1, 1, 1), GizmoTransformType.SCALE).scl(this.scale);
		tmpMatrix.scale(tmpVector.x, tmpVector.y, tmpVector.z);
		draw.world.debugDrawObject(tmpMatrix, this.shape, objectTree.elementSelector.isElementOrParentsSelected(this) ? UColor.hitboxSelected : UColor.hitbox);
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
		return new ElementHitboxMesh(this);
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
