package by.fxg.speceditor.std.objectTree.elements;

import java.util.Arrays;

import com.badlogic.gdx.math.Vector3;

import by.fxg.speceditor.render.DebugDraw3D;
import by.fxg.speceditor.render.DebugDraw3D.IDebugDraw;
import by.fxg.speceditor.std.gizmos.GizmoTransformType;
import by.fxg.speceditor.std.gizmos.ITreeElementGizmos;
import by.fxg.speceditor.std.objectTree.SpecObjectTree;
import by.fxg.speceditor.std.objectTree.TreeElement;
import by.fxg.speceditor.std.ui.SpecInterface.UColor;

public class ElementHitbox extends TreeElementHitbox implements ITreeElementGizmos, IDebugDraw {
	private Vector3 position = new Vector3();
	private Vector3 rotation = new Vector3();
	private Vector3 scale = new Vector3(1, 1, 1);
	
	public ElementHitbox() { this("New hitbox"); }
	public ElementHitbox(String name) {
		this.displayName = name;
	}
	
	private ElementHitbox(ElementHitbox copy) {
		this.displayName = copy.displayName;
		this.visible = copy.visible;
		this.specFlags = copy.specFlags;
		this.btCollisionFlags = copy.btCollisionFlags;
		this.btFilterMask = copy.btFilterMask;
		this.btFilterGroup = copy.btFilterGroup;
		this.linkToParent = Arrays.copyOf(copy.linkToParent, copy.linkToParent.length);
		this.position.set(copy.position);
		this.rotation.set(copy.rotation);
		this.scale.set(copy.scale);
	}
	
	public void draw(SpecObjectTree objectTree, DebugDraw3D draw) {
		tmpVectorMin.set(-0.5F, -0.5F, -0.5F);
		tmpVectorMax.set(0.5F, 0.5F, 0.5F);
		tmpMatrix.setToTranslation(this.getOffsetTransform(tmpVector.setZero(), GizmoTransformType.TRANSLATE).add(this.position));
		this.getOffsetTransform(tmpVector.setZero(), GizmoTransformType.ROTATE).add(this.rotation);
		tmpMatrix.rotate(1, 0, 0, tmpVector.x).rotate(0, 1, 0, tmpVector.y).rotate(0, 1, 1, tmpVector.z);
		this.getOffsetTransform(tmpVector.set(1, 1, 1), GizmoTransformType.SCALE).scl(this.scale);
		tmpVectorMin.scl(tmpVector.x, tmpVector.y, tmpVector.z);
		tmpVectorMax.scl(tmpVector.x, tmpVector.y, tmpVector.z);
		draw.drawer.drawBox(tmpVectorMin, tmpVectorMax, tmpMatrix, objectTree.elementSelector.isElementOrParentsSelected(this) ? UColor.hitboxSelected : UColor.hitbox);
	}
	
	public Vector3 getTransform(GizmoTransformType transformType) {
		switch(transformType) {
			case TRANSLATE: return this.position;
			case ROTATE: return this.rotation;
			case SCALE: return this.scale;
			default: return Vector3.Zero;
		}
	}
	
	public TreeElement cloneElement() {
		return new ElementHitbox(this);
	}
}
